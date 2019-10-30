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

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** A collection of utility functions to make handling {@link android.view.WindowInsets} easier. */
public class Insetter {

  /** @hide */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(
      value = {LEFT, TOP, RIGHT, BOTTOM, NONE},
      flag = true)
  public @interface InsetDimension {}

  public static final int LEFT = Gravity.LEFT;
  public static final int TOP = Gravity.TOP;
  public static final int RIGHT = Gravity.RIGHT;
  public static final int BOTTOM = Gravity.BOTTOM;

  public static final int ALL = LEFT | TOP | RIGHT | BOTTOM;
  public static final int HORIZONTAL = LEFT | RIGHT;
  public static final int VERTICAL = TOP | BOTTOM;
  public static final int NONE = Gravity.NO_GRAVITY;

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
   * @param view
   * @param insets
   * @param initialState
   * @param paddingSystemWindowInsets
   * @param marginSystemWindowInsets
   * @param paddingSystemGestureInsets
   * @param marginSystemGestureInsets
   */
  public static void applyInsetsToView(
      @NonNull final View view,
      @NonNull final WindowInsetsCompat insets,
      @NonNull final ViewState initialState,
      final int paddingSystemWindowInsets,
      final int marginSystemWindowInsets,
      final int paddingSystemGestureInsets,
      final int marginSystemGestureInsets) {
    final ViewDimensions initialPadding = initialState.getPaddings();
    int paddingLeft = initialPadding.getLeft();
    if (hasFlag(paddingSystemGestureInsets, LEFT)) {
      paddingLeft += insets.getSystemGestureInsets().left;
    } else if (hasFlag(paddingSystemWindowInsets, LEFT)) {
      paddingLeft += insets.getSystemWindowInsetLeft();
    }

    int paddingTop = initialPadding.getTop();
    if (hasFlag(paddingSystemGestureInsets, TOP)) {
      paddingTop += insets.getSystemGestureInsets().top;
    } else if (hasFlag(paddingSystemWindowInsets, TOP)) {
      paddingTop += insets.getSystemWindowInsetTop();
    }

    int paddingRight = initialPadding.getRight();
    if (hasFlag(paddingSystemGestureInsets, RIGHT)) {
      paddingRight += insets.getSystemGestureInsets().right;
    } else if (hasFlag(paddingSystemWindowInsets, RIGHT)) {
      paddingRight += insets.getSystemWindowInsetRight();
    }

    int paddingBottom = initialPadding.getBottom();
    if (hasFlag(paddingSystemGestureInsets, BOTTOM)) {
      paddingBottom += insets.getSystemGestureInsets().bottom;
    } else if (hasFlag(paddingSystemWindowInsets, BOTTOM)) {
      paddingBottom += insets.getSystemWindowInsetBottom();
    }

    view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

    // Now we can deal with margins

    final ViewDimensions initialMargins = initialState.getMargins();

    int marginLeft = initialMargins.getLeft();
    if (hasFlag(marginSystemGestureInsets, LEFT)) {
      marginLeft += insets.getSystemGestureInsets().left;
    } else if (hasFlag(marginSystemWindowInsets, LEFT)) {
      marginLeft += insets.getSystemWindowInsetLeft();
    }

    int marginTop = initialMargins.getTop();
    if (hasFlag(marginSystemGestureInsets, TOP)) {
      marginTop += insets.getSystemGestureInsets().top;
    } else if (hasFlag(marginSystemWindowInsets, TOP)) {
      marginTop += insets.getSystemWindowInsetTop();
    }

    int marginRight = initialMargins.getRight();
    if (hasFlag(marginSystemGestureInsets, RIGHT)) {
      marginRight += insets.getSystemGestureInsets().right;
    } else if (hasFlag(marginSystemWindowInsets, RIGHT)) {
      marginRight += insets.getSystemWindowInsetRight();
    }

    int marginBottom = initialMargins.getBottom();
    if (hasFlag(marginSystemGestureInsets, BOTTOM)) {
      marginBottom += insets.getSystemGestureInsets().bottom;
    } else if (hasFlag(marginSystemWindowInsets, BOTTOM)) {
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
    } else if (marginSystemGestureInsets != NONE || marginSystemWindowInsets != NONE) {
      throw new IllegalArgumentException(
          "Margin inset handling requested but view LayoutParams do not"
              + " extend MarginLayoutParams");
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
   * @param marginSystemGestureLeft
   * @param marginSystemGestureTop
   * @param marginSystemGestureRight
   * @param marginSystemGestureBottom
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
      final boolean marginSystemGestureLeft,
      final boolean marginSystemGestureTop,
      final boolean marginSystemGestureRight,
      final boolean marginSystemGestureBottom) {
    applyInsetsToView(
        view,
        insets,
        initialState,
        generateFlagInt(
            paddingSystemWindowLeft,
            paddingSystemWindowTop,
            paddingSystemWindowRight,
            paddingSystemWindowBottom),
        generateFlagInt(
            marginSystemWindowLeft,
            marginSystemWindowTop,
            marginSystemWindowRight,
            marginSystemWindowBottom),
        generateFlagInt(
            paddingSystemGestureLeft,
            paddingSystemGestureTop,
            paddingSystemGestureRight,
            paddingSystemGestureBottom),
        generateFlagInt(
            marginSystemGestureLeft,
            marginSystemGestureTop,
            marginSystemGestureRight,
            marginSystemGestureBottom));
  }

  /**
   * Set this view's system-ui visibility, with the flags required to be laid out 'edge-to-edge'.
   *
   * @param enabled true if the view should request to be laid out 'edge-to-edge', false if not
   * @see View#setSystemUiVisibility(int)
   */
  @RequiresApi(api = 16)
  public static void setEdgeToEdgeSystemUiFlags(@NonNull final View view, final boolean enabled) {
    view.setSystemUiVisibility(
        (view.getSystemUiVisibility() & ~EDGE_TO_EDGE_FLAGS) | (enabled ? EDGE_TO_EDGE_FLAGS : 0));
  }

  @SuppressLint("InlinedApi")
  @VisibleForTesting
  static final int EDGE_TO_EDGE_FLAGS =
      View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

  private static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }

  private static int generateFlagInt(boolean left, boolean top, boolean right, boolean bottom) {
    int v = NONE;
    if (left) v |= LEFT;
    if (top) v |= TOP;
    if (right) v |= RIGHT;
    if (bottom) v |= BOTTOM;
    return v;
  }
}
