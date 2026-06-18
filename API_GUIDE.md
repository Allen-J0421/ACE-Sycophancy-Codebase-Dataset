# API Documentation & Usage Guide

## Quick Start

### Minimal Example
```java
// 1. Build a graph
Graph graph = GraphBuilder.withVertexCount(5)
    .addEdge(0, 1, 4)
    .addEdge(1, 2, 3)
    .addEdge(0, 2, 8)
    .build();

// 2. Solve shortest paths
DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
ShortestPathResult result = solver.solve(graph, 0);

// 3. Access results
List<Integer> distances = result.getDistances();  // [0, 4, 7, ...]
```

## Core Classes

### Graph
**Factory Method**
```java
Graph graph = Graph.create(vertexCount);
```

**Adding Edges**
```java
graph.addEdge(source, destination, weight);
// Undirected: also adds edge from destination to source
```

**Query Methods**
```java
int vertexCount = graph.getVertexCount();
List<Edge> neighbors = graph.getAdjacencyListFor(vertexNode);  // All edges from node
```

**Error Handling**
```java
try {
    graph.addEdge(0, 10, 5);  // Invalid destination
} catch (IllegalArgumentException e) {
    System.out.println("Invalid vertex: " + e.getMessage());
}
```

### GraphBuilder
**Fluent API**
```java
Graph graph = GraphBuilder.withVertexCount(5)
    .addEdge(0, 1, 10)
    .addEdge(1, 2, 20)
    .addEdge(2, 3, 30)
    .build();
```

**Chaining**
```java
GraphBuilder builder = GraphBuilder.withVertexCount(10);
for (int i = 0; i < 9; i++) {
    builder.addEdge(i, i + 1, 1);  // Returns builder for chaining
}
Graph graph = builder.build();
```

### Edge
**Factory**
```java
Edge edge = Edge.of(destinationNode, weight);
```

**Accessors**
```java
int dest = edge.getDestination();
int weight = edge.getWeight();
```

**Constraints**
- Weight must be non-negative
- Validates on creation

### DijkstraShortestPathSolver
**Single Method**
```java
ShortestPathResult solve(WeightedGraphView graph, int sourceNode)
    throws NullPointerException,
           IllegalArgumentException;
```

**Parameters**
- `graph`: Non-null weighted graph
- `sourceNode`: Valid vertex index [0, vertexCount)

**Returns**
- Result object with distances, paths, and metadata

**Time Complexity**: O((V + E) log V)
**Space Complexity**: O(V)

### ShortestPathResult
**Distance Queries**
```java
// Single distance
Optional<Integer> dist = result.getDistanceTo(node);
dist.ifPresent(d -> System.out.println("Distance: " + d));

// All distances
List<Integer> allDistances = result.getDistances();

// Reachability check
boolean reachable = result.isReachable(node);
```

**Path Queries**
```java
// Single path
Optional<Path> path = result.getPathTo(destination);
path.ifPresent(p -> System.out.println("Path: " + p.getNodes()));

// All paths
Map<Integer, Optional<Path>> allPaths = result.getAllPaths();
allPaths.forEach((node, pathOpt) -> 
    pathOpt.ifPresent(p -> System.out.println("To " + node + ": " + p))
);
```

**Source Information**
```java
int source = result.getSourceNode();
```

### Path
**Accessors**
```java
List<Integer> nodes = path.getNodes();           // Unmodifiable
int distance = path.getTotalDistance();
int length = path.getLength();                    // Number of nodes
```

**Queries**
```java
if (path.contains(nodeId)) {
    System.out.println("Node is on path");
}
```

**String Representation**
```java
System.out.println(path);
// Output: Path(nodes=[0, 1, 2, 3], distance=9)
```

### AlgorithmConfig
**Creation**
```java
// Basic: distances and paths only
AlgorithmConfig config = AlgorithmConfig.create(sourceNode);

// Verbose: includes summary statistics
AlgorithmConfig config = AlgorithmConfig.createVerbose(sourceNode);
```

**Checking Options**
```java
if (config.shouldTrackPaths()) {
    // Path reconstruction enabled
}

if (config.isVerbose()) {
    // Verbose output enabled
}
```

### ResultFormatter
**Initialization**
```java
ResultFormatter formatter = new ResultFormatter(config);
```

**Output Methods**
```java
// Print distance vector
formatter.printDistances(result);
// Output: 0 4 7 9 10 

// Print all shortest paths
formatter.printPaths(result);
// Output: To node 1: [0, 1] (distance=4, hops=1)
//         To node 2: [0, 1, 2] (distance=7, hops=2)
//         ...

// Print summary statistics (only if verbose)
formatter.printSummary(result);
// Output: Summary: From node 0, 5/5 nodes are reachable
```

**Format Methods**
```java
String formatted = formatter.formatResult(result);
// Returns: [0, 4, 7, 9, 10]
```

### ExecutionMetrics
**Tracking**
```java
MetricsTrackingSolver solver = new MetricsTrackingSolver();
ShortestPathResult result = solver.solveWithMetrics(graph, sourceNode);
ExecutionMetrics metrics = solver.getMetrics();
```

