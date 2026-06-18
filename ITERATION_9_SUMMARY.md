# Iteration 9: Flow Networks & Maximum Flow Algorithms

## Overview
Iteration 9 introduces flow network algorithms, completing the graph algorithm suite with maximum flow and minimum cut computations. These algorithms are essential for resource allocation, network routing, and capacity optimization problems.

## New Classes

### 1. FlowNetwork
**Location:** `graph/algorithm/FlowNetwork.java`

Represents a directed flow network with edge capacities and tracks flow values for each edge.

**Design:**
- Bidirectional edge representation (forward and reverse edges)
- Capacity tracking per edge
- Flow tracking per edge
- Residual capacity computation (capacity - flow)

**Key Methods:**
- `addEdge(u, v, capacity)`: Add edge with given capacity
- `getCapacity(u, v)`: Get edge capacity
- `getFlow(u, v)`: Get current flow on edge
- `setFlow(u, v, flow)`: Set flow value
- `getResidualCapacity(u, v)`: Compute residual capacity
- `getTotalFlow()`: Total flow from source

**Properties:**
- Supports multi-edges (accumulates capacities)
- Maintains adjacency list for efficient traversal
- Separates capacity from flow for flexible algorithms

### 2. MaximumFlow
**Location:** `graph/algorithm/MaximumFlow.java`

Implements Ford-Fulkerson maximum flow algorithm using BFS for augmenting path finding (Edmonds-Karp variant).

**Algorithm:**
- Iteratively finds augmenting paths in residual graph
- Uses BFS to find shortest augmenting path (O((V+E)) per iteration)
- Augments flow by minimum residual capacity on path
- Continues until no augmenting paths exist
- Overall complexity: O(VE²)

**Key Methods:**
- `getMaxFlow()`: Returns computed maximum flow value
- `getFlowEdges()`: Returns edges with non-zero flow

**Correctness:**
- Terminates when residual graph has no path from source to sink
- Final flow satisfies flow conservation at all intermediate vertices
- Achieves optimality (proven by max-flow min-cut theorem)

### 3. MinimumCut
**Location:** `graph/algorithm/MinimumCut.java`

Computes minimum cut after maximum flow is computed, partitioning vertices and identifying critical edges.

**Algorithm:**
- Uses BFS on residual graph starting from source
- Identifies source partition (reachable vertices)
- Sink partition = remaining vertices
- Cut edges = edges from source to sink partition with positive flow

**Key Methods:**
- `getSourcePartition()`: Vertices reachable from source in residual graph
- `getSinkPartition()`: Remaining vertices
- `getCutEdges()`: Edges crossing the cut with flow
- `getCutValue()`: Total capacity of cut edges

**Theorem Application:**
- Max-Flow Min-Cut Theorem: max flow = min cut capacity
- Proves optimality of computed flow
- Cut edges represent bottlenecks in network

## Test Suite

**File:** `graph/test/Iteration9Tests.java`
**Test Count:** 9 tests
**Pass Rate:** 100%

### Test Categories:

1. **Maximum Flow (5 tests)**
   - Simple sequential path flow
   - Multiple parallel paths
   - Complex network with cycles
   - Single edge networks
   - Flow edge enumeration

2. **Minimum Cut (4 tests)**
   - Source partition identification
   - Max-flow equals min-cut value
   - Complete partition coverage
   - Cut edge correctness

## Demo Application

**File:** `graph/demo/Iteration9Demo.java`

Demonstrates flow network features with four scenarios:

1. **Simple Flow**: Sequential bottleneck demonstration
2. **Multi-Path Flow**: Parallel path distribution showing flow split
3. **Complex Network**: Real-world scenario with 6 vertices and 10 edges
4. **Minimum Cut**: Max-flow min-cut theorem verification with partition analysis

## Design Patterns Used

1. **Strategy Pattern**: Different augmenting path finding approaches
2. **Builder Pattern**: FlowNetwork construction
3. **Facade Pattern**: MaximumFlow unified interface
4. **State Pattern**: Flow values as algorithm state

## Integration Points

- **With Existing Code:**
  - Compatible with existing graph interfaces
  - Uses standard Java collections
  - Follows established code structure

- **Package Organization:**
  ```
  graph/
  ├── algorithm/
  │   ├── FlowNetwork.java
  │   ├── MaximumFlow.java
  │   └── MinimumCut.java
  ├── demo/
  │   └── Iteration9Demo.java
  └── test/
      └── Iteration9Tests.java
  ```

## Performance Characteristics

| Algorithm | Time | Space | Notes |
|-----------|------|-------|-------|
| Ford-Fulkerson (BFS) | O(VE²) | O(V+E) | Edmonds-Karp variant |
| Augmenting Path BFS | O(V+E) | O(V) | Per iteration |
| MinimumCut Partition | O(V+E) | O(V) | Linear scan of residual |

**Optimization Notes:**
- Current BFS implementation is O(VE²) worst-case
- For better performance: Dinic's algorithm O(V²E), push-relabel O(V³)
- Space-efficient: bidirectional edges stored in single hash map

## Code Statistics
- **New Classes:** 3
- **New Test Methods:** 9
- **Test Pass Rate:** 100% (9/9)
- **Total LOC (Iteration 9):** ~420 lines
- **Cumulative Project LOC:** ~4,220 lines

## Real-World Applications

1. **Network Routing**: Route traffic through network maximizing throughput
2. **Resource Allocation**: Distribute resources across capacity-limited channels
3. **Bipartite Matching**: Find maximum matchings in bipartite graphs
4. **Baseball Elimination**: Determine if team can win division
5. **Image Segmentation**: Min-cut used in computer vision
6. **Airline Scheduling**: Route flights through hub network

## Key Insights

1. **Optimality Certificate**: Min-cut provides proof of max-flow optimality
2. **Flow Decomposition**: Max-flow can decompose into path flows
3. **Residual Graph**: Key abstraction enabling iterative algorithms
4. **Bottleneck Identification**: Cut edges reveal capacity constraints
5. **Flow Conservation**: Kirchhoff's law for flow networks

## Relationships to Previous Iterations

- **Iteration 1-4**: Foundation (DFS, BFS, connected components)
- **Iteration 5**: Statistics (support for network metrics)
- **Iteration 6**: Weighted graphs (edge weights for capacities)
- **Iteration 7**: Directed graphs (flow direction)
- **Iteration 8**: Connectivity (bridges, articulation points)
- **Iteration 9**: Flow networks (utilizes all previous work)

## Completion Status

✅ Implementation complete
✅ Test suite passing (9/9)
✅ Demo functionality verified
✅ Documentation complete

---

**Iteration Summary**: Iteration 9 brings sophisticated flow network algorithms to the codebase, enabling optimization problems ranging from network routing to resource allocation. The max-flow min-cut framework is fundamental to combinatorial optimization.
