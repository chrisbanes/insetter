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

import static dev.chrisbanes.insetter.Insetter.NONE;

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
import dev.chrisbanes.insetter.Insetter.InsetDimension;
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
      Insetter.applyInsetsToView(
          view,
          insetsCompat,
          state,
          lp.paddingSystemWindowInsets,
          lp.marginSystemWindowInsets,
          lp.paddingSystemGestureInsets,
          lp.marginSystemGestureInsets);
    }
    return insetsCompat.toWindowInsets();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      LayoutParams childLp = (LayoutParams) child.getLayoutParams();

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
    /**
     * Defines which dimensions should be applied using padding from the system window insets. This
     * value is an int containing a combination of bitwise OR'd flags. Possible flags are {@link
     * Insetter#LEFT}, {@link Insetter#TOP}, {@link Insetter#RIGHT} or {@link Insetter#BOTTOM}.
     *
     * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    @InsetDimension public int paddingSystemWindowInsets = NONE;

    /**
     * Defines which dimensions should be applied using padding from the system gesture insets. This
     * value is an int containing a combination of bitwise OR'd flags. Possible flags are {@link
     * Insetter#LEFT}, {@link Insetter#TOP}, {@link Insetter#RIGHT} or {@link Insetter#BOTTOM}.
     *
     * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    @InsetDimension public int paddingSystemGestureInsets = NONE;

    /**
     * Defines which dimensions should be applied using margin from the system window insets. This
     * value is an int containing a combination of bitwise OR'd flags. Possible flags are {@link
     * Insetter#LEFT}, {@link Insetter#TOP}, {@link Insetter#RIGHT} or {@link Insetter#BOTTOM}.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemWindowInsets()
     */
    @InsetDimension public int marginSystemWindowInsets = NONE;

    /**
     * Defines which dimensions should be applied using margin from the system gesture insets. This
     * value is an int containing a combination of bitwise OR'd flags. Possible flags are {@link
     * Insetter#LEFT}, {@link Insetter#TOP}, {@link Insetter#RIGHT} or {@link Insetter#BOTTOM}.
     *
     * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
     *
     * @see WindowInsetsCompat#getSystemGestureInsets()
     */
    @InsetDimension public int marginSystemGestureInsets = NONE;

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
    }

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);

      final TypedArray ta =
          c.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintLayout_Layout);

      paddingSystemWindowInsets =
          ta.getInt(
              R.styleable.InsetterConstraintLayout_Layout_paddingSystemWindowInsets,
              paddingSystemWindowInsets);

      paddingSystemGestureInsets =
          ta.getInt(
              R.styleable.InsetterConstraintLayout_Layout_paddingSystemGestureInsets,
              paddingSystemGestureInsets);

      marginSystemWindowInsets =
          ta.getInt(
              R.styleable.InsetterConstraintLayout_Layout_layout_marginSystemWindowInsets,
              marginSystemWindowInsets);

      marginSystemGestureInsets =
          ta.getInt(
              R.styleable.InsetterConstraintLayout_Layout_layout_marginSystemGestureInsets,
              marginSystemGestureInsets);

      ta.recycle();

      validate();
    }

    private void resetRequestApplyInsetsRequired() {
      requestApplyInsetsRequired = false;
    }

    public void validate() {
      super.validate();

      requestApplyInsetsRequired =
          paddingSystemWindowInsets != NONE
              || paddingSystemGestureInsets != NONE
              || marginSystemWindowInsets != NONE
              || marginSystemGestureInsets != NONE;
    }
  }
}
