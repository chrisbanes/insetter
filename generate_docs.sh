#!/bin/sh

DOCS_ROOT=docs-gen

[ -d $DOCS_ROOT ] && rm -r $DOCS_ROOT
mkdir $DOCS_ROOT

copyReadme() {
  cp $1/README.md $DOCS_ROOT/$1.md
  mkdir -p $DOCS_ROOT/$1
  [ -d "$1/images" ] && cp -r $1/images $DOCS_ROOT/$1
}

# Clear out the old API docs
[ -d docs/api ] && rm -r docs/api
# Build the docs with dokka
./gradlew clean dokkaHtmlMultiModule

# Dokka doesn't currently allow us to change the index page name so move it manually
mv docs/api/-modules.html docs/api/index.html

# Re-word the Dokka call out
find docs/api/ -type f -name '*.html' -exec sed -i -e 's/Sponsored and developed/Documentation generated/g' {} \;
# Remove the copyright declaration
find docs/api/ -type f -name '*.html' -exec sed -i -e 's/© [0-9]* Copyright//' {} \;

# Copy over any static + API docs to our $DOCS_ROOT
cp -R docs/* $DOCS_ROOT

cp CONTRIBUTING.md $DOCS_ROOT/contributing.md

sed -i.bak 's/CONTRIBUTING.md/contributing/' $DOCS_ROOT/index.md
sed -i.bak 's/README.md//' $DOCS_ROOT/index.md

# Convert docs/xxx.md links to just xxx/
sed -i.bak 's/docs\/\([a-zA-Z-]*\).md/\1/' $DOCS_ROOT/index.md

copyReadme library
copyReadme dbx
copyReadme widgets

# Finally delete all of the backup files
find . -name '*.bak' -delete
