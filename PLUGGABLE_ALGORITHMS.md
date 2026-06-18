# Pluggable Algorithms Architecture

## Overview

The codebase now implements a strategy-based architecture using the `PathfindingAlgorithm` interface, enabling seamless swapping of algorithms without changing application logic.

**Key Achievement**: Main application logic remains unchanged while supporting multiple algorithms.

## Architecture

```
PathfindingAlgorithm (interface)
    ├── DijkstraShortestPathSolver
    ├── BellmanFordSolver
    └── [Future implementations]

AlgorithmFactory
    ├── Manages algorithm registration
    ├── Provides lazy instantiation
    └── Handles algorithm discovery

Main Application
    └── Uses factory to get algorithms
        └── No direct dependency on implementations
```

## Core Components

### 1. PathfindingAlgorithm Interface
```java
interface PathfindingAlgorithm {
    ShortestPathResult solve(WeightedGraphView graph, int sourceNode);
    String getName();
}
```

**Why an interface?**
- Defines contract for all algorithms
- Enables polymorphism
- Decouples client from implementations
- Easy to test with mocks

### 2. Algorithm Implementations

#### DijkstraShortestPathSolver
```java
class DijkstraShortestPathSolver implements PathfindingAlgorithm
```
- **Time**: O((V + E) log V)
- **Space**: O(V)
- **Constraints**: Non-negative weights required
- **Strengths**: Fast for sparse graphs, common graphs
- **Use Case**: General shortest path problems

#### BellmanFordSolver
```java
class BellmanFordSolver implements PathfindingAlgorithm
```
- **Time**: O(VE)
- **Space**: O(V)
- **Constraints**: Detects negative cycles
- **Strengths**: Handles negative weights
- **Use Case**: Currency arbitrage, protocol routing
- **Method**: `hasNegativeCycle()` to detect cycles

### 3. AlgorithmFactory
```java
class AlgorithmFactory {
    static PathfindingAlgorithm create(String name)
    static Optional<PathfindingAlgorithm> createOptional(String name)
    static void register(String name, AlgorithmProvider provider)
    static String listAlgorithms()
}
```

**Responsibilities:**
- Centralized algorithm registration
- Lazy instantiation via providers
- Algorithm discovery
- Extensibility for custom algorithms

### 4. MetricsTrackingSolver (Decorator Pattern)
```java
class MetricsTrackingSolver implements PathfindingAlgorithm {
    MetricsTrackingSolver(PathfindingAlgorithm delegate)
}
```

Wraps any algorithm to add metrics without modifying source.

## Usage Patterns

### Pattern 1: Using Factory (Recommended)
```java
PathfindingAlgorithm algorithm = AlgorithmFactory.create("dijkstra");
ShortestPathResult result = algorithm.solve(graph, 0);
System.out.println(algorithm.getName());  // "Dijkstra"
```

### Pattern 2: Direct Instantiation
```java
PathfindingAlgorithm algorithm = new DijkstraShortestPathSolver();
ShortestPathResult result = algorithm.solve(graph, 0);
```

### Pattern 3: With Metrics
```java
PathfindingAlgorithm algorithm = AlgorithmFactory.create("bellman-ford");
MetricsTrackingSolver metrics = new MetricsTrackingSolver(algorithm);
ShortestPathResult result = metrics.solve(graph, 0);
System.out.println(metrics.getMetrics());
```

### Pattern 4: Comparing Algorithms
```java
for (String name : new String[]{"dijkstra", "bellman-ford"}) {
    PathfindingAlgorithm algo = AlgorithmFactory.create(name);
    ShortestPathResult result = algo.solve(graph, 0);
    System.out.println(algo.getName() + ": " + result.getDistances());
}
```

## Command-Line Interface

The Main application demonstrates full command-line control:

```bash
# Default: Dijkstra
java Main

# Specify algorithm
java Main --dijkstra
java Main --bellman-ford

# With metrics
java Main --dijkstra --metrics
java Main --bellman-ford --metrics

# Compare all algorithms
java Main --all

# Help
java Main --help
```

## Adding New Algorithms

### Step 1: Implement Interface
```java
class MyAlgorithmSolver implements PathfindingAlgorithm {
    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        // Implementation
    }

    @Override
    public String getName() {
        return "My Algorithm";
    }
}
```

### Step 2: Register in Factory
```java
// In AlgorithmFactory static block or via register method
AlgorithmFactory.register("myalgo", () -> new MyAlgorithmSolver());
```

### Step 3: Use Immediately
```java
PathfindingAlgorithm algo = AlgorithmFactory.create("myalgo");
ShortestPathResult result = algo.solve(graph, 0);
```

## Extensibility Examples

### Example 1: A* Search
```java
class AStarSolver implements PathfindingAlgorithm {
    private final Heuristic heuristic;

    AStarSolver(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        // Use heuristic to guide search
    }

    @Override
    public String getName() {
        return "A*";
    }
}

// Register
AlgorithmFactory.register("astar", () -> new AStarSolver(euclideanDistance));
```

### Example 2: Floyd-Warshall (All-Pairs)
```java
class FloydWarshallSolver implements PathfindingAlgorithm {
    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        // Compute all-pairs shortest paths
        // Return single-source results for requested source
    }

    @Override
    public String getName() {
        return "Floyd-Warshall";
    }
}
```

