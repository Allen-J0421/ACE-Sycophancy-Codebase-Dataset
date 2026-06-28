/*
 * Copyright (C) 2026 The Guava Authors
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

package com.google.common.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

abstract class ImmutableGraphBuilderBase<
    N,
    MutableT,
    ImmutableT,
    B extends ImmutableGraphBuilderBase<N, MutableT, ImmutableT, B>> {
  final MutableT mutableDelegate;

  ImmutableGraphBuilderBase(MutableT mutableDelegate) {
    this.mutableDelegate = checkNotNull(mutableDelegate);
  }

  @CanIgnoreReturnValue
  final B addNodeInternal(N node) {
    addNodeToDelegate(node);
    return self();
  }

  final ImmutableT buildInternal() {
    return buildFromDelegate();
  }

  abstract void addNodeToDelegate(N node);

  abstract ImmutableT buildFromDelegate();

  @SuppressWarnings("unchecked")
  private B self() {
    return (B) this;
  }
}
