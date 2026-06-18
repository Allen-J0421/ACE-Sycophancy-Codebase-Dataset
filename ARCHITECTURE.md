# Graph Traversal Architecture

## Overview
A production-ready graph library implementing multiple design patterns for extensibility, maintainability, and robust error handling.

## Core Components

### 1. Graph Abstraction Layer
- **Graph** (interface): Defines contract for all graph implementations
- **AbstractGraph** (abstract): Shared initialization and validation logic
- **UndirectedGraph**: Bidirectional edge implementation
- **DirectedGraph**: Unidirectional edge implementation

### 2. Traversal Algorithms
- **GraphTraversal** (interface): Strategy pattern for different traversal algorithms
- **AbstractTraversal** (abstract): Common traversal logic and component tracking
- **BreadthFirstSearch**: Level-order traversal
- **DepthFirstSearch**: Recursive depth-first traversal

### 3. Builder & Configuration
- **GraphBuilder**: Fluent API for graph construction
  - `GraphBuilder.undirected(n)` - Create undirected graph
  - `GraphBuilder.directed(n)` - Create directed graph
  - `.addEdge(u, v)` - Chain edge additions
  - `.build()` - Get final graph
  
- **TraversalBuilder**: Fluent API for traversal configuration
  - `.withStrategy(GraphTraversal)` - Select algorithm
  - `.addVisitor(VertexVisitor)` - Attach visitors
  - `.execute()` - Run traversal with notifications

### 4. Visitor Pattern
- **VertexVisitor** (interface): Defines vertex processing contract
- Detached from traversal logic
- Multiple visitors can be attached to single traversal
- Supports custom processing (logging, counting, collecting, etc.)

### 5. Analysis & Metrics
- **GraphAnalyzer**: Extracts graph statistics
  - Component counting
  - Edge counting
  - Degree calculation
  
- **GraphMetrics**: Value object encapsulating analysis results
  - Min/Max/Average degree
  - Component count
  - Vertex degree mapping

### 6. Exception Handling
- **GraphException**: Base exception class
- **InvalidVertexException**: Thrown when vertex is out of range
- **InvalidGraphConfigurationException**: Thrown on invalid setup
- Meaningful error messages with context

## Design Patterns Used

| Pattern | Components | Purpose |
|---------|-----------|---------|
| Strategy | GraphTraversal + BFS/DFS | Swap algorithms at runtime |
| Builder | GraphBuilder, TraversalBuilder | Fluent, readable graph/traversal setup |
| Visitor | VertexVisitor | Decouple traversal from processing |
| Template Method | AbstractTraversal | Define algorithm skeleton in parent |
| Abstract Factory | AbstractGraph | Consistent interface for graph types |

## Usage Examples

### Create and Traverse Graph
```java
Graph graph = GraphBuilder.undirected(6)
    .addEdge(1, 2)
    .addEdge(2, 0)
    .addEdge(0, 3)
    .addEdge(4, 5)
    .build();

TraversalResult result = new TraversalBuilder(graph)
    .withStrategy(new BreadthFirstSearch())
    .addVisitor(v -> System.out.println("Visiting: " + v))
    .execute();
```

### Analyze Graph
```java
GraphAnalyzer analyzer = new GraphAnalyzer(graph);
GraphMetrics metrics = analyzer.analyze();
metrics.print();
```

### Error Handling
```java
try {
    graph.addEdge(0, 100);  // Invalid vertex
} catch (GraphException.InvalidVertexException e) {
    System.out.println(e.getMessage());
}
```

## Class Hierarchy

```
Graph (interface)
  ├── AbstractGraph (abstract)
  │   ├── UndirectedGraph
  │   └── DirectedGraph
  
GraphTraversal (interface)
  └── AbstractTraversal (abstract)
      ├── BreadthFirstSearch
      └── DepthFirstSearch

VertexVisitor (interface)
  └── [Custom implementations]

GraphException (extends RuntimeException)
  ├── InvalidVertexException
  └── InvalidGraphConfigurationException
```

## Key Benefits

1. **Separation of Concerns**: Graph structure, traversal algorithms, and analysis are independent
2. **Open/Closed Principle**: Easy to add new graph types and algorithms without modifying existing code
3. **Liskov Substitution**: All graphs can be used interchangeably
4. **Dependency Inversion**: Builders and analyzers depend on interfaces, not concrete classes
5. **Single Responsibility**: Each class has one reason to change
6. **Extensibility**: Visitors enable custom processing without core modifications
7. **Robustness**: Comprehensive error handling with meaningful messages

## File Organization

- Core interfaces: `Graph.java`, `GraphTraversal.java`, `VertexVisitor.java`
- Base implementations: `AbstractGraph.java`, `AbstractTraversal.java`
- Graph types: `UndirectedGraph.java`, `DirectedGraph.java`
- Algorithms: `BreadthFirstSearch.java`, `DepthFirstSearch.java`
- Builders: `GraphBuilder.java`, `TraversalBuilder.java`
- Analysis: `GraphAnalyzer.java`, `GraphMetrics.java`
- Results: `TraversalResult.java`
- Errors: `GraphException.java`
- Entry point: `Main.java`

## Future Extensions

The architecture supports easy addition of:
- Weighted graphs (modify Graph interface minimally)
- Additional algorithms (DFS iterative, Dijkstra, Prim's, Kruskal's)
- Visualization visitors (generate dot files, ASCII art)
- Graph validation visitors (check for cycles, bipartiteness)
- Performance monitoring visitors (timing, throughput)
