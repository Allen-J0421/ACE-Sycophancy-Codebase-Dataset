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

## Result

All functionality preserved, output matches original (0 4 7 9 10). The code is now:
- **Production-Ready**: Factory methods, equality, and hashing enable proper use in collections
- **Type-Safe**: Value objects with complete equals/hashCode implementation
- **Immutable**: Private constructors force controlled construction
- **Validated**: Domain constraints enforced at boundaries
- **Functional**: Stream operations and functional patterns for modern Java
- **Testable**: Complete equality support enables assertion-based testing
- **Extensible**: Optional types and functional approaches enable future features
