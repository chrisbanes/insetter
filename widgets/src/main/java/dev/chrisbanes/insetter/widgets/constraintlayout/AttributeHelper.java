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

package dev.chrisbanes.insetter.widgets.constraintlayout;

import android.view.Gravity;
import androidx.annotation.Nullable;
import dev.chrisbanes.insetter.Insetter.Side;
import java.util.EnumSet;

class AttributeHelper {
  // These values match the values in attrs.xml
  private static final int LEFT = Gravity.LEFT;
  private static final int TOP = Gravity.TOP;
  private static final int RIGHT = Gravity.RIGHT;
  private static final int BOTTOM = Gravity.BOTTOM;

  @Nullable
  static EnumSet<Side> flagToEnumSet(int value) {
    // Fast path if the value is empty
    if (value == 0) return null;

    final EnumSet<Side> set = EnumSet.noneOf(Side.class);
    if (hasFlag(value, LEFT)) set.add(Side.LEFT);
    if (hasFlag(value, TOP)) set.add(Side.TOP);
    if (hasFlag(value, RIGHT)) set.add(Side.RIGHT);
    if (hasFlag(value, BOTTOM)) set.add(Side.BOTTOM);
    return set;
  }

  private static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }

  private AttributeHelper() {}
}
