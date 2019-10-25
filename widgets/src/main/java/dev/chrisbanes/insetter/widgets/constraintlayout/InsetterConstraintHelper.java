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

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.NO_GRAVITY;
import static android.view.Gravity.RIGHT;
import static android.view.Gravity.TOP;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;

public class InsetterConstraintHelper extends ConstraintHelper {

  public int paddingSystemWindowInsets = NO_GRAVITY;
  public int paddingSystemGestureInsets = NO_GRAVITY;
  public int marginSystemWindowInsets = NO_GRAVITY;
  public int marginSystemGestureInsets = NO_GRAVITY;

  private ConstraintLayout container;

  public InsetterConstraintHelper(Context context) { super(context); }

  public InsetterConstraintHelper(Context context, AttributeSet attrs) {
    super(context, attrs);
    readAttrs(attrs);
  }

  public InsetterConstraintHelper(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    readAttrs(attrs);
  }

  private static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }

  private void readAttrs(AttributeSet attrs) {
    final TypedArray ta =
        getContext().obtainStyledAttributes(attrs, R.styleable.InsetterConstraintHelper);

    paddingSystemWindowInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets,
            paddingSystemWindowInsets);

    paddingSystemGestureInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets,
            paddingSystemGestureInsets);

    marginSystemWindowInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_marginSystemWindowInsets,
            marginSystemWindowInsets);

    marginSystemGestureInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_marginSystemGestureInsets,
            marginSystemGestureInsets);

    ta.recycle();
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    container = (ConstraintLayout) getParent();

    for (int id : mIds) {
      View view = container.getViewById(id);
      if (view != null && view.getTag(R.id.insetter_initial_state) == null) {
        view.setTag(R.id.insetter_initial_state, new ViewState(view));
      }
    }
  }

  @Override
  @RequiresApi(20)
  public WindowInsets onApplyWindowInsets(WindowInsets insets) {
    final WindowInsetsCompat insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets);

    for (int i = 0; i < mCount; i++) {
      View view = container.getViewById(mIds[i]);
      final ViewState state = (ViewState) view.getTag(R.id.insetter_initial_state);
      if (state != null) {
        applyInsetsToChild(view, insetsCompat, state);
      }
    }

    return insetsCompat.toWindowInsets();
  }

  private void applyInsetsToChild(
      @NonNull View view, @NonNull WindowInsetsCompat insets, @NonNull ViewState initialState) {

    Insetter.applyInsetsToView(
        view,
        insets,
        initialState,
        hasFlag(paddingSystemWindowInsets, LEFT),
        hasFlag(paddingSystemWindowInsets, TOP),
        hasFlag(paddingSystemWindowInsets, RIGHT),
        hasFlag(paddingSystemWindowInsets, BOTTOM),
        hasFlag(paddingSystemGestureInsets, LEFT),
        hasFlag(paddingSystemGestureInsets, TOP),
        hasFlag(paddingSystemGestureInsets, RIGHT),
        hasFlag(paddingSystemGestureInsets, BOTTOM),
        hasFlag(marginSystemWindowInsets, LEFT),
        hasFlag(marginSystemWindowInsets, TOP),
        hasFlag(marginSystemWindowInsets, RIGHT),
        hasFlag(marginSystemWindowInsets, BOTTOM),
        hasFlag(marginSystemGestureInsets, LEFT),
        hasFlag(marginSystemGestureInsets, TOP),
        hasFlag(marginSystemGestureInsets, RIGHT),
        hasFlag(marginSystemGestureInsets, BOTTOM));
  }

  @Override
  public void updatePostLayout(ConstraintLayout container) {
    super.updatePostLayout(container);

    for (int i = 0; i < mCount; i++) {
      View child = container.getViewById(mIds[i]);
      ViewCompat.requestApplyInsets(child);
    }
  }
}
