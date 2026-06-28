/*
 * Copyright (C) 2016 The Guava Authors
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.graph.Graphs.checkNonNegative;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;

/**
 * A base class for builders that construct graphs with user-defined properties.
 *
 * @author James Sexton
 */
abstract class AbstractGraphBuilder<N, B extends AbstractGraphBuilder<N, B>> {
  final boolean directed;
  boolean allowsSelfLoops = false;
  ElementOrder<N> nodeOrder = ElementOrder.insertion();
  ElementOrder<N> incidentEdgeOrder = ElementOrder.unordered();

  @Nullable Integer expectedNodeCount = null;

  /**
   * Creates a new instance with the specified edge directionality.
   *
   * @param directed if true, creates an instance for graphs whose edges are each directed; if
   *     false, creates an instance for graphs whose edges are each undirected.
   */
  AbstractGraphBuilder(boolean directed) {
    this.directed = directed;
  }

  @CanIgnoreReturnValue
  final B allowsSelfLoopsInternal(boolean allowsSelfLoops) {
    this.allowsSelfLoops = allowsSelfLoops;
    return self();
  }

  @CanIgnoreReturnValue
  final B expectedNodeCountInternal(int expectedNodeCount) {
    this.expectedNodeCount = checkNonNegative(expectedNodeCount);
    return self();
  }

  final void copyStateTo(AbstractGraphBuilder<N, ?> builder) {
    builder.allowsSelfLoops = allowsSelfLoops;
    builder.nodeOrder = nodeOrder;
    builder.incidentEdgeOrder = incidentEdgeOrder;
    builder.expectedNodeCount = expectedNodeCount;
  }

  static <N> ElementOrder<N> validateIncidentEdgeOrder(ElementOrder<N> incidentEdgeOrder) {
    checkArgument(
        incidentEdgeOrder.type() == ElementOrder.Type.UNORDERED
            || incidentEdgeOrder.type() == ElementOrder.Type.STABLE,
        "The given elementOrder (%s) is unsupported. incidentEdgeOrder() only supports"
            + " ElementOrder.unordered() and ElementOrder.stable().",
        incidentEdgeOrder);
    return checkNotNull(incidentEdgeOrder);
  }

  @SuppressWarnings("unchecked")
  final B self() {
    return (B) this;
  }

  @SuppressWarnings("unchecked")
  final <N1 extends N, B1 extends AbstractGraphBuilder<N1, B1>> B1 cast() {
    return (B1) this;
  }
}
