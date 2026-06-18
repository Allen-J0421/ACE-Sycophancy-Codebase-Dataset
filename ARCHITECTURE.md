# Graph Traversal Architecture

## Overview
An enterprise-grade graph library implementing advanced design patterns including event-driven architecture, caching, fluent queries, and comprehensive monitoring capabilities.

## Layered Architecture

```
┌─────────────────────────────────────────────┐
│        Application Layer (Main)             │
│  - Demonstrates all capabilities           │
│  - Shows typical usage patterns            │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│    Service Layer (GraphService)            │
│  - Unified API facade                      │
│  - Orchestrates all components             │
└──────────────┬──────────────────────────────┘
               │
        ┌──────┴────────────────────────┬───────────────┐
        │                               │               │
┌───────▼──────┐  ┌──────────────┐  ┌──▼─────────┐  ┌─▼──────────┐
│  Query Layer │  │  Cache Layer │  │ Event Bus  │  │ Config     │
│ (GraphQuery) │  │              │  │            │  │ (Builder)  │
└───────┬──────┘  └──────────────┘  └──┬─────────┘  └────────────┘
        │                               │
    ┌───┴────────────────┬──────┬───────┴──────┐
    │                    │      │              │
┌───▼────┐    ┌──────────▼──┐  │  ┌──────────▼──────┐
│Traverse │    │Connectivity│  │  │  Degree Query   │
│ Query   │    │Query        │  │  │  Vertex Query   │
└─────────┘    └─────────────┘  │  └─────────────────┘
               
┌──────────────────────────────────────────────┐
│   Algorithm Layer (BFS/DFS)                 │
│   - Event publishing                        │
│   - Strategy implementations                │
└──────────────┬───────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│    Graph Model Layer                        │
│  - Graph abstraction                        │
│  - Validation and error handling            │
│  - UndirectedGraph/DirectedGraph            │
└───────────────────────────────────────────────┘
```

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

### 5. Event-Driven System
- **TraversalEvent**: Immutable event objects with timestamp and metadata
- **TraversalEventListener**: Subscriber interface for event notifications
- **TraversalEventBus**: Publish-subscribe broker with dynamic subscriptions
- Events published at: traversal start/end, vertex visit, component discovery
- Enables: monitoring, logging, metrics collection, real-time analytics

### 6. Caching Layer
- **TraversalCache**: TTL-based result caching with configurable expiration
  - Automatic expiration checking
  - Key generation from graph and strategy identity
  - Manual invalidation support
  - Cache statistics and size tracking
- Reduces redundant traversals
- Configurable per-traversal via TraversalConfig

### 7. Query API
- **GraphQuery**: Unified query entry point with fluent builders
- **TraversalQuery**: Chainable traversal configuration
  - `.usingBFS()` / `.usingDFS()` - Algorithm selection
  - `.withoutCache()` - Disable caching
  - `.execute()` - Run and return result
- **ConnectivityQuery**: Fast connectivity checks
  - Precomputed component assignment
  - O(1) connectivity lookups
- **VertexQuery**: Vertex filtering and analysis
  - Filter by degree properties
  - Leaf vertex detection
  - Existence checking

### 8. Configuration Management
- **TraversalConfig**: Builder for traversal configuration
  - Strategy selection (BFS/DFS)
  - Enable/disable caching
  - Event listeners setup
  - Vertex visitors attachment
  - Immutable configuration objects

### 9. Service Layer
- **GraphService**: Facade providing unified API
  - Coordinates all components
  - Handles event setup and cache management
  - Returns typed query objects
  - Manages cache lifecycle

### 10. Analysis & Metrics
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
| Builder | GraphBuilder, TraversalBuilder, TraversalConfig | Fluent configuration |
| Visitor | VertexVisitor | Decouple traversal from processing |
| Template Method | AbstractTraversal | Define algorithm skeleton in parent |
| Abstract Factory | AbstractGraph | Consistent interface for graph types |
| Facade | GraphService | Unified API over complex subsystem |
| Observer | TraversalEventBus + Listeners | Event notification system |
| Query | GraphQuery + subqueries | Fluent, chainable queries |
| Decorator | TraversalConfig | Add capabilities without modifying core |
| Repository | TraversalCache | Abstract data access and caching |

## Usage Examples

### Basic Graph Creation and Traversal
```java
Graph graph = GraphBuilder.undirected(6)
    .addEdge(1, 2)
    .addEdge(2, 0)
    .addEdge(0, 3)
    .addEdge(4, 5)
    .build();

GraphService service = new GraphService(graph);

// Traverse with BFS, caching enabled
TraversalResult result = service.traverse(
    TraversalConfig.builder()
        .withBFS()
        .withCache(true)
        .build()
);
```

### Fluent Query API
```java
// Using fluent query API
TraversalResult result = service.getQuery().traverse()
    .usingDFS()
    .withoutCache()
    .execute();

// Check connectivity
ConnectivityQuery conn = service.connectivity();
boolean connected = conn.areConnected(0, 5);
int componentCount = conn.getTotalComponents();

// Analyze degrees
GraphQuery.DegreeQuery degree = service.degree();
System.out.println("Max degree: " + degree.getMaxDegree());
System.out.println("Highest degree vertices: " + degree.getHighestDegreeVertices(3));
```

