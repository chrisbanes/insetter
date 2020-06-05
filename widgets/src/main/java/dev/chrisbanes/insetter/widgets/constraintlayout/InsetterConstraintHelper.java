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
import dev.chrisbanes.insetter.InsetDimension;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.ViewState;
import dev.chrisbanes.insetter.widgets.R;
import java.util.EnumSet;

/**
 * An {@link ConstraintHelper} which adds enhanced support for inset handling.
 *
 * <p>This class supports the use of {@code paddingSystemWindowInsets}, {@code
 * paddingSystemGestureInsets}, {@code layout_marginSystemWindowInsets} and {@code
 * layout_marginSystemGestureInsets} attributes, which are then applied to any referenced children.
 * Each of the attributes accept a combination of flags which define which dimensions the relevant
 * insets will be applied with.
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
  private EnumSet<InsetDimension> systemWindowInsetsPaddingDimensions;
  private EnumSet<InsetDimension> systemGestureInsetsPaddingDimensions;
  private EnumSet<InsetDimension> systemWindowInsetsMarginDimensions;
  private EnumSet<InsetDimension> systemGestureInsetsMarginDimensions;

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
            .applySystemWindowInsetsToPadding(systemWindowInsetsPaddingDimensions)
            .applySystemWindowInsetsToMargin(systemWindowInsetsMarginDimensions)
            .applySystemGestureInsetsToPadding(systemGestureInsetsPaddingDimensions)
            .applySystemGestureInsetsToMargin(systemGestureInsetsMarginDimensions)
            .build()
            .applyInsetsToView(view, insetsCompat, state);
      }
    }

    return insetsCompat.toWindowInsets();
  }

  /**
   * Returns the {@link EnumSet} of {@link InsetDimension}s which define which padding dimensions
   * should be applied using the system window insets.
   *
   * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  @Nullable
  public EnumSet<InsetDimension> getSystemWindowInsetsPaddingDimensions() {
    return systemWindowInsetsPaddingDimensions;
  }

  /**
   * Set the {@link InsetDimension}s which define which padding dimensions should be applied using
   * the system window insets.
   *
   * <p>This value can be set using the {@code app:paddingSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  public void setSystemWindowInsetsPaddingDimensions(@Nullable EnumSet<InsetDimension> values) {
    if (!ObjectsCompat.equals(systemWindowInsetsPaddingDimensions, values)) {
      systemWindowInsetsPaddingDimensions = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link EnumSet} of {@link InsetDimension}s which define which padding dimensions
   * should be applied using the system gesture insets.
   *
   * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  @Nullable
  public EnumSet<InsetDimension> getSystemGestureInsetsPaddingDimensions() {
    return systemGestureInsetsPaddingDimensions;
  }

  /**
   * Set the {@link InsetDimension}s which define which padding dimensions should be applied using
   * the system gesture insets.
   *
   * <p>This value can be set using the {@code app:paddingSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  public void setSystemGestureInsetsPaddingDimensions(@Nullable EnumSet<InsetDimension> values) {
    if (!ObjectsCompat.equals(systemGestureInsetsPaddingDimensions, values)) {
      systemGestureInsetsPaddingDimensions = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link EnumSet} of {@link InsetDimension}s which define which margin dimensions
   * should be applied using the system window insets.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  @Nullable
  public EnumSet<InsetDimension> getSystemWindowInsetsMarginDimensions() {
    return systemWindowInsetsMarginDimensions;
  }

  /**
   * Set the {@link InsetDimension}s which define which margin dimensions should be applied using
   * the system window insets.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemWindowInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemWindowInsets()
   */
  public void setSystemWindowInsetsMarginDimensions(@Nullable EnumSet<InsetDimension> values) {
    if (!ObjectsCompat.equals(systemWindowInsetsMarginDimensions, values)) {
      systemWindowInsetsMarginDimensions = values;
      ViewCompat.requestApplyInsets(this);
    }
  }

  /**
   * Returns the {@link EnumSet} of {@link InsetDimension}s which define which margin dimensions
   * should be applied using the system gesture insets.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  @Nullable
  public EnumSet<InsetDimension> getSystemGestureInsetsMarginDimensions() {
    return systemGestureInsetsMarginDimensions;
  }

  /**
   * Set which {@link InsetDimension}s which define which margin dimensions should be applied using
   * the system gesture insets.
   *
   * <p>This value can be set using the {@code app:layout_marginSystemGestureInsets} attribute.
   *
   * @see WindowInsetsCompat#getSystemGestureInsets()
   */
  public void setSystemGestureInsetsMarginDimensions(@Nullable EnumSet<InsetDimension> values) {
    if (!ObjectsCompat.equals(systemGestureInsetsMarginDimensions, values)) {
      systemGestureInsetsMarginDimensions = values;
      ViewCompat.requestApplyInsets(this);
    }
  }
}
