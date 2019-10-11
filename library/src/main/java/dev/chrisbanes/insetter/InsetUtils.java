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

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsetUtils {

  private InsetUtils() {
    // private constructor. No instantiating.
  }

  public static void setOnApplyInsetsListener(
      @NonNull View view, @NonNull final OnApplyInsetsListener listener) {
    final ViewDimensions initialPadding = createPaddingDimensions(view);
    final ViewDimensions initialMargin = createMarginDimensions(view);

    ViewCompat.setOnApplyWindowInsetsListener(
        view,
        new OnApplyWindowInsetsListener() {
          @Override
          public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            listener.onInsetsListener(insets, initialPadding, initialMargin);
            // Always return the initial insets instance
            return insets;
          }
        });

    // Now request an insets pass
    requestApplyInsetsWhenAttached(view);
  }

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
              ViewCompat.requestApplyInsets(v);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
              // no-op
            }
          });
    }
  }

  @NonNull
  private static ViewDimensions createPaddingDimensions(@NonNull View view) {
    return new ViewDimensions(
        view.getPaddingLeft(),
        view.getPaddingTop(),
        view.getPaddingRight(),
        view.getPaddingBottom());
  }

  @NonNull
  private static ViewDimensions createMarginDimensions(@NonNull View view) {
    if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
      return new ViewDimensions(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, mlp.bottomMargin);
    }
    return ViewDimensions.EMPTY;
  }
}
