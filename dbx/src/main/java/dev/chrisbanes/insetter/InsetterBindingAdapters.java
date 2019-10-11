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
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.BindingAdapter;

public class InsetterBindingAdapters {

  private InsetterBindingAdapters() {}

  @BindingAdapter(
      value = {
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        "paddingLeftGestureInsets",
        "paddingTopGestureInsets",
        "paddingRightGestureInsets",
        "paddingBottomGestureInsets",
        "marginLeftSystemWindowInsets",
        "marginTopSystemWindowInsets",
        "marginRightSystemWindowInsets",
        "marginBottomSystemWindowInsets",
        "marginLeftGestureInsets",
        "marginTopGestureInsets",
        "marginRightGestureInsets",
        "marginBottomGestureInsets"
      },
      requireAll = false)
  public static void applySystemWindows(
      @NonNull final View v,
      final boolean padSystemWindowLeft,
      final boolean padSystemWindowTop,
      final boolean padSystemWindowRight,
      final boolean padSystemWindowBottom,
      final boolean padGestureLeft,
      final boolean padGestureTop,
      final boolean padGestureRight,
      final boolean padGestureBottom,
      final boolean marginSystemWindowLeft,
      final boolean marginSystemWindowTop,
      final boolean marginSystemWindowRight,
      final boolean marginSystemWindowBottom,
      final boolean marginGestureLeft,
      final boolean marginGestureTop,
      final boolean marginGestureRight,
      final boolean marginGestureBottom) {

    InsetUtils.setOnApplyInsetsListener(
        v,
        new OnApplyInsetsListener() {
          @Override
          void onInsetsListener(
              @NonNull WindowInsetsCompat insets,
              @NonNull ViewDimensions initialPadding,
              @NonNull ViewDimensions initialMargin) {

            int paddingLeft = 0;
            if (padGestureLeft) {
              paddingLeft = insets.getSystemGestureInsets().left;
            } else if (padSystemWindowLeft) {
              paddingLeft = insets.getSystemWindowInsetLeft();
            }

            int paddingTop = 0;
            if (padGestureTop) {
              paddingTop = insets.getSystemGestureInsets().top;
            } else if (padSystemWindowTop) {
              paddingTop = insets.getSystemWindowInsetTop();
            }

            int paddingRight = 0;
            if (padGestureRight) {
              paddingRight = insets.getSystemGestureInsets().right;
            } else if (padSystemWindowRight) {
              paddingRight = insets.getSystemWindowInsetRight();
            }

            int paddingBottom = 0;
            if (padGestureBottom) {
              paddingBottom = insets.getSystemGestureInsets().bottom;
            } else if (padSystemWindowBottom) {
              paddingBottom = insets.getSystemWindowInsetBottom();
            }

            v.setPadding(
                initialPadding.getLeft() + paddingLeft,
                initialPadding.getTop() + paddingTop,
                initialPadding.getRight() + paddingRight,
                initialPadding.getBottom() + paddingBottom);

            final boolean marginInsetRequested =
                marginSystemWindowLeft
                    || marginGestureLeft
                    || marginSystemWindowTop
                    || marginGestureTop
                    || marginSystemWindowRight
                    || marginGestureRight
                    || marginSystemWindowBottom
                    || marginGestureBottom;
            if (marginInsetRequested) {
              int marginLeft = 0;
              if (padGestureLeft) {
                marginLeft = insets.getSystemGestureInsets().left;
              } else if (padSystemWindowLeft) {
                marginLeft = insets.getSystemWindowInsetLeft();
              }

              int marginTop = 0;
              if (padGestureTop) {
                marginTop = insets.getSystemGestureInsets().top;
              } else if (padSystemWindowTop) {
                marginTop = insets.getSystemWindowInsetTop();
              }

              int marginRight = 0;
              if (padGestureRight) {
                marginRight = insets.getSystemGestureInsets().right;
              } else if (padSystemWindowRight) {
                marginRight = insets.getSystemWindowInsetRight();
              }

              int marginBottom = 0;
              if (padGestureBottom) {
                marginBottom = insets.getSystemGestureInsets().bottom;
              } else if (padSystemWindowBottom) {
                marginBottom = insets.getSystemWindowInsetBottom();
              }

              final ViewGroup.LayoutParams lp = v.getLayoutParams();
              if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.leftMargin = initialMargin.getLeft() + marginLeft;
                mlp.topMargin = initialMargin.getTop() + marginTop;
                mlp.rightMargin = initialMargin.getRight() + marginRight;
                mlp.bottomMargin = initialMargin.getBottom() + marginBottom;
                v.setLayoutParams(mlp);
              } else {
                throw new IllegalArgumentException(
                    "Margin inset handling requested but view LayoutParams do not"
                        + " extend MarginLayoutParams");
              }
            }
          }
        });
  }
}
