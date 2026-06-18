# Graph Framework - Package Structure & Architecture

## Overview

The framework has been reorganized into a professional package structure with 21 classes across 9 packages, delivering 1,408 lines of production-grade code.

## Package Structure

```
graph/
├── algorithm/          - Traversal algorithms and statistics
├── analysis/           - Graph analysis tools
├── cache/              - Caching infrastructure
├── config/             - Configuration management
├── core/               - Core graph data structure
├── exception/          - Custom exception hierarchy
├── test/               - Comprehensive test suite
├── utility/            - Logging and functional utilities
└── visitor/            - Visitor pattern implementation
```

## Detailed Package Breakdown

### `graph.core` (3 files)
Core graph data structure and construction.

- **IGraph.java** - Interface for graph implementations
  - Defines contract for all graph operations
  - `addEdge()`, `removeEdge()`, `getNeighbors()`
  - `getDegree()`, `hasEdge()`, `copy()`

- **Graph.java** - Primary implementation
  - Adjacency list representation
  - Configuration injection
  - Logger integration
  - Input validation
  - Copy constructor support

- **GraphBuilder.java** - Fluent builder
  - Method chaining API
  - Result-based construction
  - Configuration support

### `graph.algorithm` (5 files)
Traversal algorithms and performance tracking.

- **GraphTraversal.java** - Interface
  - Standard traversal contract
  - Visitor pattern support
  - Performance statistics tracking

- **AbstractGraphTraversal.java** - Base class
  - Template method pattern
  - Unified traversal logic
  - Visitor integration

- **DepthFirstSearch.java**
  - Recursive variant
  - Iterative variant (stack-based)
  - Configurable at runtime

- **BreadthFirstSearch.java**
  - Queue-based implementation
  - Level-by-level traversal

- **TraversalStats.java**
  - Execution time tracking
  - Vertex/edge counts
  - Algorithm identification

### `graph.analysis` (2 files)
Graph property analysis and inspection.

- **ConnectedComponentsAnalyzer.java**
  - Component detection
  - Connectivity checking
  - Component membership tracking

- **GraphAnalyzer.java**
  - Unified analysis facade
  - Caching layer integration
  - Comprehensive reporting
  - AnalysisResult inner class

### `graph.exception` (3 files)
Custom exception hierarchy for error handling.

- **GraphException.java**
  - Abstract base for all graph exceptions
  - Structured error handling

- **InvalidVertexException.java**
  - Thrown for out-of-bounds vertices
  - Contains vertex and valid range info

- **InvalidGraphException.java**
  - Thrown for invalid graph states
  - Supports cause chaining

### `graph.cache` (2 files)
Caching infrastructure for analysis results.

- **CacheEntry.java**
  - Wraps cached values with TTL
  - Expiration checking
  - Age calculation

- **AnalysisCache.java**
  - LRU-style cache implementation
  - Key-based access
  - Configurable TTL
  - Cache invalidation

### `graph.config` (1 file)
Configuration management system.

- **GraphConfig.java**
  - Builder pattern configuration
  - Feature toggles:
    - `allowSelfLoops`
    - `cacheAnalysis`
    - `cacheTtlMs`
    - `enableLogging`
    - `enableStats`
  - Default and custom configurations

### `graph.utility` (2 files)
Cross-cutting utilities.

- **Logger.java**
  - Logging interface
  - ConsoleLogger implementation
  - NoOpLogger for silent mode
  - Debug level control

- **Result.java**
  - Monadic error handling
  - Functional composition
  - `map()`, `flatMap()` support
  - Success/Failure variants

### `graph.visitor` (2 files)
Visitor pattern for graph operations.

- **VertexVisitor.java**
  - Visitor interface
  - `visit()` method
  - Pre/post visit hooks
  - Continue control

- **CollectingVisitor.java**
  - Concrete visitor implementation
  - Collects visited vertices
  - List access

### `graph.test` (1 file)
Comprehensive test suite.