### Event-Driven Traversal
```java
TraversalConfig config = TraversalConfig.builder()
    .withBFS()
    .withEvents(true)
    .addEventListener(event -> {
        if (event.getType() == TraversalEvent.EventType.VERTEX_VISITED) {
            System.out.println("Visited: " + event.getVertex() + 
                             " (component " + event.getComponentId() + ")");
        }
    })
    .addVertexVisitor(v -> System.out.println("Processing: " + v))
    .build();

TraversalResult result = service.traverse(config);
```

### Vertex and Connectivity Queries
```java
// Query vertices
GraphQuery.VertexQuery vq = service.getQuery().vertices();
List<Integer> allVertices = vq.allVertices();
List<Integer> leafVertices = vq.leafVertices();

// Connectivity analysis
ConnectivityQuery conn = service.connectivity();
int componentOf0 = conn.getComponent(0);
List<Integer> sameCom = conn.getVerticesWithDegree(2);
```

### Graph Analysis
```java
GraphAnalyzer analyzer = new GraphAnalyzer(graph);
GraphMetrics metrics = analyzer.analyze();
metrics.print();

// Or via service
System.out.println("Cache size: " + service.getQuery().getCache().getSize());
service.clearCache();
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

### Core Abstractions (5 files)
- `Graph.java` - Graph interface
- `GraphTraversal.java` - Algorithm interface
- `VertexVisitor.java` - Visitor pattern interface
- `TraversalEventListener.java` - Event listener interface
- `GraphException.java` - Custom exception hierarchy

### Graph Implementations (3 files)
- `AbstractGraph.java` - Base graph implementation
- `UndirectedGraph.java` - Undirected graph
- `DirectedGraph.java` - Directed graph

### Algorithm Implementations (2 files)
- `AbstractTraversal.java` - Base traversal with event support
- `BreadthFirstSearch.java` - BFS implementation
- `DepthFirstSearch.java` - DFS implementation

### Service Layer (1 file)
- `GraphService.java` - Unified facade API

### Query System (4 files)
- `GraphQuery.java` - Main query entry point with nested classes
  - `TraversalQuery` - Fluent traversal query builder
  - `VertexQuery` - Vertex filtering and analysis
  - `DegreeQuery` - Degree-based queries
- `ConnectivityQuery.java` - Connectivity analysis
- `GraphAnalyzer.java` - Metrics extraction
- `GraphMetrics.java` - Metrics value object

### Event System (3 files)
- `TraversalEvent.java` - Immutable event objects
- `TraversalEventBus.java` - Publish-subscribe broker
- `TraversalEventListener.java` - Subscriber interface

### Caching & Configuration (2 files)
- `TraversalCache.java` - TTL-based caching
- `TraversalConfig.java` - Configuration builder
- `TraversalResult.java` - Result value object

### Builders (2 files)
- `GraphBuilder.java` - Fluent graph construction
- `TraversalBuilder.java` - Fluent traversal configuration

### Application (1 file)
- `Main.java` - Entry point demonstrating all features
- `ARCHITECTURE.md` - This documentation

## Advanced Features

### Event-Driven Monitoring
```java
TraversalEventBus eventBus = service.getQuery().getEventBus();
eventBus.subscribe(event -> {
    if (event.getType() == TraversalEvent.EventType.VERTEX_VISITED) {
        // Log, metrics, real-time visualization
    }
});
```

### Caching Strategy
- TTL-based automatic expiration (default 5 minutes)
- Key generation from graph identity and strategy type
- Per-traversal cache control
- Cache statistics and monitoring

### Performance Characteristics
- BFS/DFS: O(V + E) traversal time
- Connectivity: O(V + E) preprocessing, O(1) queries
- Degree queries: O(1) for single vertex, O(V log V) for sorted results
- Caching: O(1) for cache hits

## Integration Points

### Metrics & Monitoring
1. **Event listeners** for real-time traversal tracking
2. **Cache statistics** for performance analysis
3. **GraphMetrics** for static analysis
4. **ConnectivityQuery** for structure analysis

### Extensibility Hooks
1. **Custom VertexVisitor** implementations for processing
2. **Custom TraversalEventListener** for monitoring
3. **Custom GraphTraversal** implementations for new algorithms
4. **Custom TraversalConfig** builder extensions

## Thread Safety

Current implementation is **not thread-safe**:
- Graph modifications during traversal may cause issues
- Shared cache requires external synchronization
- Event listeners execute synchronously

For multi-threaded scenarios:
- Use synchronized collections wrapper
- Implement thread-safe event bus
- Consider read-write locks for graph access

## Future Extensions

The architecture supports easy addition of:
- **Weighted graphs**: Enhanced Graph interface with weight() method
- **Directed acyclic graph (DAG) algorithms**: Topological sort, shortest path
- **Advanced traversals**: Dijkstra's, Prim's, Kruskal's, A*
- **Graph algorithms**: Connected components, cycle detection, bipartiteness checking
- **Visitors**: Visualization (dot/ASCII), metrics collection, validation
- **Distributed traversals**: Partition graph across nodes
- **Incremental updates**: Delta computation for graph changes
- **Memory optimization**: Compressed adjacency lists for sparse graphs
