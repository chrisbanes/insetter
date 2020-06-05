/*
 * Copyright 2020 Google LLC
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

/**
 * A class specifying the sides of a {@link android.view.View} on which the relevant insets should
 * be applied.
 */
public final class Sides {
  public static final Sides NONE = Sides.create(false, false, false, false);

  private final boolean left;
  private final boolean top;
  private final boolean right;
  private final boolean bottom;

  private Sides(Builder builder) {
    left = builder.left;
    top = builder.top;
    right = builder.right;
    bottom = builder.bottom;
  }

  /**
   * Returns true if applying the relevant insets to the left side of the {@link android.view.View}
   * is enabled, false otherwise.
   */
  public boolean left() {
    return left;
  }

  /**
   * Returns true if applying the relevant insets to the top side of the {@link android.view.View}
   * is enabled, false otherwise.
   */
  public boolean top() {
    return top;
  }

  /**
   * Returns true if applying the relevant insets to the right side of the {@link android.view.View}
   * is enabled, false otherwise.
   */
  public boolean right() {
    return right;
  }

  /**
   * Returns true if applying the relevant insets to the bottom side of the {@link
   * android.view.View} is enabled, false otherwise.
   */
  public boolean bottom() {
    return bottom;
  }

  /**
   * Returns true if applying the relevant insets to any sides of the {@link android.view.View} is
   * enabled, false otherwise.
   */
  public boolean hasSidesSet() {
    return left || top || right || bottom;
  }

  /**
   * A convenience method for instances of {@link Sides} when all sides are used.
   *
   * @param left true to enable setting the relevant insets on the left side of the {@link
   *     android.view.View}, false otherwise. Disabled by default.
   * @param top true to enable setting the relevant insets on the top side of the {@link
   *     android.view.View}, false otherwise. Disabled by default.
   * @param right true to enable setting the relevant insets on the right side of the {@link
   *     android.view.View}, false otherwise. Disabled by default.
   * @param bottom true to enable setting the relevant insets on the bottom side of the {@link
   *     android.view.View}, false otherwise. Disabled by default.
   */
  public static Sides create(boolean left, boolean top, boolean right, boolean bottom) {
    return Sides.builder().left(left).top(top).right(right).bottom(bottom).build();
  }

  /** Returns a build for creating an instance of {@link Sides}. */
  public static Builder builder() {
    return new Builder();
  }

  /** A builder class for creating instances of {@link Sides}. */
  public static final class Builder {
    private boolean left;
    private boolean top;
    private boolean right;
    private boolean bottom;

    private Builder() {
      // private constructor.
    }

    /**
     * @param left true to enable setting the relevant insets on the left side of the {@link
     *     android.view.View}, false otherwise. Disabled by default.
     */
    public Builder left(boolean left) {
      this.left = left;
      return this;
    }

    /**
     * @param top true to enable setting the relevant insets on the top side of the {@link
     *     android.view.View}, false otherwise. Disabled by default.
     */
    public Builder top(boolean top) {
      this.top = top;
      return this;
    }

    /**
     * @param right true to enable setting the relevant insets on the right side of the {@link
     *     android.view.View}, false otherwise. Disabled by default.
     */
    public Builder right(boolean right) {
      this.right = right;
      return this;
    }

    /**
     * @param bottom true to enable setting the relevant insets on the bottom side of the {@link
     *     android.view.View}, false otherwise. Disabled by default.
     */
    public Builder bottom(boolean bottom) {
      this.bottom = bottom;
      return this;
    }

    /** Builds the {@link Sides} instance. */
    public Sides build() {
      return new Sides(this);
    }
  }
}