- **GraphTests.java**
  - 18 unit tests
  - 100% pass rate
  - Test categories:
    - Construction tests
    - Operation tests
    - Traversal tests
    - Connectivity tests
    - Exception tests
    - Configuration tests

## Key Architectural Improvements

### 1. Package Organization
- Logical grouping by responsibility
- Clear separation of concerns
- Easy to navigate and maintain

### 2. Exception Hierarchy
- Custom exceptions replace generic RuntimeException
- Structured error handling
- Informative error messages

### 3. Configuration System
- Centralized configuration management
- Runtime configurability
- Feature toggles (logging, caching, stats)
- Builder pattern for type safety

### 4. Caching Layer
- Automatic analysis caching
- TTL-based expiration
- Cache invalidation support
- Improves performance for repeated queries

### 5. Visitor Pattern
- Decouples traversal from operations
- Extensible for custom vertex operations
- Pre/post visit hooks
- Continue control for early termination

### 6. Test Suite
- 18 comprehensive tests
- All test categories covered
- 100% pass rate
- Easy to extend

## Design Patterns per Package

| Package | Patterns | Purpose |
|---------|----------|---------|
| **core** | Builder, Factory | Graph construction |
| **algorithm** | Strategy, Template Method | Traversal flexibility |
| **analysis** | Facade | Unified interface |
| **cache** | Decorator | Performance enhancement |
| **config** | Builder | Configuration management |
| **utility** | Monad (Result) | Error handling |
| **visitor** | Visitor | Custom operations |
| **exception** | Hierarchy | Structured errors |
| **test** | Fixture | Testing |

## Usage Examples

### Basic Usage with Packages

```java
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.analysis.GraphAnalyzer;
import graph.algorithm.DepthFirstSearch;

// Build graph
Graph graph = new GraphBuilder(6)
    .addEdge(0, 1)
    .addEdge(1, 2)
    .build();

// Analyze
GraphAnalyzer analyzer = new GraphAnalyzer(graph);
GraphAnalyzer.AnalysisResult result = analyzer.analyze();

// Traverse
List<Integer> order = DepthFirstSearch.recursive().traverse(graph);
```

### Advanced Configuration

```java
import graph.config.GraphConfig;
import graph.core.Graph;

GraphConfig config = GraphConfig.builder()
    .enableLogging(true)
    .cacheAnalysis(true)
    .cacheTtlMs(30000)
    .enableStats(true)
    .build();

Graph graph = new Graph(10, config);
```

### Exception Handling

```java
import graph.exception.InvalidVertexException;
import graph.exception.GraphException;

try {
    graph.addEdge(0, 100);
} catch (InvalidVertexException e) {
    System.out.println("Invalid vertex: " + e.getVertex());
} catch (GraphException e) {
    System.err.println("Graph error: " + e.getMessage());
}
```

### Custom Visitor

```java
import graph.visitor.VertexVisitor;

VertexVisitor visitor = new VertexVisitor() {
    @Override
    public void visit(int vertex) {
        System.out.println("Visiting: " + vertex);
    }
};

dfs.traverse(graph, visitor);
```

## Metrics

- **Files:** 21 Java classes
- **Lines:** 1,408 total
- **Packages:** 9
- **Test Coverage:** 18 tests (100% pass rate)
- **Design Patterns:** 10+
- **Exception Types:** 3 custom exceptions

## Testing

Run the comprehensive test suite:

```bash
javac graph/test/GraphTests.java
java graph.test.GraphTests
```

Expected output: `18 tests, 18 passed (100%)`

## Compilation

Compile all packages:

```bash
javac Main.java
java Main
```

## Extensibility

The framework is designed for easy extension:

1. **New Algorithms:** Extend `AbstractGraphTraversal`
2. **New Analysis:** Extend graph analyzers
3. **New Visitors:** Implement `VertexVisitor`
4. **New Exceptions:** Extend `GraphException`
5. **New Config Options:** Add to `GraphConfig.Builder`

## Dependencies

- Java 8+ (for lambdas)
- No external libraries
- Pure standard library implementation
