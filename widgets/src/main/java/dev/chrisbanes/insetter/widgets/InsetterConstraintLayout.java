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

package dev.chrisbanes.insetter.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.ViewDimensions;
import dev.chrisbanes.insetter.ViewState;

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
      applyInsetsToChild(view, insetsCompat, state);
    }
    return insetsCompat.toWindowInsets();
  }

  private void applyInsetsToChild(
      @NonNull View view, @NonNull WindowInsetsCompat insets, @NonNull ViewState initialState) {
    final LayoutParams lp = (LayoutParams) view.getLayoutParams();

    int paddingLeft = 0;
    if (hasFlag(lp.paddingSystemGestureInsets, LayoutParams.LEFT)) {
      paddingLeft = insets.getSystemGestureInsets().left;
    } else if (hasFlag(lp.paddingSystemWindowInsets, LayoutParams.LEFT)) {
      paddingLeft = insets.getSystemWindowInsetLeft();
    }

    int paddingTop = 0;
    if (hasFlag(lp.paddingSystemGestureInsets, LayoutParams.TOP)) {
      paddingTop = insets.getSystemGestureInsets().top;
    } else if (hasFlag(lp.paddingSystemWindowInsets, LayoutParams.TOP)) {
      paddingTop = insets.getSystemWindowInsetTop();
    }

    int paddingRight = 0;
    if (hasFlag(lp.paddingSystemGestureInsets, LayoutParams.RIGHT)) {
      paddingRight = insets.getSystemGestureInsets().right;
    } else if (hasFlag(lp.paddingSystemWindowInsets, LayoutParams.RIGHT)) {
      paddingRight = insets.getSystemWindowInsetRight();
    }

    int paddingBottom = 0;
    if (hasFlag(lp.paddingSystemGestureInsets, LayoutParams.BOTTOM)) {
      paddingBottom = insets.getSystemGestureInsets().bottom;
    } else if (hasFlag(lp.paddingSystemWindowInsets, LayoutParams.BOTTOM)) {
      paddingBottom = insets.getSystemWindowInsetLeft();
    }

    final ViewDimensions initialPadding = initialState.getPaddings();
    view.setPadding(
        initialPadding.getLeft() + paddingLeft,
        initialPadding.getTop() + paddingTop,
        initialPadding.getRight() + paddingRight,
        initialPadding.getBottom() + paddingBottom);

    final boolean marginInsetRequested =
        lp.marginSystemGestureInsets != 0 || lp.marginSystemWindowInsets != 0;
    if (marginInsetRequested) {
      int marginLeft = 0;
      if (hasFlag(lp.marginSystemGestureInsets, LayoutParams.LEFT)) {
        marginLeft = insets.getSystemGestureInsets().left;
      } else if (hasFlag(lp.marginSystemWindowInsets, LayoutParams.LEFT)) {
        marginLeft = insets.getSystemWindowInsetLeft();
      }

      int marginTop = 0;
      if (hasFlag(lp.marginSystemGestureInsets, LayoutParams.TOP)) {
        marginTop = insets.getSystemGestureInsets().top;
      } else if (hasFlag(lp.marginSystemWindowInsets, LayoutParams.TOP)) {
        marginTop = insets.getSystemWindowInsetTop();
      }

      int marginRight = 0;
      if (hasFlag(lp.marginSystemGestureInsets, LayoutParams.RIGHT)) {
        marginRight = insets.getSystemGestureInsets().right;
      } else if (hasFlag(lp.marginSystemWindowInsets, LayoutParams.RIGHT)) {
        marginRight = insets.getSystemWindowInsetRight();
      }

      int marginBottom = 0;
      if (hasFlag(lp.marginSystemGestureInsets, LayoutParams.BOTTOM)) {
        marginBottom = insets.getSystemGestureInsets().bottom;
      } else if (hasFlag(lp.marginSystemWindowInsets, LayoutParams.BOTTOM)) {
        marginBottom = insets.getSystemWindowInsetBottom();
      }

      final ViewDimensions initialMargins = initialState.getMargins();
      lp.leftMargin = initialMargins.getLeft() + marginLeft;
      lp.topMargin = initialMargins.getTop() + marginTop;
      lp.rightMargin = initialMargins.getRight() + marginRight;
      lp.bottomMargin = initialMargins.getBottom() + marginBottom;
      view.setLayoutParams(lp);
    }
  }

  private static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      LayoutParams childLp = (LayoutParams) child.getLayoutParams();

      if (childLp.applyInsetsRequired) {
        ViewCompat.requestApplyInsets(child);
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
    public static final int LEFT = Gravity.LEFT;
    public static final int TOP = Gravity.TOP;
    public static final int RIGHT = Gravity.RIGHT;
    public static final int BOTTOM = Gravity.BOTTOM;
    public static final int NONE = Gravity.NO_GRAVITY;

    public int paddingSystemWindowInsets = NONE;
    public int paddingSystemGestureInsets = NONE;
    public int marginSystemWindowInsets = NONE;
    public int marginSystemGestureInsets = NONE;

    boolean applyInsetsRequired = true;

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

    public void validate() {
      super.validate();

      if (paddingSystemWindowInsets != NONE
          || paddingSystemGestureInsets != NONE
          || marginSystemWindowInsets != NONE
          || marginSystemGestureInsets != NONE) {
        applyInsetsRequired = true;
      }
    }
  }
}
