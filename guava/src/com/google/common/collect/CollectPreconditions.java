/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import org.jspecify.annotations.Nullable;

/** Precondition checks useful in collection implementations. */
@GwtCompatible
final class CollectPreconditions {

  static void checkEntryNotNull(@Nullable Object key, @Nullable Object value) {
    if (key == null) {
      throw nullKeyInEntry(value);
    } else if (value == null) {
      throw nullValueInEntry(key);
    }
  }

  @CanIgnoreReturnValue
  static int checkNonnegative(int value, String name) {
    if (value < 0) {
      throw nonnegative(name, value);
    }
    return value;
  }

  @CanIgnoreReturnValue
  static long checkNonnegative(long value, String name) {
    if (value < 0) {
      throw nonnegative(name, value);
    }
    return value;
  }

  /** A variant of {@link #checkNonnegative} that throws {@link IndexOutOfBoundsException}. */
  @CanIgnoreReturnValue
  static int checkNonnegativeIndex(int value, String name) {
    if (value < 0) {
      throw nonnegativeIndex(name, value);
    }
    return value;
  }

  static void checkPositive(int value, String name) {
    if (value <= 0) {
      throw positive(name, value);
    }
  }

  /**
   * Precondition tester for {@link Iterator#remove} that throws an exception with a consistent
   * error message.
   */
  static void checkRemove(boolean canRemove) {
    checkState(canRemove, "no calls to next() since the last call to remove()");
  }

  private CollectPreconditions() {}

  private static NullPointerException nullKeyInEntry(@Nullable Object value) {
    return new NullPointerException("null key in entry: null=" + value);
  }

  private static NullPointerException nullValueInEntry(@Nullable Object key) {
    return new NullPointerException("null value in entry: " + key + "=null");
  }

  private static IllegalArgumentException nonnegative(String name, long value) {
    return new IllegalArgumentException(name + " cannot be negative but was: " + value);
  }

  private static IndexOutOfBoundsException nonnegativeIndex(String name, int value) {
    return new IndexOutOfBoundsException(name + " cannot be negative but was: " + value);
  }

  private static IllegalArgumentException positive(String name, int value) {
    return new IllegalArgumentException(name + " must be positive but was: " + value);
  }
}
