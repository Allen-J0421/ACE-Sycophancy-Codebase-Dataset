# Error Handling & Validation Guide

## Overview
The codebase implements comprehensive error handling through validation abstractions and clear exception contracts.

## Validation Strategy

### 1. Input Validation at Boundaries

**Vertex Validation**
```java
// Validates vertex indices are within [0, vertexCount)
VertexValidator validator = new VertexValidator(5);
validator.validate(3);   // OK
validator.validate(-1);  // IllegalArgumentException: Vertex -1 is out of range [0, 5)
validator.validate(5);   // IllegalArgumentException: Vertex 5 is out of range [0, 5)
```

**Edge Weight Validation**
```java
// Validates weights are non-negative (required for Dijkstra)
Edge edge = Edge.of(1, 10);  // OK
Edge edge = Edge.of(1, -5);  // IllegalArgumentException: Edge weight must be non-negative, got -5
```

### 2. Graph Construction Validation

```java
// Positive vertex count required
Graph graph = Graph.create(0);   // IllegalArgumentException: Vertex count must be positive

// No self-loops allowed
graph.addEdge(2, 2, 5);          // IllegalArgumentException: Self-loops are not supported

// Valid vertices required
graph.addEdge(0, 10, 5);         // IllegalArgumentException: Vertex 10 is out of range [0, 5)
```

### 3. Algorithm Input Validation

```java
DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();

// Null graph check
solver.solve(null, 0);           // NullPointerException: Graph cannot be null

// Source node bounds check
solver.solve(graph, -1);         // IllegalArgumentException: Source node -1 is invalid...
solver.solve(graph, 10);         // IllegalArgumentException: Source node 10 is invalid...
```

## Error Types

### IllegalArgumentException
Used for domain constraint violations:
- Out-of-bounds vertex indices
- Negative edge weights
- Invalid graph configuration (vertex count ≤ 0)
- Self-loops in undirected graphs
- Invalid source node

**When to catch:**
```java
try {
    graph.addEdge(0, 1, -5);
} catch (IllegalArgumentException e) {
    System.out.println("Invalid edge weight: " + e.getMessage());
}
```

### NullPointerException
Used for null inputs at system boundaries:
- Graph passed to solver
- Critical dependencies

**When to catch:**
```java
try {
    solver.solve(null, 0);
} catch (NullPointerException e) {
    System.out.println("Graph required: " + e.getMessage());
}
```

## Validation Framework

### Generic Validator Interface
```java
interface Validator<T> {
    void validate(T value) throws IllegalArgumentException;
}
```

### Custom Validators
Extend the framework by implementing the interface:

```java
// Example: Distance limit validator
class DistanceLimitValidator implements Validator<Integer> {
    private final int maxDistance;
    
    DistanceLimitValidator(int maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    @Override
    public void validate(Integer distance) throws IllegalArgumentException {
        if (distance > maxDistance) {
            throw new IllegalArgumentException(
                String.format("Distance %d exceeds limit %d", distance, maxDistance)
            );
        }
    }
}
```

## Safe Access Patterns

### 1. Optional for Nullable Results
```java
// Safe distance query
Optional<Integer> distance = result.getDistanceTo(node);
distance.ifPresent(d -> System.out.println("Distance: " + d));
distance.orElse(Integer.MAX_VALUE);  // Default for unreachable
```

### 2. Optional for Path Queries
```java
// Safe path access
Optional<Path> path = result.getPathTo(destination);

// Pattern 1: Check and use
if (path.isPresent()) {
    System.out.println(path.get());
}

// Pattern 2: Functional style
path.ifPresent(p -> System.out.println("Path: " + p.getNodes()));

// Pattern 3: With default
path.ifPresentOrElse(
    p -> System.out.println("Found: " + p),
    () -> System.out.println("Node unreachable")
);
```

### 3. Batch Operations
```java
// Get all paths at once
Map<Integer, Optional<Path>> allPaths = result.getAllPaths();
allPaths.forEach((node, pathOpt) -> 
    pathOpt.ifPresent(path -> System.out.println("To " + node + ": " + path))
);
```

