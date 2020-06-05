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
import android.view.WindowInsets;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Sides;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;

/**
 * An {@link ConstraintHelper} which adds enhanced support for inset handling.
 *
 * <p>This class supports the use of {@code paddingSystemWindowInsets}, {@code
 * paddingSystemGestureInsets}, {@code layout_marginSystemWindowInsets} and {@code
 * layout_marginSystemGestureInsets} attributes, which are then applied to any referenced children.
 * Each of the attributes accepts a combination of flags which defines the sides on which the
 * relevant insets will be applied.
 *
 * <pre>
 * &lt;androidx.constraintlayout.widget.ConstraintLayout
 *     xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;
 *     xmlns:app=&quot;http://schemas.android.com/apk/res-auto&quot;
 *     android:layout_width=&quot;match_parent&quot;
 *     android:layout_height=&quot;match_parent&quot;&gt;
 *
 *     &lt;dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *         android:layout_width=&quot;wrap_content&quot;
 *         android:layout_height=&quot;wrap_content&quot;
 *         app:paddingSystemWindowInsets=&quot;left|top|right|bottom&quot;
 *         app:constraint_referenced_ids=&quot;image&quot; /&gt;
 *
 *     &lt;ImageView
 *         android:id=&quot;@+id/image&quot;
 *         android:layout_width=&quot;wrap_content&quot;
 *         android:layout_height=&quot;wrap_content&quot;
 *         android:src=&quot;@drawable/icon&quot; /&gt;
 *
 * &lt;/androidx.constraintlayout.widget.ConstraintLayout&gt;
 * </pre>
 *
 * A {@link InsetterConstraintHelper} can be applied to multiple views, but appending the ID name to
 * the {@code constraint_referenced_ids} attribute on the helper, like so:
 *
 * <pre>
 * &lt;dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *     android:layout_width=&quot;wrap_content&quot;
 *     android:layout_height=&quot;wrap_content&quot;
 *     app:paddingSystemWindowInsets=&quot;left|top|right|bottom&quot;
 *     app:constraint_referenced_ids=&quot;image,button,toolbar&quot; /&gt;
 * </pre>
 *
 * Multiple {@link InsetterConstraintHelper}s can also safely be applied to a single view, if
 * required:
 *
 * <pre>
 * &lt;androidx.constraintlayout.widget.ConstraintLayout ...&gt;
 *
 *     &lt;dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *         android:layout_width=&quot;wrap_content&quot;
 *         android:layout_height=&quot;wrap_content&quot;
 *         app:paddingSystemWindowInsets=&quot;left|right&quot;
 *         app:constraint_referenced_ids=&quot;image&quot; /&gt;
 *
 *     &lt;dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *         android:layout_width=&quot;wrap_content&quot;
 *         android:layout_height=&quot;wrap_content&quot;
 *         app:paddingSystemWindowInsets=&quot;top|bottom&quot;
 *         app:constraint_referenced_ids=&quot;image&quot; /&gt;
 *
 *     &lt;ImageView
 *         android:id=&quot;@+id/image&quot;
 *         ... /&gt;
 *
 * &lt;/androidx.constraintlayout.widget.ConstraintLayout&gt;
 * </pre>
 */
public class InsetterConstraintHelper extends ConstraintHelper {
  private Sides systemWindowInsetsPaddingSides;
  private Sides systemGestureInsetsPaddingSides;
  private Sides systemWindowInsetsMarginSides;
  private Sides systemGestureInsetsMarginSides;

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

    final int paddingSystemWindowInsetsFlags =
        ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets, 0);
    systemWindowInsetsPaddingSides =
        AttributeHelper.flagToSides(paddingSystemWindowInsetsFlags);

    final int marginSystemWindowInsetsFlags =
        ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemWindowInsets, 0);
    systemWindowInsetsMarginSides = AttributeHelper.flagToSides(marginSystemWindowInsetsFlags);

    final int paddingSystemGestureInsetsFlags =
        ta.getInt(R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets, 0);
    systemGestureInsetsPaddingSides =
        AttributeHelper.flagToSides(paddingSystemGestureInsetsFlags);

    final int marginSystemGestureInsetsFlags =
        ta.getInt(R.styleable.InsetterConstraintHelper_layout_marginSystemGestureInsets, 0);
    systemGestureInsetsMarginSides =
        AttributeHelper.flagToSides(marginSystemGestureInsetsFlags);

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
        Insetter.builder()
            .applySystemWindowInsetsToPadding(systemWindowInsetsPaddingSides)
            .applySystemWindowInsetsToMargin(systemWindowInsetsMarginSides)
            .applySystemGestureInsetsToPadding(systemGestureInsetsPaddingSides)
            .applySystemGestureInsetsToMargin(systemGestureInsetsMarginSides)
            .build()
            .applyInsetsToView(view, insetsCompat, state);
      }
    }

    return insetsCompat.toWindowInsets();
  }

  /**
   * Returns the {@link Sides} on which system window insets should be applied to the padding.
   *
   * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  @Nullable
  public Sides getSystemWindowInsetsPaddingSides() {
    return systemWindowInsetsPaddingSides;
  }

  /**
   * Set the {@link Sides} on which system window insets should be applied to the padding.
   *
   * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  public void setSystemWindowInsetsPaddingSides(@Nullable Sides values) {
    if (!ObjectsCompat.equals(systemWindowInsetsPaddingSides, values)) {
      systemWindowInsetsPaddingSides = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link Sides} on which system gesture insets should be applied to the padding.
   *
   * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  @Nullable
  public Sides getSystemGestureInsetsPaddingSides() {
    return systemGestureInsetsPaddingSides;
  }

  /**
   * Set the {@link Sides} on which system gesture insets should be applied to the padding.
   *
   * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  public void setSystemGestureInsetsPaddingSides(@Nullable Sides values) {
    if (!ObjectsCompat.equals(systemGestureInsetsPaddingSides, values)) {
      systemGestureInsetsPaddingSides = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link Sides} on which system window insets should be applied to the margin.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  @Nullable
  public Sides getSystemWindowInsetsMarginSides() {
    return systemWindowInsetsMarginSides;
  }

  /**
   * Set the {@link Sides} on which system window insets should be applied to the margin.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  public void setSystemWindowInsetsMarginSides(@Nullable Sides values) {
    if (!ObjectsCompat.equals(systemWindowInsetsMarginSides, values)) {
      systemWindowInsetsMarginSides = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link Sides} on which system gesture insets should be applied to the margin.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  @Nullable
  public Sides getSystemGestureInsetsMarginSides() {
    return systemGestureInsetsMarginSides;
  }

  /**
   * Set the {@link Sides} on which system gesture insets should be applied to the margin.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  public void setSystemGestureInsetsMarginSides(@Nullable Sides values) {
    if (!ObjectsCompat.equals(systemGestureInsetsMarginSides, values)) {
      systemGestureInsetsMarginSides = values;
      ViewCompat.requestApplyInsets(this);
    }
  }
}
