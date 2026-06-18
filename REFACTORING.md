# Dijkstra's Shortest Path - Refactoring Summary

## Improvements Made

### 1. **Introduced Type Safety with Dedicated Classes**
   - **Before**: Raw `int[]` arrays with magic indices (0 for distance, 1 for node)
   - **After**: 
     - `Edge` class: Encapsulates destination and weight
     - `QueueEntry` class: Replaces `int[]{distance, node}` arrays
     - `Graph` class: Manages graph structure and operations

### 2. **Improved Naming Clarity**
   - Replaced cryptic single-letter variables:
     - `u`, `v` → `currentNode`, `neighbor`
     - `w` → `weight`
     - `d` → `currentDistance`, `newDistance`
     - `p` → `edge`
     - `src` → `sourceNode`
     - `V` → `vertexCount`
     - `pq` → `priorityQueue`
     - `adj` → `adjacencyList`

### 3. **Separated Concerns**
   - **Graph Building**: Isolated in `Graph` class
   - **Algorithm Logic**: Moved to `ShortestPathSolver` class
   - **Display/Entry Point**: Handled by `Main` class
   - **Data Structure**: `Edge` and `QueueEntry` classes provide domain-specific types

### 4. **Better Encapsulation**
   - Graph internals (adjacencyList) are private with public accessor methods
   - Graph handles its own initialization and edge addition
   - Clear public interface: `addEdge()`, `getAdjacencyList()`, `getVertexCount()`

### 5. **Comparable Implementation**
   - `QueueEntry` implements `Comparable` for proper PriorityQueue ordering
   - Removes fragile lambda comparator: `(a, b) -> a[0] - b[0]`
   - More maintainable and prevents integer overflow in edge cases

### 6. **Improved Code Readability**
   - Method names describe intent: `solve()`, `printDistances()`
   - Clear variable names in loops make algorithm flow obvious
   - Comments implicit in structure (no need to guess what array indices mean)

## Result
All functionality preserved, output matches original (0 4 7 9 10). The refactored code is:
- More maintainable
- Easier to test (each class has single responsibility)
- Self-documenting through clear naming
- Type-safe with dedicated classes
- Scalable for future enhancements
