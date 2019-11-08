/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.chrisbanes.insetter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import java.util.Locale;

public class ViewState {
  private ViewDimensions paddings;
  private ViewDimensions margins;

  public ViewState(@NonNull View view) {
    this(createPaddingDimensions(view), createMarginDimensions(view));
  }

  private ViewState(@NonNull ViewDimensions paddings, @NonNull ViewDimensions margins) {
    this.paddings = paddings;
    this.margins = margins;
  }

  public ViewDimensions getPaddings() {
    return paddings;
  }

  public ViewDimensions getMargins() {
    return margins;
  }

  @NonNull
  private static ViewDimensions createPaddingDimensions(@NonNull View view) {
    return new ViewDimensions(
        view.getPaddingLeft(),
        view.getPaddingTop(),
        view.getPaddingRight(),
        view.getPaddingBottom());
  }

  @NonNull
  private static ViewDimensions createMarginDimensions(@NonNull View view) {
    if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
      return new ViewDimensions(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, mlp.bottomMargin);
    }
    return ViewDimensions.EMPTY;
  }

  @Override
  public String toString() {
    return String.format(Locale.US, "ViewState{paddings=%s, margins=%s}", paddings, margins);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ViewState viewState = (ViewState) o;

    if (!ObjectsCompat.equals(paddings, viewState.paddings)) {
      return false;
    }
    return ObjectsCompat.equals(margins, viewState.margins);
  }

  @Override
  public int hashCode() {
    int result = paddings != null ? paddings.hashCode() : 0;
    result = 31 * result + (margins != null ? margins.hashCode() : 0);
    return result;
  }

  public static final ViewState EMPTY = new ViewState(ViewDimensions.EMPTY, ViewDimensions.EMPTY);
}
