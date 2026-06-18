# Module Architecture Guide

## Overview
This project has been refactored into 15 focused, modular files organized by concern. Each module is independently usable and testable.

## File Organization

### Core Interfaces (2 files)
- **`Validator.java`**: Generic validation interface for pluggable validators
  - Defines: `interface Validator<T>`
  - Used by: Edge weight validation, vertex validation

- **`WeightedGraphView.java`**: Abstract graph interface
  - Defines: `interface WeightedGraphView`
  - Implemented by: `Graph.java`
  - Enables: Graph polymorphism and testing with mocks

### Graph Components (4 files)
- **`Edge.java`**: Weighted edge representation
  - Defines: `Edge`, `EdgeWeightValidator`
  - Validates: Non-negative weights
  - Provides: `Edge.of(destination, weight)` factory

- **`Graph.java`**: Undirected weighted graph
  - Implements: `WeightedGraphView`
  - Contains: Adjacency list, vertex count, validation
  - Methods: `addEdge()`, `getAdjacencyListFor()`, `getVertexCount()`

- **`VertexValidator.java`**: Vertex bounds validator
  - Implements: `Validator<Integer>`
  - Validates: Vertex indices within [0, vertexCount)
  - Reused by: Graph for all vertex operations

- **`GraphBuilder.java`**: Fluent graph construction
  - Pattern: Builder pattern
  - Usage: `GraphBuilder.withVertexCount(5).addEdge(...).build()`
  - Returns: Immutable `Graph` instance

### Algorithm (3 files)
- **`DijkstraShortestPathSolver.java`**: Core Dijkstra algorithm
  - Public: `solve(WeightedGraphView graph, int sourceNode)`
  - Private: Queue processing, edge relaxation, initialization
  - Produces: `ShortestPathResult` with distances and paths

- **`DistanceTable.java`**: Distance array encapsulation
  - Manages: Primitive int[] distances
  - API: `updateDistance()`, `getDistance()`, `toArray()`
  - Hides: Raw array manipulation details

- **`PriorityQueueEntry.java`**: Priority queue element
  - Comparable: By distance (ascending)
  - Validates: Non-negative distances
  - Factory: `PriorityQueueEntry.of(distance, node)`

### Results Handling (3 files)
- **`ShortestPathResult.java`**: Algorithm results container
  - Contains: Distances, predecessors, source node, graph reference
  - Caches: Computed paths via `PathCache`
  - API: `getDistanceTo()`, `getPathTo()`, `getAllPaths()`, `isReachable()`

- **`Path.java`**: Path representation
  - Stores: Node sequence (Deque), total distance
  - Utilities: `getLength()`, `contains(node)`
  - Value object: Immutable with `equals()`/`hashCode()`

- **`PathCache.java`**: Path memoization
  - Implementation: `LinkedHashMap`
  - Caches: Both successful paths and unreachable destinations
  - Optimization: Eliminates recomputation on repeated queries

### Configuration & Output (3 files)
- **`AlgorithmConfig.java`**: Runtime configuration
  - Options: `sourceNode`, `trackPaths`, `verbose`
  - Factories: `create()`, `createVerbose()`
  - Future: Extensible for feature flags

- **`ResultFormatter.java`**: Output formatting
  - Takes: `AlgorithmConfig` in constructor
  - Methods: `printDistances()`, `printPaths()`, `printSummary()`
  - Respects: Configuration options for conditional output

- **`Main.java`**: Application entry point
  - Builds: Example graph via `GraphBuilder`
  - Runs: `DijkstraShortestPathSolver`
  - Formats: Results via `ResultFormatter`

## Usage Examples

### Basic Usage
```java
Graph graph = GraphBuilder.withVertexCount(5)
    .addEdge(0, 1, 4)
    .addEdge(1, 2, 3)
    .build();

DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
ShortestPathResult result = solver.solve(graph, 0);

// Get distances
List<Integer> distances = result.getDistances(); // [0, 4, 7, ...]

// Get specific path
Optional<Path> pathTo2 = result.getPathTo(2);
```

### With Configuration
```java
AlgorithmConfig config = AlgorithmConfig.createVerbose(0);
ResultFormatter formatter = new ResultFormatter(config);

formatter.printDistances(result);
formatter.printPaths(result);
formatter.printSummary(result);
```

### Validation
```java
// Automatic validation
Graph graph = Graph.create(5);
graph.addEdge(0, 1, -5);  // Throws IllegalArgumentException
graph.addEdge(0, 0, 5);   // Throws IllegalArgumentException (self-loop)
graph.addEdge(0, 10, 5);  // Throws IllegalArgumentException (out of bounds)
```

## Dependency Graph

```
Main.java
├── GraphBuilder.java
│   └── Graph.java
│       ├── WeightedGraphView.java
│       ├── Edge.java
│       │   └── Validator.java
│       └── VertexValidator.java
│           └── Validator.java
├── AlgorithmConfig.java
├── DijkstraShortestPathSolver.java
│   ├── WeightedGraphView.java
│   ├── DistanceTable.java
│   ├── PriorityQueueEntry.java
│   └── Edge.java
├── ShortestPathResult.java
│   ├── Path.java
│   ├── PathCache.java
│   └── WeightedGraphView.java
└── ResultFormatter.java
    └── AlgorithmConfig.java
```

## Extension Points

### Add Custom Validator
```java
class DistanceValidator implements Validator<Integer> {
    @Override
    public void validate(Integer value) throws IllegalArgumentException {
        if (value > 10000) throw new IllegalArgumentException("Distance too large");
    }
}
```

### Implement Directed Graph
```java
class DirectedGraph implements WeightedGraphView {
    // Only add edge in one direction
    void addEdge(int source, int destination, int weight) {
        adjacencyList.get(source).add(Edge.of(destination, weight));
    }
}
```

### Create New Algorithm
```java
class BellmanFordSolver {
    ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        // Implementation...
    }
}
```

## Compilation & Testing

### Compile All Modules
```bash
javac *.java
```

### Run Application
```bash
java Main
```

### Unit Test Single Module
```java
// EdgeTest.java
@Test
void testEdgeCreation() {
    Edge edge = Edge.of(1, 10);
    assertEquals(1, edge.getDestination());
    assertEquals(10, edge.getWeight());
}
```

## Code Metrics

| Aspect | Value |
|--------|-------|
| Total Files | 15 |
| Average File Size | 40-60 lines |
| Largest File | ShortestPathResult.java (90 lines) |
| Interfaces | 2 (Validator, WeightedGraphView) |
| Classes | 13 |
| Test Entry Points | 1 (Main.java) |

## Future Improvements

1. **Module Packages**: Group related files into packages (graph, algorithm, result)
2. **Unit Tests**: JUnit tests for each module
3. **Configuration File**: Load graph from JSON/YAML
4. **Visualization**: GraphViz output for path visualization
5. **Parallel Computation**: Multi-source shortest paths
6. **Weighted Variants**: Support directed/weighted variants
