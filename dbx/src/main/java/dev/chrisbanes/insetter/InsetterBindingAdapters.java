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

import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.BindingAdapter;

public class InsetterBindingAdapters {
  private static final String TAG = "Insetter";

  private InsetterBindingAdapters() {}

  @BindingAdapter(
      value = {
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        "paddingLeftSystemGestureInsets",
        "paddingTopSystemGestureInsets",
        "paddingRightSystemGestureInsets",
        "paddingBottomSystemGestureInsets",
        "layout_marginLeftSystemWindowInsets",
        "layout_marginTopSystemWindowInsets",
        "layout_marginRightSystemWindowInsets",
        "layout_marginBottomSystemWindowInsets",
        "layout_marginLeftSystemGestureInsets",
        "layout_marginTopSystemGestureInsets",
        "layout_marginRightSystemGestureInsets",
        "layout_marginBottomSystemGestureInsets"
      },
      requireAll = false)
  public static void applyInsetsFromBooleans(
      @NonNull final View v,
      final boolean padSystemWindowLeft,
      final boolean padSystemWindowTop,
      final boolean padSystemWindowRight,
      final boolean padSystemWindowBottom,
      final boolean padSystemGestureLeft,
      final boolean padSystemGestureTop,
      final boolean padSystemGestureRight,
      final boolean padSystemGestureBottom,
      final boolean marginSystemWindowLeft,
      final boolean marginSystemWindowTop,
      final boolean marginSystemWindowRight,
      final boolean marginSystemWindowBottom,
      final boolean marginSystemGestureLeft,
      final boolean marginSystemGestureTop,
      final boolean marginSystemGestureRight,
      final boolean marginSystemGestureBottom) {
    Insetter.setOnApplyInsetsListener(
        v,
        new OnApplyInsetsListener() {
          @Override
          public void onApplyInsets(
              @NonNull View view,
              @NonNull WindowInsetsCompat insets,
              @NonNull ViewState initialState) {
            Insetter.applyInsetsToView(
                view,
                insets,
                initialState,
                Insetter.generateEnumSet(
                    padSystemWindowLeft,
                    padSystemWindowTop,
                    padSystemWindowRight,
                    padSystemWindowBottom),
                Insetter.generateEnumSet(
                    marginSystemWindowLeft,
                    marginSystemWindowTop,
                    marginSystemWindowRight,
                    marginSystemWindowBottom),
                Insetter.generateEnumSet(
                    padSystemGestureLeft,
                    padSystemGestureTop,
                    padSystemGestureRight,
                    padSystemGestureBottom),
                Insetter.generateEnumSet(
                    marginSystemGestureLeft,
                    marginSystemGestureTop,
                    marginSystemGestureRight,
                    marginSystemGestureBottom));
          }
        });
  }

  @BindingAdapter(
            value = {
                    "marginBottomForKeyboard",
                    "paddingBottomForKeyboard"
            },
            requireAll = false)
  public static void applyKeyboardInsetsFromBooleans(
            @NonNull final View v,
            final boolean marginBottomForKeyboard,
            final boolean paddingBottomForKeyboard) {
      Insetter.applyInsetsWhenKeyboardDisplayed(
              v,
              marginBottomForKeyboard,
              paddingBottomForKeyboard);
  }

  @BindingAdapter("layout_edgeToEdge")
  public static void setEdgeToEdgeFlags(@NonNull final View view, boolean enabled) {
    if (Build.VERSION.SDK_INT >= 16) {
      Insetter.setEdgeToEdgeSystemUiFlags(view, enabled);
    } else {
      Log.i(TAG, "The layout_edgeToEdge attribute only works on API 16+");
    }
  }
}
