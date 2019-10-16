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

public class ViewState {
  private ViewDimensions paddings;
  private ViewDimensions margins;

  ViewState(@NonNull View view) {
    updateForView(view);
  }

  public ViewDimensions getPaddings() {
    return paddings;
  }

  public ViewDimensions getMargins() {
    return margins;
  }

  private void updateForView(@NonNull View view) {
    paddings = createPaddingDimensions(view);
    margins = createMarginDimensions(view);
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
