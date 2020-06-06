/*
 * Copyright 2020 Google LLC
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

import java.util.Locale;

/**
 * A class specifying the sides of a {@link android.view.View} on which the relevant insets should
 * be applied.
 */
public final class SideUtils {

  @Sides
  public static int create(boolean left, boolean top, boolean right, boolean bottom) {
    return (left ? Side.LEFT : 0)
        | (top ? Side.TOP : 0)
        | (right ? Side.RIGHT : 0)
        | (bottom ? Side.BOTTOM : 0);
  }

  protected static boolean hasSide(int sides, @Sides int flag) {
    return (sides & flag) == flag;
  }

  private static String toString(@Sides int sides) {
    return String.format(
        Locale.US,
        "Sides{left=%b, top=%b, right=%b, bottom=%b}",
        hasSide(sides, Side.LEFT),
        hasSide(sides, Side.TOP),
        hasSide(sides, Side.RIGHT),
        hasSide(sides, Side.BOTTOM));
  }
}
