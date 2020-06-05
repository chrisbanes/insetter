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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Insetter.Side;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;
import java.util.EnumSet;

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
 * Each of the attributes accept a combination of flags which define which dimensions the relevant
 * insets will be applied with.
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
          .applySystemWindowInsetsToPadding(lp.systemWindowInsetsPaddingDimensions)
          .applySystemWindowInsetsToMargin(lp.systemWindowInsetsMarginDimensions)
          .applySystemGestureInsetsToPadding(lp.systemGestureInsetsPaddingDimensions)
          .applySystemGestureInsetsToMargin(lp.systemGestureInsetsMarginDimensions)
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
    private EnumSet<Side> systemWindowInsetsPaddingDimensions = null;
    private EnumSet<Side> systemGestureInsetsPaddingDimensions = null;
    private EnumSet<Side> systemWindowInsetsMarginDimensions = null;
    private EnumSet<Side> systemGestureInsetsMarginDimensions = null;

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
      systemWindowInsetsPaddingDimensions = source.systemWindowInsetsPaddingDimensions;
      systemWindowInsetsMarginDimensions = source.systemWindowInsetsMarginDimensions;
      systemGestureInsetsPaddingDimensions = source.systemGestureInsetsPaddingDimensions;
      systemGestureInsetsMarginDimensions = source.systemGestureInsetsMarginDimensions;
    }

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);

      final TypedArray ta =
          c.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintLayout_Layout);

      final int paddingSystemWindowInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets, 0);
      systemWindowInsetsPaddingDimensions =
          AttributeHelper.flagToEnumSet(paddingSystemWindowInsetsFlags);

      final int marginSystemWindowInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemWindowInsets, 0);
      systemWindowInsetsMarginDimensions =
          AttributeHelper.flagToEnumSet(marginSystemWindowInsetsFlags);

      final int paddingSystemGestureInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets, 0);
      systemGestureInsetsPaddingDimensions =
          AttributeHelper.flagToEnumSet(paddingSystemGestureInsetsFlags);

      final int marginSystemGestureInsetsFlags =
          ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemGestureInsets, 0);
      systemGestureInsetsMarginDimensions =
          AttributeHelper.flagToEnumSet(marginSystemGestureInsetsFlags);

      ta.recycle();
    }

    void resetRequestApplyInsetsRequired() {
      requestApplyInsetsRequired = false;
    }

    /**
     * Returns the {@link EnumSet} of {@link Side}s which define which padding dimensions should be
     * applied using the system window insets.
     *
     * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    @Nullable
    public EnumSet<Side> getSystemWindowInsetsPaddingDimensions() {
      return systemWindowInsetsPaddingDimensions;
    }

    /**
     * Set the {@link Side}s which define which padding dimensions should be applied using the
     * system window insets.
     *
     * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public void setSystemWindowInsetsPaddingDimensions(@Nullable EnumSet<Side> values) {
      if (!ObjectsCompat.equals(systemWindowInsetsPaddingDimensions, values)) {
        systemWindowInsetsPaddingDimensions = values;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the {@link EnumSet} of {@link Side}s which define which padding dimensions should be
     * applied using the system gesture insets.
     *
     * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    @Nullable
    public EnumSet<Side> getSystemGestureInsetsPaddingDimensions() {
      return systemGestureInsetsPaddingDimensions;
    }

    /**
     * Set the {@link Side}s which define which padding dimensions should be applied using the
     * system gesture insets.
     *
     * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public void setSystemGestureInsetsPaddingDimensions(@Nullable EnumSet<Side> values) {
      if (!ObjectsCompat.equals(systemGestureInsetsPaddingDimensions, values)) {
        systemGestureInsetsPaddingDimensions = values;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the {@link EnumSet} of {@link Side}s which define which margin dimensions should be
     * applied using the system window insets.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    @Nullable
    public EnumSet<Side> getSystemWindowInsetsMarginDimensions() {
      return systemWindowInsetsMarginDimensions;
    }

    /**
     * Set the {@link Side}s which define which margin dimensions should be applied using the system
     * window insets.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    public void setSystemWindowInsetsMarginDimensions(@Nullable EnumSet<Side> values) {
      if (!ObjectsCompat.equals(systemWindowInsetsMarginDimensions, values)) {
        systemWindowInsetsMarginDimensions = values;
        requestApplyInsetsRequired = true;
      }
    }

    /**
     * Returns the {@link EnumSet} of {@link Side}s which define which margin dimensions should be
     * applied using the system gesture insets.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    @Nullable
    public EnumSet<Side> getSystemGestureInsetsMarginDimensions() {
      return systemGestureInsetsMarginDimensions;
    }

    /**
     * Set the {@link Side}s which define which margin dimensions should be applied using the system
     * gesture insets.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    public void setSystemGestureInsetsMarginDimensions(@Nullable EnumSet<Side> values) {
      if (!ObjectsCompat.equals(systemGestureInsetsMarginDimensions, values)) {
        systemGestureInsetsMarginDimensions = values;
        requestApplyInsetsRequired = true;
      }
    }
  }
}