## Constraint Enforcement

### Domain Constraints
1. **Non-negative weights**: Enforced by `EdgeWeightValidator`
2. **Non-negative distances**: Enforced by `PriorityQueueEntry`
3. **Positive vertex count**: Enforced by `Graph` constructor
4. **No self-loops**: Enforced by `Graph.addEdge()`
5. **Valid vertices**: Enforced by `VertexValidator`

### Implementation Pattern
```java
// Example from Edge.java
private Edge(int destination, int weight) {
    WEIGHT_VALIDATOR.validate(weight);  // Throw early
    this.destination = destination;
    this.weight = weight;
}
```

## Testing Validation

### Unit Test Examples
```java
@Test
void testVertexValidation() {
    VertexValidator validator = new VertexValidator(5);
    
    // Valid vertices
    validator.validate(0);
    validator.validate(4);
    
    // Invalid vertices
    assertThrows(IllegalArgumentException.class, () -> validator.validate(-1));
    assertThrows(IllegalArgumentException.class, () -> validator.validate(5));
}

@Test
void testEdgeWeightValidation() {
    assertThrows(IllegalArgumentException.class, () -> Edge.of(1, -5));
    Edge validEdge = Edge.of(1, 0);  // 0 weight is valid
}

@Test
void testGraphConstraints() {
    Graph graph = Graph.create(5);
    
    // No self-loops
    assertThrows(IllegalArgumentException.class, () -> graph.addEdge(2, 2, 5));
    
    // Out of bounds
    assertThrows(IllegalArgumentException.class, () -> graph.addEdge(0, 10, 5));
}

@Test
void testAlgorithmInputValidation() {
    DijkstraShortestPathSolver solver = new DijkstraShortestPathSolver();
    Graph graph = Graph.create(5);
    
    // Null graph
    assertThrows(NullPointerException.class, () -> solver.solve(null, 0));
    
    // Invalid source
    assertThrows(IllegalArgumentException.class, () -> solver.solve(graph, -1));
}
```

## Error Message Quality

### Descriptive Messages
```java
// Good: Includes context and constraint
"Vertex 10 is out of range [0, 5)"
"Edge weight must be non-negative, got -5"
"Source node -1 is invalid for graph with 5 vertices"

// Bad: Vague
"Invalid vertex"
"Bad weight"
```

## Performance Impact

Validation overhead is minimal:
- Vertex validation: O(1) comparison
- Edge validation: O(1) comparison
- Graph validation: Amortized O(1) per operation

Validation should never be disabled for performance—prevent bad data early.

## Fail-Fast Philosophy

The codebase follows fail-fast principles:
- Validate immediately at entry points
- Throw exceptions early
- Prevent propagation of invalid state
- Make bugs easier to debug

Example:
```java
// Bad: Accept invalid input, fail later
void addEdge(int source, int destination, int weight) {
    adjacencyList.get(source).add(new Edge(destination, weight));
}

// Good: Validate immediately
void addEdge(int source, int destination, int weight) {
    vertexValidator.validate(source);
    vertexValidator.validate(destination);
    WEIGHT_VALIDATOR.validate(weight);
    adjacencyList.get(source).add(Edge.of(destination, weight));
}
```

## Guidelines for Extending

When adding new features:

1. **Identify domain constraints**: What values are invalid?
2. **Create validators**: Implement `Validator<T>` interface
3. **Apply at boundaries**: Validate inputs before use
4. **Use factory methods**: Encapsulate validation in factories
5. **Return Optional**: For nullable results
6. **Document exceptions**: Javadoc with `@throws`

Example extension:
```java
/**
 * New graph type: weighted directed graph
 */
class DirectedWeightedGraph implements WeightedGraphView {
    private final Graph delegate;
    
    void addDirectedEdge(int source, int destination, int weight) {
        vertexValidator.validate(source);
        vertexValidator.validate(destination);
        // Only add in one direction
        adjacencyList.get(source).add(Edge.of(destination, weight));
    }
}
```
