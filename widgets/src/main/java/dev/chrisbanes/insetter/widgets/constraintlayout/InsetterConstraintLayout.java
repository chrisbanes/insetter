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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Sides;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;

/**
 * An extension to {@link ConstraintLayout} which adds enhanced support for inset handling.
 *
 * <p>This class supports the use of {@code paddingSystemWindowInsets}, {@code
 * paddingSystemGestureInsets}, {@code layout_marginSystemWindowInsets} and {@code
 * layout_marginSystemGestureInsets} attributes on children:
 *
 * <pre>
 * &lt;dev.chrisbanes.insetter.widgets.InsetterConstraintLayout
 *     xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;
 *     xmlns:app=&quot;http://schemas.android.com/apk/res-auto&quot;
 *     android:layout_width=&quot;match_parent&quot;
 *     android:layout_height=&quot;match_parent&quot;&gt;
 *
 *     &lt;ImageView
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;match_parent&quot;
 *         app:paddingSystemWindowInsets=&quot;left|top|right|bottom&quot;
 *         android:src=&quot;@drawable/icon&quot; /&gt;
 *
 * &lt;/dev.chrisbanes.insetter.widgets.InsetterConstraintLayout&gt;
 * </pre>
 *
 * Each of the attributes accepts a combination of flags which defines the sides on which the
 * relevant insets will be applied.
 */
public class InsetterConstraintLayout extends ConstraintLayout {

  public InsetterConstraintLayout(Context context) {
    super(context);
  }

  public InsetterConstraintLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public InsetterConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onViewAdded(View view) {
    super.onViewAdded(view);
    view.setTag(R.id.insetter_initial_state, new ViewState(view));
  }

  @Override
  @RequiresApi(20)
  public WindowInsets onApplyWindowInsets(WindowInsets insets) {
    final WindowInsetsCompat insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets);
    for (int i = 0; i < getChildCount(); i++) {
      final View view = getChildAt(i);
      final ViewState state = (ViewState) view.getTag(R.id.insetter_initial_state);
      final LayoutParams lp = (LayoutParams) view.getLayoutParams();
      Insetter.builder()
          .applySystemWindowInsetsToPadding(lp.systemWindowInsetsPaddingSides)
          .applySystemWindowInsetsToMargin(lp.systemWindowInsetsMarginSides)
          .applySystemGestureInsetsToPadding(lp.systemGestureInsetsPaddingSides)
          .applySystemGestureInsetsToMargin(lp.systemGestureInsetsMarginSides)
          .build()
          .applyInsetsToView(view, insetsCompat, state);
    }
    return insetsCompat.toWindowInsets();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    for (int i = 0, count = getChildCount(); i < count; i++) {
      final View child = getChildAt(i);
      final LayoutParams childLp = (LayoutParams) child.getLayoutParams();

      if (childLp.requestApplyInsetsRequired) {
        ViewCompat.requestApplyInsets(child);
        childLp.resetRequestApplyInsetsRequired();
      }
    }
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  @Override
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  public static class LayoutParams extends ConstraintLayout.LayoutParams {
    private int systemWindowInsetsPaddingSides;
    private int systemGestureInsetsPaddingSides;
    private int systemWindowInsetsMarginSides;
    private int systemGestureInsetsMarginSides;

    private boolean requestApplyInsetsRequired = true;

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }

    public LayoutParams(ConstraintLayout.LayoutParams source) {
      super(source);
    }

    public LayoutParams(LayoutParams source) {
      super(source);
      systemWindowInsetsPaddingSides = source.systemWindowInsetsPaddingSides;
      systemWindowInsetsMarginSides = source.systemWindowInsetsMarginSides;
      systemGestureInsetsPaddingSides = source.systemGestureInsetsPaddingSides;
      systemGestureInsetsMarginSides = source.systemGestureInsetsMarginSides;
    }

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);

      final TypedArray ta =
          c.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintLayout_Layout);

      final int paddingSystemWindowInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets, 0);
      systemWindowInsetsPaddingSides = AttributeHelper.flagToSides(paddingSystemWindowInsetsFlags);

      final int marginSystemWindowInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemWindowInsets, 0);
      systemWindowInsetsMarginSides = AttributeHelper.flagToSides(marginSystemWindowInsetsFlags);

      final int paddingSystemGestureInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets, 0);
      systemGestureInsetsPaddingSides =
          AttributeHelper.flagToSides(paddingSystemGestureInsetsFlags);

      final int marginSystemGestureInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemGestureInsets, 0);
      systemGestureInsetsMarginSides = AttributeHelper.flagToSides(marginSystemGestureInsetsFlags);

      ta.recycle();
    }

    void resetRequestApplyInsetsRequired() {
      requestApplyInsetsRequired = false;
    }

    /**
     * Returns the sides on which system window insets should be applied to the padding.
     *
     * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public int getSystemWindowInsetsPaddingSides() {
      return systemWindowInsetsPaddingSides;
    }

    /**
     * Set the sides on which system window insets should be applied to the padding.
     *
     * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public void setSystemWindowInsetsPaddingSides(@Sides int flags) {
      if (systemWindowInsetsPaddingSides != flags) {
        systemWindowInsetsPaddingSides = flags;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the sides on which system gesture insets should be applied to the padding.
     *
     * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public int getSystemGestureInsetsPaddingSides() {
      return systemGestureInsetsPaddingSides;
    }

    /**
     * Set the sides on which system gesture insets should be applied to the padding.
     *
     * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public void setSystemGestureInsetsPaddingSides(@Sides int flags) {
      if (systemGestureInsetsPaddingSides != flags) {
        systemGestureInsetsPaddingSides = flags;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the sides on which system window insets should be applied to the margin.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public int getSystemWindowInsetsMarginSides() {
      return systemWindowInsetsMarginSides;
    }

    /**
     * Set the sides on which system window insets should be applied to the margin.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public void setSystemWindowInsetsMarginSides(@Sides int flags) {
      if (systemWindowInsetsMarginSides != flags) {
        systemWindowInsetsMarginSides = flags;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the sidess on which system gesture insets should be applied to the margin.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public int getSystemGestureInsetsMarginSides() {
      return systemGestureInsetsMarginSides;
    }

    /**
     * Set the sides on which system gesture insets should be applied to the margin.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public void setSystemGestureInsetsMarginSides(@Sides int flags) {
      if (systemGestureInsetsMarginSides != flags) {
        systemGestureInsetsMarginSides = flags;
        requestApplyInsetsRequired = true;
      }
    }
  }
}
