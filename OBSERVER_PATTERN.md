# Observer Pattern Implementation

## Overview

This document describes the Observer pattern implementation for real-time monitoring of graph modifications and algorithm progress without coupling core logic to side-effect-heavy systems like UI or logging.

**Key Achievement**: Core pathfinding and graph logic remain completely decoupled from logging, visualization, and monitoring systems.

## Problem Solved

### Before Observer Pattern
```
Graph/Algorithm Logic
    ↓
[Tightly coupled to logging/visualization]
    ↓
System output, visualization, statistics
```

**Issues**:
- Side effects mixed with core logic
- Hard to disable observers
- Hard to add new monitoring systems
- Tests require mocking side effects
- Logic reuse difficult

### After Observer Pattern
```
Graph/Algorithm Logic
    ↓
Observer Interface
    ├── LoggingGraphObserver
    ├── StatisticsGraphObserver
    ├── [Custom observers]
    └── LoggingAlgorithmObserver
        ├── StatisticsAlgorithmObserver
        └── [Custom observers]
```

**Benefits**:
- Pure logic, no side effects
- Easy to enable/disable observers
- Easy to add new monitoring
- Clean separation of concerns
- Testable in isolation

## Core Components

### 1. GraphObserver Interface

```java
interface GraphObserver {
    void onEdgeAdded(int source, int destination, int weight);
    void onGraphCreated(int vertexCount);
}
```

**Purpose**: Notify observers when graph structure changes

**Events**:
- `onGraphCreated`: Graph initialization
- `onEdgeAdded`: Each edge addition

### 2. AlgorithmObserver Interface

```java
interface AlgorithmObserver {
    void onAlgorithmStart(String algorithmName, int sourceNode, int vertexCount);
    void onVertexProcessed(int vertex, int distance);
    void onEdgeRelaxed(int from, int to, int oldDistance, int newDistance, int relaxationCount);
    void onAlgorithmComplete(int[] finalDistances, int totalRelaxations, long executionTimeMillis);
}
```

**Purpose**: Notify observers of algorithm progress

**Events**:
- `onAlgorithmStart`: Execution begins
- `onVertexProcessed`: Vertex dequeued
- `onEdgeRelaxed`: Distance improved
- `onAlgorithmComplete`: Execution ends

### 3. ObservableGraph (Decorator)

```java
class ObservableGraph implements WeightedGraphView {
    void subscribe(GraphObserver observer)
    boolean unsubscribe(GraphObserver observer)
    void addEdge(int source, int destination, int weight)
}
```

**Pattern**: Decorator + Observer
**Purpose**: Wrap Graph and notify observers of changes

**Usage**:
```java
Graph graph = Graph.create(5);
ObservableGraph observable = new ObservableGraph(graph);
observable.subscribe(new LoggingGraphObserver());
observable.addEdge(0, 1, 4);  // Triggers notifications
```

### 4. ObservablePathfindingAlgorithm (Decorator)

```java
class ObservablePathfindingAlgorithm implements PathfindingAlgorithm {
    void subscribe(AlgorithmObserver observer)
    boolean unsubscribe(AlgorithmObserver observer)
    ShortestPathResult solve(WeightedGraphView graph, int sourceNode)
}
```

**Pattern**: Decorator + Observer
**Purpose**: Wrap algorithm and notify observers of progress

**Usage**:
```java
PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
ObservablePathfindingAlgorithm observable = 
    new ObservablePathfindingAlgorithm(dijkstra);
observable.subscribe(new LoggingAlgorithmObserver());
observable.solve(graph, 0);  // Triggers notifications
```

## Concrete Observers

### LoggingGraphObserver

Logs all graph modifications:

```
[GraphObserver] Edge #1 added: 0 → 1 (weight=4)
[GraphObserver] Edge #2 added: 0 → 2 (weight=8)
[GraphObserver] Edge #3 added: 1 → 2 (weight=3)
```

**Usage**:
```java
graph.subscribe(new LoggingGraphObserver());
```

### StatisticsGraphObserver

Collects graph statistics:

```java
StatisticsGraphObserver stats = new StatisticsGraphObserver();
graph.subscribe(stats);
graph.addEdge(0, 1, 4);
// ...
System.out.println(stats);
// Output: GraphStats{vertices=5, edges=6, density=40%, weights=[2..10, avg=5.5]}
```

