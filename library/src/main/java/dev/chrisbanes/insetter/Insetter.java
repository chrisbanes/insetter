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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.EnumSet;
import java.util.Locale;

/** A collection of utility functions to make handling {@link android.view.WindowInsets} easier. */
public class Insetter {

  static final String TAG = "Insetter";

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
              v.removeOnAttachStateChangeListener(this);
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
   * A convenience function which applies insets to a view.
   *
   * <p>How the given insets are applied depends on the options provided via the various parameters.
   * Each of {@code paddingSystemWindowInsets}, {@code marginSystemWindowInsets}, {@code
   * paddingSystemGestureInsets} and {@code marginSystemGestureInsets} take an {@link EnumSet} of
   * {@link InsetDimension} values.
   *
   * @param view the view to apply inset handling too
   * @param insets the insets to apply
   * @param initialState the initial view state of the view
   * @param paddingSystemWindowInsets enum set defining padding handling of system window insets
   * @param marginSystemWindowInsets enum set defining margin handling of system window insets
   * @param paddingSystemGestureInsets enum set defining padding handling of system gesture insets
   * @param marginSystemGestureInsets enum set defining margin handling of system gesture insets
   */
  public static void applyInsetsToView(
      @NonNull final View view,
      @NonNull final WindowInsetsCompat insets,
      @NonNull final ViewState initialState,
      @Nullable final EnumSet<InsetDimension> paddingSystemWindowInsets,
      @Nullable final EnumSet<InsetDimension> marginSystemWindowInsets,
      @Nullable final EnumSet<InsetDimension> paddingSystemGestureInsets,
      @Nullable final EnumSet<InsetDimension> marginSystemGestureInsets) {

    final Insets systemWindowInsets = insets.getSystemWindowInsets();
    final Insets systemGestureInsets = insets.getSystemGestureInsets();

    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(
          TAG,
          String.format(
              Locale.US,
              "applyInsetsToView. View: %s. Insets: %s. State: %s",
              view,
              insets,
              initialState));
    }

    final ViewDimensions initialPadding = initialState.getPaddings();
    int paddingLeft = view.getPaddingLeft();
    if (paddingSystemGestureInsets != null
        && paddingSystemGestureInsets.contains(InsetDimension.LEFT)) {
      paddingLeft = initialPadding.getLeft() + systemGestureInsets.left;
    } else if (paddingSystemWindowInsets != null
        && paddingSystemWindowInsets.contains(InsetDimension.LEFT)) {
      paddingLeft = initialPadding.getLeft() + systemWindowInsets.left;
    }

    int paddingTop = view.getPaddingTop();
    if (paddingSystemGestureInsets != null
        && paddingSystemGestureInsets.contains(InsetDimension.TOP)) {
      paddingTop = initialPadding.getTop() + systemGestureInsets.top;
    } else if (paddingSystemWindowInsets != null
        && paddingSystemWindowInsets.contains(InsetDimension.TOP)) {
      paddingTop = initialPadding.getTop() + systemWindowInsets.top;
    }

    int paddingRight = view.getPaddingRight();
    if (paddingSystemGestureInsets != null
        && paddingSystemGestureInsets.contains(InsetDimension.RIGHT)) {
      paddingRight = initialPadding.getRight() + systemGestureInsets.right;
    } else if (paddingSystemWindowInsets != null
        && paddingSystemWindowInsets.contains(InsetDimension.RIGHT)) {
      paddingRight = initialPadding.getRight() + systemWindowInsets.right;
    }

    int paddingBottom = view.getPaddingBottom();
    if (paddingSystemGestureInsets != null
        && paddingSystemGestureInsets.contains(InsetDimension.BOTTOM)) {
      paddingBottom = initialPadding.getBottom() + systemGestureInsets.bottom;
    } else if (paddingSystemWindowInsets != null
        && paddingSystemWindowInsets.contains(InsetDimension.BOTTOM)) {
      paddingBottom = initialPadding.getBottom() + systemWindowInsets.bottom;
    }