**Metrics Available**
```java
long timeMs = metrics.getExecutionTimeMillis();
long timeMicros = metrics.getExecutionTimeMicros();
int verticesProcessed = metrics.getVerticesProcessed();
int edgesProcessed = metrics.getEdgesProcessed();
int enqueues = metrics.getEnqueueCount();
int dequeues = metrics.getDequeueCount();
int relaxations = metrics.getRelaxationCount();
```

**Display**
```java
System.out.println(metrics);
// Output:
// ExecutionMetrics {
//   Time: 0 ms (461 µs)
//   Vertices processed: 5
//   Edges processed: 12
//   Queue enqueues: 6
//   Queue dequeues: 6
//   Edge relaxations: 5
// }
```

## Usage Patterns

### Pattern 1: Basic Distance Calculation
```java
Graph graph = GraphBuilder.withVertexCount(5)
    .addEdge(0, 1, 4)
    .addEdge(1, 2, 3)
    .build();

DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
ShortestPathResult result = solver.solve(graph, 0);

System.out.println(result.getDistances());  // [0, 4, 7, ...]
```

### Pattern 2: Path Reconstruction
```java
ShortestPathResult result = solver.solve(graph, 0);

for (int i = 0; i < result.getDistances().size(); i++) {
    result.getPathTo(i).ifPresent(path ->
        System.out.println("To " + i + ": " + path.getNodes() + 
                         " (distance=" + path.getTotalDistance() + ")")
    );
}
```

### Pattern 3: Filtering Results
```java
ShortestPathResult result = solver.solve(graph, 0);

// Find all nodes within distance 10
List<Integer> close = new ArrayList<>();
for (int i = 0; i < result.getDistances().size(); i++) {
    result.getDistanceTo(i)
        .filter(d -> d <= 10)
        .ifPresent(d -> close.add(i));
}
```

### Pattern 4: Performance Analysis
```java
MetricsTrackingSolver solver = new MetricsTrackingSolver();
ShortestPathResult result = solver.solveWithMetrics(graph, 0);

System.out.println("Time: " + solver.getMetrics().getExecutionTimeMillis() + "ms");
System.out.println("Relaxations: " + solver.getMetrics().getRelaxationCount());
```

### Pattern 5: Batch Path Operations
```java
ShortestPathResult result = solver.solve(graph, 0);
Map<Integer, Optional<Path>> allPaths = result.getAllPaths();

// Filter paths that contain node 2
allPaths.entrySet().stream()
    .filter(e -> e.getValue().map(p -> p.contains(2)).orElse(false))
    .forEach(e -> System.out.println("Node " + e.getKey() + 
                                    " path contains node 2"));
```

## Common Scenarios

### Scenario 1: Finding Shortest Route
```java
// Get path from A to B
Optional<Path> path = result.getPathTo(B);
path.ifPresentOrElse(
    p -> System.out.println("Route: " + p.getNodes()),
    () -> System.out.println("No route found")
);
```

### Scenario 2: Checking Connectivity
```java
// Which nodes are reachable?
List<Integer> reachable = new ArrayList<>();
for (int i = 0; i < result.getDistances().size(); i++) {
    if (result.isReachable(i)) {
        reachable.add(i);
    }
}
System.out.println("Reachable: " + reachable);
```

### Scenario 3: Comparing Algorithms
```java
// Standard algorithm
DijkstraShortestPathSolver solver1 = new DijkstraShortestPathSolver();
ShortestPathResult result1 = solver1.solve(graph, 0);

// With metrics
MetricsTrackingSolver solver2 = new MetricsTrackingSolver();
ShortestPathResult result2 = solver2.solveWithMetrics(graph, 0);

System.out.println("Standard: " + result1.getDistances());
System.out.println("Metrics: " + result2.getDistances());
System.out.println("Performance: " + solver2.getMetrics());
```

### Scenario 4: Custom Output
```java
ShortestPathResult result = solver.solve(graph, 0);

// Format as JSON-like output
System.out.println("{");
result.getAllPaths().forEach((node, pathOpt) ->
    pathOpt.ifPresent(p -> 
        System.out.println("  \"" + node + "\": {\"distance\": " + 
                         p.getTotalDistance() + ", \"path\": " + 
                         p.getNodes() + "}")
    )
);
System.out.println("}");
```

## Error Recovery

### Safe Graph Building
```java
try {
    Graph graph = Graph.create(5);
    graph.addEdge(0, 1, 10);
    graph.addEdge(1, 2, 20);
} catch (IllegalArgumentException e) {
    System.err.println("Graph error: " + e.getMessage());
    // Handle error appropriately
}
```

### Safe Path Access
```java
Optional<Path> path = result.getPathTo(node);
if (path.isPresent()) {
    processPath(path.get());
} else {
    System.out.println("Node " + node + " is unreachable");
}
```

### Safe Algorithm Execution
```java
try {
    ShortestPathResult result = solver.solve(graph, sourceNode);
    processResult(result);
} catch (NullPointerException e) {
    System.err.println("Graph is required");
} catch (IllegalArgumentException e) {
    System.err.println("Invalid source node: " + e.getMessage());
}
```