**Statistics**:
- Total vertices
- Total edges
- Graph density
- Min/max/avg weights
- Total weight sum

### LoggingAlgorithmObserver

Logs algorithm progress:

```
[AlgorithmObserver] Starting Dijkstra from node 0 (5 vertices)
[AlgorithmObserver] Algorithm complete in 7 ms
[AlgorithmObserver] Final distances: [0, 4, 7, 9, 10]
[AlgorithmObserver] Total relaxations: 5
```

**Usage**:
```java
algorithm.subscribe(new LoggingAlgorithmObserver());
```

### StatisticsAlgorithmObserver

Collects algorithm execution statistics:

```java
StatisticsAlgorithmObserver stats = new StatisticsAlgorithmObserver();
algorithm.subscribe(stats);
algorithm.solve(graph, 0);
// ...
System.out.println(stats);
// Output: AlgorithmStats{Dijkstra from 0, vertices=5, relaxations=5, time=7 ms}
```

**Statistics**:
- Algorithm name
- Source node
- Vertices processed
- Total relaxations
- Reachable node count
- Average distance
- Execution time

## Design Patterns Used

### 1. Observer Pattern
- Multiple observers notified of same event
- Observers implement interfaces
- Subject maintains observer list

### 2. Decorator Pattern
- Observable* classes wrap originals
- Add observability without inheritance
- Maintain same interface as wrapped class

### 3. Strategy Pattern (for observers)
- Different observers implement same interface
- Can be plugged in at runtime
- Easy to add new observer types

## Usage Examples

### Example 1: Graph Monitoring

```java
// Create observable graph
Graph baseGraph = Graph.create(5);
ObservableGraph graph = new ObservableGraph(baseGraph);

// Attach observers
graph.subscribe(new LoggingGraphObserver());
graph.subscribe(new StatisticsGraphObserver());

// Build graph (observers get notifications)
graph.addEdge(0, 1, 4);
graph.addEdge(0, 2, 8);
graph.addEdge(1, 2, 3);
```

**Output**:
```
[GraphObserver] Edge #1 added: 0 → 1 (weight=4)
[GraphObserver] Edge #2 added: 0 → 2 (weight=8)
[GraphObserver] Edge #3 added: 1 → 2 (weight=3)
```

### Example 2: Algorithm Monitoring

```java
// Create observable algorithm
PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
ObservablePathfindingAlgorithm algorithm = 
    new ObservablePathfindingAlgorithm(dijkstra);

// Attach observers
algorithm.subscribe(new LoggingAlgorithmObserver());
algorithm.subscribe(new StatisticsAlgorithmObserver());

// Solve (observers get notifications)
ShortestPathResult result = algorithm.solve(graph, 0);
```

### Example 3: Selective Observation

```java
ObservableGraph graph = new ObservableGraph(Graph.create(5));
StatisticsGraphObserver stats = new StatisticsGraphObserver();

// Subscribe
graph.subscribe(stats);
graph.addEdge(0, 1, 4);
graph.addEdge(0, 2, 8);

// Unsubscribe
graph.unsubscribe(stats);
graph.addEdge(1, 2, 3);  // stats won't see this

// Get statistics
System.out.println(stats);  // Only 2 edges counted
```

### Example 4: Custom Observer

```java
class CustomGraphObserver implements GraphObserver {
    @Override
    public void onGraphCreated(int vertexCount) {
        System.out.println("Custom: Graph with " + vertexCount + " vertices");
    }

    @Override
    public void onEdgeAdded(int source, int destination, int weight) {
        System.out.println("Custom: Edge " + source + "->" + destination);
    }
}

// Use custom observer
ObservableGraph graph = new ObservableGraph(Graph.create(5));
graph.subscribe(new CustomGraphObserver());
graph.addEdge(0, 1, 4);  // Triggers custom notification
```

## Command-Line Interface

```bash
# Run with observers enabled
java Main --observe

# Output shows:
# 1. Graph construction with logging
# 2. Graph statistics
# 3. Algorithm execution with logging
# 4. Algorithm statistics
```

## Testing with Observers

Observers can be used in tests without affecting production code:

