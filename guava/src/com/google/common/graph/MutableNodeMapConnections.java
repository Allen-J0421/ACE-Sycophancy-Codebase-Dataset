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

import static com.google.common.base.Preconditions.checkState;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.function.Supplier;

final class MutableNodeMapConnections {
  private MutableNodeMapConnections() {}

  @CanIgnoreReturnValue
  static <N, C> boolean addNode(
      MapIteratorCache<N, C> nodeConnections, N node, Supplier<? extends C> newConnections) {
    if (nodeConnections.containsKey(node)) {
      return false;
    }
    putNewNode(nodeConnections, node, newConnections);
    return true;
  }

  @CanIgnoreReturnValue
  static <N, C> C getOrPutNode(
      MapIteratorCache<N, C> nodeConnections, N node, Supplier<? extends C> newConnections) {
    C connections = nodeConnections.get(node);
    return (connections == null) ? putNewNode(nodeConnections, node, newConnections) : connections;
  }

  @CanIgnoreReturnValue
  private static <N, C> C putNewNode(
      MapIteratorCache<N, C> nodeConnections, N node, Supplier<? extends C> newConnections) {
    C connections = newConnections.get();
    checkState(nodeConnections.put(node, connections) == null);
    return connections;
  }
}
