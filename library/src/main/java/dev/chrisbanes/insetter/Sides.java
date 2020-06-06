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

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * A class specifying the sides of a {@link android.view.View} on which the relevant insets should
 * be applied.
 */
public final class Sides {

  @IntDef(
      flag = true,
      value = {LEFT, TOP, RIGHT, BOTTOM})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Side {}

  public static final int LEFT = 1;

  public static final int TOP = 1 << 1;

  public static final int RIGHT = 1 << 2;

  public static final int BOTTOM = 1 << 3;

  @Side
  public static int create(boolean left, boolean top, boolean right, boolean bottom) {
    return (left ? LEFT : 0) | (top ? TOP : 0) | (right ? RIGHT : 0) | (bottom ? BOTTOM : 0);
  }

  protected static boolean hasSide(int sides, @Side int flag) {
    return (sides & flag) == flag;
  }

  private static String toString(@Side int sides) {
    return String.format(
        Locale.US,
        "Sides{left=%b, top=%b, right=%b, bottom=%b}",
        hasSide(sides, Sides.LEFT),
        hasSide(sides, Sides.TOP),
        hasSide(sides, Sides.RIGHT),
        hasSide(sides, Sides.BOTTOM));
  }
}
