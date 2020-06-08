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
import java.util.Locale;

/**
 * A helper class to make handling {@link android.view.WindowInsets} easier.
 *
 * <p>It includes a {@link Builder} for building easy-to-use {@link OnApplyWindowInsetsListener}
 * instances:
 *
 * <pre>
 * Insetter.builder()
 *     // This will apply the system window insets as padding to left, bottom and right of the view
 *     .applySystemWindowInsetsToPadding(Side.LEFT | Side.BOTTOM | Side.RIGHT)
 *     // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
 *     .applyToView(view);
 * </pre>
 *
 * Each inset type as on Android 10 (API level 29) is included, with variants for applying the inset
 * as either padding or margin on the view.
 *
 * <p>You can also provide custom logic via the {@link
 * Builder#setOnApplyInsetsListener(OnApplyInsetsListener)} function. The listener type is slightly
 * different to {@link OnApplyWindowInsetsListener}, in that it contains a third parameter to tell
 * you what the initial view padding/margin state is.
 *
 * <p>By default the listener will not consume any insets which are passed to it. If you wish to
 * consume the system window insets, you can use the {@link
 * Builder#consumeSystemWindowInsets(boolean)} function.
 */
public final class Insetter {

  static final String TAG = "Insetter";

  @Nullable private OnApplyInsetsListener onApplyInsetsListener;
  private int paddingSystemWindowInsets;
  private int marginSystemWindowInsets;
  private int paddingSystemGestureInsets;
  private int marginSystemGestureInsets;
  private boolean consumeSystemWindowInsets;

  private Insetter(@NonNull Builder builder) {
    onApplyInsetsListener = builder.onApplyInsetsListener;
    paddingSystemWindowInsets = builder.paddingSystemWindowInsets;
    marginSystemWindowInsets = builder.marginSystemWindowInsets;
    paddingSystemGestureInsets = builder.paddingSystemGestureInsets;
    marginSystemGestureInsets = builder.marginSystemGestureInsets;
    consumeSystemWindowInsets = builder.consumeSystemWindowInsets;
  }

  /** A builder class for creating instances of {@link Insetter}. */
  public static final class Builder {

    @Nullable private OnApplyInsetsListener onApplyInsetsListener;
    private int paddingSystemWindowInsets;
    private int marginSystemWindowInsets;
    private int paddingSystemGestureInsets;
    private int marginSystemGestureInsets;
    private boolean consumeSystemWindowInsets;

    private Builder() {
      // private constructor.
    }

    /**
     * @param onApplyInsetsListener Callback for supplying custom logic to apply insets. If set,
     *     Insetter will ignore any specified side flags, and the caller is responsible for applying
     *     insets.
     * @see Insetter#setOnApplyInsetsListener(View)
     */
    @NonNull
    public Builder setOnApplyInsetsListener(@Nullable OnApplyInsetsListener onApplyInsetsListener) {
      this.onApplyInsetsListener = onApplyInsetsListener;
      return this;
    }

    /**
     * @param flags specifies the sides on which the system window insets should be applied to the
     *     padding. Ignored if {@link Insetter#onApplyInsetsListener } is set.
     * @see Insetter#applyInsetsToView(View, WindowInsetsCompat, ViewState)
     */
    @NonNull
    public Builder applySystemWindowInsetsToPadding(@Sides int flags) {
      paddingSystemWindowInsets = flags;
      return this;
    }

    /**
     * @param flags specifies the sides on which the system window insets should be applied to the
     *     margin. Ignored if {@link Insetter#onApplyInsetsListener } is set.
     * @see Insetter#applyInsetsToView(View, WindowInsetsCompat, ViewState)
     */
    @NonNull
    public Builder applySystemWindowInsetsToMargin(@Sides int flags) {
      marginSystemWindowInsets = flags;
      return this;
    }

    /**
     * @param flags specifies the sides on which the system gesture insets should be applied to the
     *     padding. Ignored if {@link Insetter#onApplyInsetsListener } is set.
     * @see Insetter#applyInsetsToView(View, WindowInsetsCompat, ViewState)
     */
    @NonNull
    public Builder applySystemGestureInsetsToPadding(@Sides int flags) {
      paddingSystemGestureInsets = flags;
      return this;
    }

    /**
     * @param flags specifies the sides on which the system gesture insets should be applied to the
     *     margin. Ignored if {@link Insetter#onApplyInsetsListener } is set.
     * @see Insetter#applyInsetsToView(View, WindowInsetsCompat, ViewState)
     */
    @NonNull
    public Builder applySystemGestureInsetsToMargin(@Sides int flags) {
      marginSystemGestureInsets = flags;
      return this;
    }

    /**
     * @param consumeSystemWindowInsets true if the system window insets should be consumed, false
     *     if not. If unset, the default behavior is to not consume system window insets.
     * @see Insetter#setOnApplyInsetsListener(View)
     */
    @NonNull
    public Builder consumeSystemWindowInsets(boolean consumeSystemWindowInsets) {
      this.consumeSystemWindowInsets = consumeSystemWindowInsets;
      return this;
    }

    /**
     * Builds the {@link Insetter} instance and sets it as a {@link OnApplyWindowInsetsListener} on
     * the provided {@link View}.
     *
     * @param view the {@link View} on which {@link WindowInsetsCompat} should be applied
     */
    @NonNull
    public Insetter applyToView(@NonNull View view) {
      Insetter insetter = build();
      insetter.setOnApplyInsetsListener(view);
      return insetter;
    }