### Example 3: Bidirectional Search
```java
class BidirectionalDijkstra implements PathfindingAlgorithm {
    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        // Search from both source and all targets simultaneously
    }

    @Override
    public String getName() {
        return "Bidirectional Dijkstra";
    }
}
```

## Testing Algorithms

### Test Template
```java
@Test
void testAlgorithm() {
    // Setup
    PathfindingAlgorithm algo = AlgorithmFactory.create("algorithm-name");
    Graph graph = buildTestGraph();

    // Execute
    ShortestPathResult result = algo.solve(graph, 0);

    // Assert
    assertEquals(expectedDistances, result.getDistances());
    assertEquals(expectedPaths, result.getAllPaths());
}

@Test
void testAlgorithmConsistency() {
    Graph graph = buildTestGraph();
    PathfindingAlgorithm dijkstra = AlgorithmFactory.create("dijkstra");
    PathfindingAlgorithm bellmanFord = AlgorithmFactory.create("bellman-ford");

    ShortestPathResult r1 = dijkstra.solve(graph, 0);
    ShortestPathResult r2 = bellmanFord.solve(graph, 0);

    assertEquals(r1.getDistances(), r2.getDistances());
}
```

## Performance Comparison

### Sample Results
| Algorithm | Time | Space | Min Weight | Notes |
|-----------|------|-------|-----------|-------|
| Dijkstra | O((V+E)logV) | O(V) | 0 | Fastest for sparse graphs |
| Bellman-Ford | O(VE) | O(V) | unlimited | Handles negative weights |
| A* | O(E) best | O(V) | 0 | With good heuristic |
| Floyd-Warshall | O(V³) | O(V²) | unlimited | All-pairs computation |

## Benefits of This Architecture

### 1. Separation of Concerns
- Algorithm logic isolated
- Main application doesn't know about implementations
- Easy to modify algorithms without affecting client code

### 2. Flexibility
- Swap algorithms without recompilation
- Runtime algorithm selection via configuration
- Easy to test different algorithms

### 3. Extensibility
- Add new algorithms without modifying existing code
- Follow Open/Closed Principle
- Support future variants and optimizations

### 4. Testability
- Mock algorithms for testing Main
- Compare algorithm outputs
- Validate against reference implementations

### 5. Reusability
- Algorithms work with any WeightedGraphView
- Can be used in different applications
- No coupling to specific graph types

## Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|---|---------|
| Strategy | PathfindingAlgorithm | Pluggable algorithms |
| Factory | AlgorithmFactory | Algorithm instantiation |
| Decorator | MetricsTrackingSolver | Add metrics to any algorithm |
| Adapter | WeightedGraphView | Abstract graph types |
| Dependency Injection | Main class | Runtime algorithm selection |

## Constraints & Considerations

### When to Use Each Algorithm

**Use Dijkstra when:**
- Graph has non-negative weights ✓
- Performance is critical
- Common shortest path queries
- Sparse graph

**Use Bellman-Ford when:**
- Graph may have negative weights
- Need to detect negative cycles
- All-pairs needed (with modification)
- Dense graph acceptable

**Use A* when:**
- Have good heuristic function
- Single target known
- Euclidean space applicable
- Need optimal path fast

### Limitations

- **Dijkstra**: Cannot handle negative weights
- **Bellman-Ford**: Slower than Dijkstra for non-negative weights
- **A\***: Requires good heuristic for efficiency
- **Floyd-Warshall**: O(V³) space not suitable for large graphs

## Backward Compatibility

Existing code still works:
```java
// Old way still works
DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
ShortestPathResult result = solver.solve(graph, 0);

// New way via factory
PathfindingAlgorithm algorithm = AlgorithmFactory.create("dijkstra");
ShortestPathResult result = algorithm.solve(graph, 0);
```

## Future Enhancements

1. **Algorithm Metadata**
   ```java
   interface AlgorithmMetadata {
       AlgorithmProperties getProperties();  // Time, space, constraints
       List<String> getConstraints();
   }
   ```

2. **Adaptive Algorithm Selection**
   ```java
   class AdaptiveAlgorithmChooser {
       PathfindingAlgorithm chooseAlgorithm(Graph graph) {
           // Select algorithm based on graph properties
       }
   }
   ```

3. **Algorithm Benchmarking**
   ```java
   class AlgorithmBenchmark {
       void compareAlgorithms(Graph graph, int iterations) {
           // Run and time each algorithm
       }
   }
   ```

4. **Cached Results**
   ```java
   class CachingAlgorithm implements PathfindingAlgorithm {
       // Cache results for repeated queries
   }
   ```

## Migration Guide

### For Users
No changes needed for basic usage. Main application logic unchanged.

### For Developers Extending
1. Implement `PathfindingAlgorithm` interface
2. Implement `solve()` method
3. Implement `getName()` method
4. Register in factory: `AlgorithmFactory.register("name", provider)`
5. Use: `AlgorithmFactory.create("name")`

## Summary

The pluggable architecture provides:
- ✓ Algorithm independence
- ✓ Easy algorithm swapping
- ✓ Runtime algorithm selection
- ✓ Clean client code
- ✓ Extensible design
- ✓ Testable implementations
- ✓ Zero impact on existing code

**Main benefit**: Add new algorithms or swap implementations without touching application logic.