```java
@Test
void testGraphConstruction() {
    StatisticsGraphObserver stats = new StatisticsGraphObserver();
    ObservableGraph graph = new ObservableGraph(Graph.create(5));
    graph.subscribe(stats);

    graph.addEdge(0, 1, 4);
    graph.addEdge(0, 2, 8);

    assertEquals(2, stats.getTotalEdges());
    assertEquals(8, stats.getMaxWeight());
}
```

## Extension Points

### Add Graph Observer

```java
class ValidationGraphObserver implements GraphObserver {
    @Override
    public void onGraphCreated(int vertexCount) {
        if (vertexCount <= 0) throw new IllegalArgumentException("Invalid");
    }

    @Override
    public void onEdgeAdded(int source, int destination, int weight) {
        if (weight < 0) throw new IllegalArgumentException("Negative weight");
    }
}
```

### Add Algorithm Observer

```java
class PerformanceAlgorithmObserver implements AlgorithmObserver {
    @Override
    public void onAlgorithmStart(String name, int source, int vertexCount) {
        System.out.println("Starting performance measurement...");
    }

    @Override
    public void onAlgorithmComplete(int[] distances, int relaxations, long time) {
        double opsPerMs = (double) relaxations / time;
        System.out.println("Performance: " + opsPerMs + " ops/ms");
    }

    // Other methods...
}
```

## Real-World Applications

### 1. Visualization Systems
```java
class VisualizationGraphObserver implements GraphObserver {
    void onEdgeAdded(int source, int destination, int weight) {
        // Redraw edge on canvas
        canvas.drawEdge(source, destination, weight);
    }
}
```

### 2. Audit Logging
```java
class AuditGraphObserver implements GraphObserver {
    void onEdgeAdded(int source, int destination, int weight) {
        auditLog.record("Edge added", source, destination, weight);
    }
}
```

### 3. Performance Monitoring
```java
class MonitoringAlgorithmObserver implements AlgorithmObserver {
    void onAlgorithmComplete(int[] distances, int relaxations, long time) {
        metrics.recordAlgorithmRun(relaxations, time);
    }
}
```

### 4. Live Progress Bar
```java
class ProgressAlgorithmObserver implements AlgorithmObserver {
    void onAlgorithmStart(String name, int source, int vertexCount) {
        progressBar.setMax(vertexCount);
    }

    void onVertexProcessed(int vertex, int distance) {
        progressBar.increment();
    }
}
```

## Comparison: Before vs After

### Before (Tightly Coupled)
```java
class DijkstraShortestPathSolver {
    ShortestPathResult solve(Graph graph, int source) {
        System.out.println("Starting...");  // Hard-coded logging
        // ... algorithm logic ...
        System.out.println("Complete");    // Hard-coded logging
    }
}
```

**Issues**:
- Logging mixed with algorithm
- Can't disable logging
- Can't add different outputs
- Hard to test

### After (Loosely Coupled)
```java
PathfindingAlgorithm algorithm = new DijkstraShortestPathSolver();
ObservablePathfindingAlgorithm observable = 
    new ObservablePathfindingAlgorithm(algorithm);
observable.subscribe(new LoggingAlgorithmObserver());
observable.solve(graph, 0);
```

**Benefits**:
- Pure algorithm logic
- Optional observers
- Multiple observer types
- Easy testing

## Performance Impact

**Overhead**: Minimal (negligible for most applications)

- Observer subscription: O(1) per observer
- Notification: O(n) where n = observer count
- No impact on algorithm complexity
- Observers can be disabled at runtime

## Future Enhancements

1. **Observer Groups** - Enable/disable sets of observers
2. **Filtered Observers** - Receive only specific events
3. **Async Observers** - Non-blocking notifications
4. **Observer Composition** - Combine multiple observers
5. **Event Batching** - Reduce notification overhead

## Summary

The Observer pattern implementation provides:

- ✓ **Loose Coupling**: Core logic independent of observers
- ✓ **Flexibility**: Easy to add/remove observers at runtime
- ✓ **Extensibility**: Simple to implement custom observers
- ✓ **Testability**: Can test logic without side effects
- ✓ **Reusability**: Observers work with any graph/algorithm
- ✓ **Scalability**: Add new monitoring without code changes

**Key Takeaway**: Side effects (logging, visualization, monitoring) are completely decoupled from core pathfinding logic via the Observer pattern.
