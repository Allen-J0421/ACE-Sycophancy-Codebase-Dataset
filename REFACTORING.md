# Dijkstra's Shortest Path - Refactoring Summary

## Iteration 1: Type Safety & Naming

### Improvements Made

1. **Introduced Type Safety with Dedicated Classes**
   - Replaced raw `int[]` arrays with `Edge` and `QueueEntry` classes
   - Added `Graph` class for structure management

2. **Improved Naming Clarity**
   - Renamed cryptic variables: `u`, `v`, `w`, `d`, `src`, `V`, `pq`, `adj`
   - Used descriptive names: `currentNode`, `neighbor`, `weight`, `sourceNode`, `vertexCount`

3. **Separated Concerns**
   - Graph building, algorithm logic, and display in separate classes

## Iteration 2: Architecture & Extensibility

### Additional Improvements

1. **Immutability & Robustness**
   - Made `Edge` fields `final` and added getter methods
   - Added `ShortestPathResult` class to encapsulate output
   - Made adjacency lists unmodifiable via `Collections.unmodifiableList()`

2. **Input Validation**
   - Added `validateVertex()` in Graph class to prevent index out of bounds
   - Added `validateSourceNode()` in solver to catch invalid source nodes
   - Uses `Objects.requireNonNull()` for null safety

3. **Better API Design**
   - Changed `getAdjacencyList()` to `getAdjacencyListFor(vertex)` for clarity
   - `ShortestPathResult` provides both list and single-node distance access
   - Clear, type-safe result object instead of raw list

4. **Method Extraction & Single Responsibility**
   - Extracted `processQueueEntry()` for queue processing logic
   - Extracted `relaxEdge()` for edge relaxation (core Dijkstra operation)
   - Extracted `initializeDistances()` for setup logic
   - Extracted `validateSourceNode()` for input validation
   - Each method has one clear purpose

5. **Builder Pattern**
   - Added `GraphBuilder` for fluent, readable graph construction
   - Enables chaining: `.addEdge().addEdge().addEdge().build()`
   - More maintainable than inline edge additions

6. **Renaming for Clarity**
   - `QueueEntry` → `PriorityQueueEntry` (more specific)
   - `ShortestPathSolver` → `DijkstraShortestPathSolver` (algorithm-specific)
   - Constant `INFINITY` extracted for reuse

7. **Documentation & Debugging**
   - Added `toString()` methods to all domain classes for debugging
   - Added `countEdges()` helper for graph introspection
   - Better error messages with context

8. **Getter Methods**
   - Edge: `getDestination()`, `getWeight()`
   - PriorityQueueEntry: `getDistance()`, `getNode()`
   - ShortestPathResult: `getDistances()`, `getDistanceTo()`, `getSourceNode()`
   - Follows Java conventions and enables future validation

## Result

All functionality preserved, output matches original (0 4 7 9 10). The code is now:
- **Immutable**: Less risk of accidental modification
- **Validated**: Catches errors at boundaries
- **Extensible**: Easy to add features (tracking predecessors, finding paths, etc.)
- **Testable**: Each component can be unit tested independently
- **Readable**: Builder pattern and small methods make flow crystal clear
- **Debuggable**: `toString()` methods aid investigation
