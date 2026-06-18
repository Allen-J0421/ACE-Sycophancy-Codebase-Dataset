# Dijkstra's Shortest Path - Refactoring Summary

## Iteration 1: Type Safety & Naming
- Replaced raw `int[]` arrays with dedicated `Edge` and `QueueEntry` classes
- Renamed cryptic variables for clarity: `u`, `v`, `w`, `d`, `src` → descriptive names
- Separated concerns: graph building, algorithm logic, and display

## Iteration 2: Architecture & Extensibility
- Added `ShortestPathResult` to encapsulate output
- Implemented input validation for vertices and source nodes
- Extracted algorithm steps: `processQueueEntry()`, `relaxEdge()`
- Added `GraphBuilder` with fluent API for readable graph construction
- Added `toString()` methods for debugging support

## Iteration 3: Factory Methods, Equality, and Advanced Patterns

### Factory Methods & Initialization
1. **Factory Method Pattern**
   - `Edge.of(destination, weight)` replaces direct constructor
   - `Graph.create(vertexCount)` provides factory creation
   - `PriorityQueueEntry.of(distance, node)` standardizes creation
   - `ShortestPathResult.of(distances, sourceNode)` for result construction
   - `GraphBuilder.withVertexCount(vertexCount)` for builder initialization
   - Makes constructors private, enforces consistent creation paths

2. **Streaming & Functional Programming**
   - Used `IntStream.range()` for initialization loops in Graph
   - Replaced loop with functional `forEach()` in queue processing
   - Streams provide cleaner, more declarative code

### Equality & Hashing
1. **Complete Value Object Implementation**
   - Added `equals()` and `hashCode()` to all domain classes:
     - `Edge`: By destination and weight
     - `ShortestPathResult`: By source node and distances
     - `Graph`: By vertex count and adjacency list
     - `PriorityQueueEntry`: By distance and node
   - Enables proper comparison, collection usage, testing

### Enhanced Validation & Safety
1. **Domain Constraint Enforcement**
   - `Edge`: Non-negative weight validation
   - `Graph`: Non-zero vertex count, no self-loops
   - `PriorityQueueEntry`: Non-negative distance
   - All constraint violations throw clear `IllegalArgumentException`

2. **Null Safety**
   - `Objects.requireNonNull()` for critical dependencies
   - Optional return type for `getDistanceTo(node)` instead of unchecked access
   - Prevents `NullPointerException` at runtime

### API Improvements
1. **Optional for Safe Queries**
   - `ShortestPathResult.getDistanceTo(node)` returns `Optional<Integer>`
   - Safely handles out-of-range queries
   - Added `isReachable(node)` convenience method

2. **Semantic Method Names**
   - `isOutdatedEntry()` extracts duplicate check logic
   - `processQueue()` separates loop from entry processing
   - `GraphBuilder.withVertexCount()` clarifies intent vs `new GraphBuilder(n)`

3. **Result Formatting Extraction**
   - New `ResultFormatter` class separates display logic
   - `printDistances()` and `formatResult()` are independently reusable
   - Enables future formatting strategies without changing Main

### Code Quality Improvements
1. **Functional Programming**
   - `graph.getAdjacencyListFor(currentNode).forEach(...)` replaces explicit loop
   - More concise, easier to parallelize if needed

2. **Constant & Helper Methods**
   - Extracted `isOutdatedEntry()` for clarity
   - Makes algorithm intent more visible

3. **String Formatting**
   - All `toString()` implementations use `String.format()` consistently
   - Better for debugging and logging

## Iteration 4: Abstraction Layers, Path Tracking, and Polymorphism

### Major Architectural Improvements

1. **Interface-Based Design**
   - New `WeightedGraphView` interface abstracts graph access
   - `Graph` implements `WeightedGraphView` for flexible polymorphism
   - Solver depends on abstraction, not concrete Graph class
   - Enables alternative graph implementations without changing solver

2. **Path Reconstruction**
   - New `Path` class encapsulates path information
   - Stores node sequence and total distance
   - `ShortestPathResult.getPathTo(node)` returns `Optional<Path>`
   - Reconstructs paths via predecessor array tracking
   - `ResultFormatter.printPaths()` displays all paths with distances

3. **Predecessor Tracking**
   - `DijkstraShortestPathSolver` now maintains predecessor array
   - Updated during relaxation: `predecessors[neighbor] = currentNode`
   - Enables full path reconstruction without recomputation
   - Passed to `ShortestPathResult` for lazy path building

4. **Separation of Concerns: DistanceTable**
   - New `DistanceTable` class encapsulates distance state management
   - Hides distance array from solver implementation
   - Provides `updateDistance()`, `getDistance()`, `toArray()` interface
   - Makes solver code more readable and testable
   - Easier to add features like distance bounds checking

### Design Pattern Improvements

1. **Strategy Pattern via Interfaces**
   - `WeightedGraphView` allows different graph implementations
   - Solver works with any graph adhering to interface
   - Future: directed graphs, weighted vs unweighted, dynamic graphs

2. **Value Object Completion**
   - `Path` is complete value object with equals/hashCode
   - Enables path caching and comparison in future versions
   - Immutable with proper encapsulation

3. **Optional for Safe Queries**
   - `getPathTo()` returns `Optional<Path>` for unreachable nodes
   - Safer than null pointers or exceptions
   - Enables fluent chaining: `.ifPresent()`, `.map()`, etc.

### Code Quality & Maintainability

1. **Reduced Coupling**
   - Solver depends on `WeightedGraphView`, not `Graph`
   - Easy to test with mock graphs
   - Easy to add new graph types

2. **Encapsulation Layers**
   - `DistanceTable` encapsulates raw array manipulation
   - `Path` encapsulates path representation
   - `ShortestPathResult` encapsulates all output information

3. **Functional Consistency**
   - `IntStream` for range operations
   - `Optional` for safe nullable results
   - Stream operations for batch processing in `ResultFormatter`

4. **Extensibility Hooks**
   - `Path` class can be extended with methods: `length()`, `reverseIterator()`, `contains(node)`
   - `WeightedGraphView` can be extended for directed graphs
   - `DistanceTable` can track updates for visualization

## Result

All functionality preserved and enhanced:
- **Original output**: `0 4 7 9 10` ✓
- **New capability**: Full path reconstruction with node sequences
- **Paths found**:
  - Node 0: [0] (distance=0)
  - Node 1: [0, 1] (distance=4)
  - Node 2: [0, 1, 2] (distance=7)
  - Node 3: [0, 1, 2, 3] (distance=9)
  - Node 4: [0, 1, 4] (distance=10)

The code is now:
- **Interface-Driven**: Polymorphic via `WeightedGraphView` abstraction
- **Feature-Rich**: Path reconstruction without additional computation
- **Well-Layered**: DistanceTable and Path separate concerns
- **Testable**: Easy to mock graphs and verify paths
- **Extensible**: New graph types or visualizations easy to add
- **Maintainable**: Clear responsibilities across classes
