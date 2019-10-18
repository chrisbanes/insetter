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
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** A collection of utility functions to make handling {@link android.view.WindowInsets} easier. */
public class Insetter {

  private Insetter() {
    // private constructor. No instantiating.
  }

  /**
   * A wrapper around {@link ViewCompat#setOnApplyWindowInsetsListener(View,
   * OnApplyWindowInsetsListener)} which stores the initial view state, and provides them whenever a
   * {@link android.view.WindowInsets} instance is dispatched to the listener provided.
   *
   * <p>This allows the listener to be able to append inset values to any existing view state
   * properties, rather than overwriting them.
   */
  public static void setOnApplyInsetsListener(
      @NonNull View view, @NonNull final OnApplyInsetsListener listener) {

    final ViewState tagState = (ViewState) view.getTag(R.id.insetter_initial_state);

    final ViewState initialState;
    if (tagState != null) {
      initialState = tagState;
    } else {
      initialState = new ViewState(view);
      view.setTag(R.id.insetter_initial_state, initialState);
    }

    ViewCompat.setOnApplyWindowInsetsListener(
        view,
        new OnApplyWindowInsetsListener() {
          @Override
          public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            listener.onApplyInsets(v, insets, initialState);
            // Always return the initial insets instance
            return insets;
          }
        });

    // Now request an insets pass
    requestApplyInsetsWhenAttached(view);
  }

  /**
   * A wrapper around {@link ViewCompat#requestApplyInsets(View)} which ensures the request will
   * happen, regardless of whether the view is attached or not.
   */
  public static void requestApplyInsetsWhenAttached(@NonNull final View view) {
    if (ViewCompat.isAttachedToWindow(view)) {
      // If the view is already attached, we can request a pass
      ViewCompat.requestApplyInsets(view);
    } else {
      // If the view is not attached, calls to requestApplyInsets() will be ignored.
      // We can just wait until the view is attached before requesting.
      view.addOnAttachStateChangeListener(
          new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
              ViewCompat.requestApplyInsets(v);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
              // no-op
            }
          });
    }
  }

  /**
   * TODO
   *
   * @param view
   * @param insets
   * @param initialState
   * @param paddingSystemWindowLeft
   * @param paddingSystemWindowTop
   * @param paddingSystemWindowRight
   * @param paddingSystemWindowBottom
   * @param paddingSystemGestureLeft
   * @param paddingSystemGestureTop
   * @param paddingSystemGestureRight
   * @param paddingSystemGestureBottom
   * @param marginSystemWindowLeft
   * @param marginSystemWindowTop
   * @param marginSystemWindowRight
   * @param marginSystemWindowBottom
   * @param marginGestureLeft
   * @param marginGestureTop
   * @param marginGestureRight
   * @param marginGestureBottom
   */
  public static void applyInsetsToView(
      @NonNull final View view,
      @NonNull final WindowInsetsCompat insets,
      @NonNull final ViewState initialState,
      final boolean paddingSystemWindowLeft,
      final boolean paddingSystemWindowTop,
      final boolean paddingSystemWindowRight,
      final boolean paddingSystemWindowBottom,
      final boolean paddingSystemGestureLeft,
      final boolean paddingSystemGestureTop,
      final boolean paddingSystemGestureRight,
      final boolean paddingSystemGestureBottom,
      final boolean marginSystemWindowLeft,
      final boolean marginSystemWindowTop,
      final boolean marginSystemWindowRight,
      final boolean marginSystemWindowBottom,
      final boolean marginGestureLeft,
      final boolean marginGestureTop,
      final boolean marginGestureRight,
      final boolean marginGestureBottom) {
    final ViewDimensions initialPadding = initialState.getPaddings();
    int paddingLeft = initialPadding.getLeft();
    if (paddingSystemGestureLeft) {
      paddingLeft += insets.getSystemGestureInsets().left;
    } else if (paddingSystemWindowLeft) {
      paddingLeft += insets.getSystemWindowInsetLeft();
    }

    int paddingTop = initialPadding.getTop();
    if (paddingSystemGestureTop) {
      paddingTop += insets.getSystemGestureInsets().top;
    } else if (paddingSystemWindowTop) {
      paddingTop += insets.getSystemWindowInsetTop();
    }

    int paddingRight = initialPadding.getRight();
    if (paddingSystemGestureRight) {
      paddingRight += insets.getSystemGestureInsets().right;
    } else if (paddingSystemWindowRight) {
      paddingRight += insets.getSystemWindowInsetRight();
    }

    int paddingBottom = initialPadding.getBottom();
    if (paddingSystemGestureBottom) {
      paddingBottom += insets.getSystemGestureInsets().bottom;
    } else if (paddingSystemWindowBottom) {
      paddingBottom += insets.getSystemWindowInsetBottom();
    }

    view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

    // Now we can deal with margins

    final ViewDimensions initialMargins = initialState.getMargins();

    final boolean marginInsetRequested =
        marginSystemWindowLeft
            || marginGestureLeft
            || marginSystemWindowTop
            || marginGestureTop
            || marginSystemWindowRight
            || marginGestureRight
            || marginSystemWindowBottom
            || marginGestureBottom;

    int marginLeft = initialMargins.getLeft();
    if (marginGestureLeft) {
      marginLeft += insets.getSystemGestureInsets().left;
    } else if (marginSystemWindowLeft) {
      marginLeft += insets.getSystemWindowInsetLeft();
    }

    int marginTop = initialMargins.getTop();
    if (marginGestureTop) {
      marginTop += insets.getSystemGestureInsets().top;
    } else if (marginSystemWindowTop) {
      marginTop += insets.getSystemWindowInsetTop();
    }

    int marginRight = initialMargins.getRight();
    if (marginGestureRight) {
      marginRight += insets.getSystemGestureInsets().right;
    } else if (marginSystemWindowRight) {
      marginRight += insets.getSystemWindowInsetRight();
    }

    int marginBottom = initialMargins.getBottom();
    if (marginGestureBottom) {
      marginBottom += insets.getSystemGestureInsets().bottom;
    } else if (marginSystemWindowBottom) {
      marginBottom += insets.getSystemWindowInsetBottom();
    }

    final ViewGroup.LayoutParams lp = view.getLayoutParams();
    if (lp instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
      if (mlp.leftMargin != marginLeft
          || mlp.topMargin != marginTop
          || mlp.rightMargin != marginRight
          || mlp.bottomMargin != marginBottom) {
        mlp.leftMargin = marginLeft;
        mlp.topMargin = marginTop;
        mlp.rightMargin = marginRight;
        mlp.bottomMargin = marginBottom;
        view.setLayoutParams(lp);
      }
    } else if (marginInsetRequested) {
      throw new IllegalArgumentException(
          "Margin inset handling requested but view LayoutParams do not"
              + " extend MarginLayoutParams");
    }
  }
}