    view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(
          TAG,
          String.format(
              Locale.US,
              "applyInsetsToView. Applied padding to %s: left=%d, top=%d, right=%d, bottom=%d}",
              view,
              paddingLeft,
              paddingTop,
              paddingRight,
              paddingBottom));
    }

    // Now we can deal with margins

    final ViewGroup.LayoutParams lp = view.getLayoutParams();
    if (lp instanceof ViewGroup.MarginLayoutParams) {
      final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
      final ViewDimensions initialMargins = initialState.getMargins();

      int marginLeft = mlp.leftMargin;
      if (marginSystemGestureInsets != null
          && marginSystemGestureInsets.contains(InsetDimension.LEFT)) {
        marginLeft = initialMargins.getLeft() + systemGestureInsets.left;
      } else if (marginSystemWindowInsets != null
          && marginSystemWindowInsets.contains(InsetDimension.LEFT)) {
        marginLeft = initialMargins.getLeft() + systemWindowInsets.left;
      }

      int marginTop = mlp.topMargin;
      if (marginSystemGestureInsets != null
          && marginSystemGestureInsets.contains(InsetDimension.TOP)) {
        marginTop = initialMargins.getTop() + systemGestureInsets.top;
      } else if (marginSystemWindowInsets != null
          && marginSystemWindowInsets.contains(InsetDimension.TOP)) {
        marginTop = initialMargins.getTop() + systemWindowInsets.top;
      }

      int marginRight = mlp.rightMargin;
      if (marginSystemGestureInsets != null
          && marginSystemGestureInsets.contains(InsetDimension.RIGHT)) {
        marginRight = initialMargins.getRight() + systemGestureInsets.right;
      } else if (marginSystemWindowInsets != null
          && marginSystemWindowInsets.contains(InsetDimension.RIGHT)) {
        marginRight = initialMargins.getRight() + systemWindowInsets.right;
      }

      int marginBottom = mlp.bottomMargin;
      if (marginSystemGestureInsets != null
          && marginSystemGestureInsets.contains(InsetDimension.BOTTOM)) {
        marginBottom = initialMargins.getBottom() + systemGestureInsets.bottom;
      } else if (marginSystemWindowInsets != null
          && marginSystemWindowInsets.contains(InsetDimension.BOTTOM)) {
        marginBottom = initialMargins.getBottom() + systemWindowInsets.bottom;
      }

      if (mlp.leftMargin != marginLeft
          || mlp.topMargin != marginTop
          || mlp.rightMargin != marginRight
          || mlp.bottomMargin != marginBottom) {
        mlp.leftMargin = marginLeft;
        mlp.topMargin = marginTop;
        mlp.rightMargin = marginRight;
        mlp.bottomMargin = marginBottom;
        view.setLayoutParams(lp);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(
              TAG,
              String.format(
                  Locale.US,
                  "applyInsetsToView. Applied margin to %s: left=%d, top=%d, right=%d, bottom=%d}",
                  view,
                  marginLeft,
                  marginTop,
                  marginRight,
                  marginBottom));
        }
      }
    } else if ((marginSystemGestureInsets != null && !marginSystemGestureInsets.isEmpty())
        || (marginSystemWindowInsets != null && !marginSystemWindowInsets.isEmpty())) {
      throw new IllegalArgumentException(
          "Margin inset handling requested but view LayoutParams do not"
              + " extend MarginLayoutParams");
    }
  }

  /**
   * @deprecated this method will be removed in a future version (before v1.0). Please use {@link
   *     #applyInsetsToView(View, WindowInsetsCompat, ViewState, EnumSet, EnumSet, EnumSet,
   *     EnumSet)} instead.
   */
  @Deprecated
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
        generateEnumSet(
            paddingSystemWindowLeft,
            paddingSystemWindowTop,
            paddingSystemWindowRight,
            paddingSystemWindowBottom),
        generateEnumSet(
            marginSystemWindowLeft,
            marginSystemWindowTop,
            marginSystemWindowRight,
            marginSystemWindowBottom),
        generateEnumSet(
            paddingSystemGestureLeft,
            paddingSystemGestureTop,
            paddingSystemGestureRight,
            paddingSystemGestureBottom),
        generateEnumSet(
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

  @Nullable
  static EnumSet<InsetDimension> generateEnumSet(
      boolean left, boolean top, boolean right, boolean bottom) {
    if (!left && !top && !right && !bottom) {
      // Fast path if all dimensions are disabled
      return null;
    }
    final EnumSet<InsetDimension> set = EnumSet.noneOf(InsetDimension.class);
    if (left) set.add(InsetDimension.LEFT);
    if (top) set.add(InsetDimension.TOP);
    if (right) set.add(InsetDimension.RIGHT);
    if (bottom) set.add(InsetDimension.BOTTOM);
    return set;
  }
}
