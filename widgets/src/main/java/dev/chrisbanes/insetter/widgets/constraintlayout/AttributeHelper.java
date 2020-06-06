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
import dev.chrisbanes.insetter.SideUtils;

class AttributeHelper {
  // These values match the values in attrs.xml
  private static final int LEFT = Gravity.LEFT;
  private static final int TOP = Gravity.TOP;
  private static final int RIGHT = Gravity.RIGHT;
  private static final int BOTTOM = Gravity.BOTTOM;

  static int flagToSides(int value) {
    // Fast path if the value is empty
    if (value == 0) return 0;

    return SideUtils.create(
        hasFlag(value, LEFT), hasFlag(value, TOP), hasFlag(value, RIGHT), hasFlag(value, BOTTOM));
  }

  private static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }

  private AttributeHelper() {}
}