    /** Builds the {@link Insetter} instance. */
    @NonNull
    public Insetter build() {
      return new Insetter(this);
    }
  }

  /** Returns a instance of {@link Builder} used for creating an instance of {@link Insetter}. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A wrapper around {@link ViewCompat#setOnApplyWindowInsetsListener(View,
   * OnApplyWindowInsetsListener)} which stores the initial view state, and provides them whenever a
   * {@link android.view.WindowInsets} instance is dispatched to the listener provided.
   *
   * <p>This allows the listener to be able to append inset values to any existing view state
   * properties, rather than overwriting them.
   */
  private void setOnApplyInsetsListener(@NonNull View view) {

    final OnApplyInsetsListener listener =
        onApplyInsetsListener != null
            ? onApplyInsetsListener
            : new OnApplyInsetsListener() {
              @Override
              public void onApplyInsets(
                  @NonNull View view,
                  @NonNull WindowInsetsCompat insets,
                  @NonNull ViewState initialState) {

                applyInsetsToView(view, insets, initialState);
              }
            };

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

            if (consumeSystemWindowInsets) {
              return insets.consumeSystemWindowInsets();
            } else {
              return insets;
            }
          }
        });

    // Now request an insets pass
    requestApplyInsetsWhenAttached(view);
  }

  /**
   * A wrapper around {@link ViewCompat#requestApplyInsets(View)} which ensures the request will
   * happen, regardless of whether the view is attached or not.
   */
  private static void requestApplyInsetsWhenAttached(@NonNull final View view) {
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
   * paddingSystemGestureInsets} and {@code marginSystemGestureInsets} accept side flag values.
   *
   * @param view the view to apply inset handling too
   * @param insets the insets to apply
   * @param initialState the initial view state of the view
   */
  public void applyInsetsToView(
      @NonNull final View view,
      @NonNull final WindowInsetsCompat insets,
      @NonNull final ViewState initialState) {

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
    if (SideUtils.hasSide(paddingSystemGestureInsets, Side.LEFT)) {
      paddingLeft = initialPadding.getLeft() + systemGestureInsets.left;
    } else if (SideUtils.hasSide(paddingSystemWindowInsets, Side.LEFT)) {
      paddingLeft = initialPadding.getLeft() + systemWindowInsets.left;
    }

    int paddingTop = view.getPaddingTop();
    if (SideUtils.hasSide(paddingSystemGestureInsets, Side.TOP)) {
      paddingTop = initialPadding.getTop() + systemGestureInsets.top;
    } else if (SideUtils.hasSide(paddingSystemWindowInsets, Side.TOP)) {
      paddingTop = initialPadding.getTop() + systemWindowInsets.top;
    }

    int paddingRight = view.getPaddingRight();
    if (SideUtils.hasSide(paddingSystemGestureInsets, Side.RIGHT)) {
      paddingRight = initialPadding.getRight() + systemGestureInsets.right;
    } else if (SideUtils.hasSide(paddingSystemWindowInsets, Side.RIGHT)) {
      paddingRight = initialPadding.getRight() + systemWindowInsets.right;
    }

    int paddingBottom = view.getPaddingBottom();
    if (SideUtils.hasSide(paddingSystemGestureInsets, Side.BOTTOM)) {
      paddingBottom = initialPadding.getBottom() + systemGestureInsets.bottom;
    } else if (SideUtils.hasSide(paddingSystemWindowInsets, Side.BOTTOM)) {
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
      if (SideUtils.hasSide(marginSystemGestureInsets, Side.LEFT)) {
        marginLeft = initialMargins.getLeft() + systemGestureInsets.left;
      } else if (SideUtils.hasSide(marginSystemWindowInsets, Side.LEFT)) {
        marginLeft = initialMargins.getLeft() + systemWindowInsets.left;
      }

      int marginTop = mlp.topMargin;
      if (SideUtils.hasSide(marginSystemGestureInsets, Side.TOP)) {
        marginTop = initialMargins.getTop() + systemGestureInsets.top;
      } else if (SideUtils.hasSide(marginSystemWindowInsets, Side.TOP)) {
        marginTop = initialMargins.getTop() + systemWindowInsets.top;
      }

      int marginRight = mlp.rightMargin;
      if (SideUtils.hasSide(marginSystemGestureInsets, Side.RIGHT)) {
        marginRight = initialMargins.getRight() + systemGestureInsets.right;
      } else if (SideUtils.hasSide(marginSystemWindowInsets, Side.RIGHT)) {
        marginRight = initialMargins.getRight() + systemWindowInsets.right;
      }

      int marginBottom = mlp.bottomMargin;
      if (SideUtils.hasSide(marginSystemGestureInsets, Side.BOTTOM)) {
        marginBottom = initialMargins.getBottom() + systemGestureInsets.bottom;
      } else if (SideUtils.hasSide(marginSystemWindowInsets, Side.BOTTOM)) {
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
    } else if (marginSystemGestureInsets != Side.NONE || marginSystemWindowInsets != Side.NONE) {
      throw new IllegalArgumentException(
          "Margin inset handling requested but view LayoutParams do not"
              + " extend MarginLayoutParams");
    }
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
}
