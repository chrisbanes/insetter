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
import android.view.WindowInsets;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;

public class InsetterConstraintHelper extends ConstraintHelper {

  public int paddingSystemWindowInsets = NONE;
  public int marginSystemWindowInsets = NONE;

  public int paddingSystemGestureInsets = NONE;
  public int marginSystemGestureInsets = NONE;

  private ConstraintLayout container;

  public InsetterConstraintHelper(Context context) {
    this(context, null);
  }

  public InsetterConstraintHelper(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InsetterConstraintHelper(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    final TypedArray ta =
        context.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintHelper);

    paddingSystemWindowInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets,
            paddingSystemWindowInsets);

    marginSystemWindowInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_layout_marginSystemWindowInsets,
            marginSystemWindowInsets);

    paddingSystemGestureInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets,
            paddingSystemGestureInsets);

    marginSystemGestureInsets =
        ta.getInt(
            R.styleable.InsetterConstraintHelper_layout_marginSystemGestureInsets,
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
        Insetter.applyInsetsToView(
            view,
            insetsCompat,
            state,
            paddingSystemWindowInsets,
            marginSystemWindowInsets,
            paddingSystemGestureInsets,
            marginSystemGestureInsets);
      }
    }

    return insetsCompat.toWindowInsets();
  }
}
