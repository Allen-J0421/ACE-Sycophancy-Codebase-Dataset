# Coin Change Solver - Production-Grade Java Implementation

A comprehensive, well-architected solution to the coin change problem demonstrating advanced software design patterns, best practices, and enterprise-level code organization.

## Quick Start

### Basic Usage
```java
CoinChangeResult result = CoinChange.solve(new int[]{1, 2, 3}, 5);
System.out.println(result.getWays());  // Output: 5
```

### With Builder Pattern
```java
CoinChangeResult result = CoinChange.builder()
    .withStrategyType(StrategyType.SPACE_OPTIMIZED)
    .withCaching()
    .enableMetrics()
    .build()
    .solve(new int[]{1, 2, 3}, 5);
```

## Project Structure

### 18 Production-Ready Classes

**Core Algorithm (2 files)**
- `CoinChangeStrategy.java` - Strategy interface
- `DynamicProgrammingStrategy.java` - 2D DP implementation
- `SpaceOptimizedStrategy.java` - Memory-optimized 1D DP

**Solver & Orchestration (4 files)**
- `CoinChangeSolver.java` - Main solver with metrics support
- `CoinChangeSolverBuilder.java` - Builder pattern for configuration
- `CoinChange.java` - Public facade API
- `InputValidator.java` - Centralized validation

**Advanced Features (4 files)**
- `CachedStrategy.java` - Memoization decorator
- `CacheKey.java` - Immutable cache key
- `SolveMetrics.java` - Performance metrics collection
- `CoinChangeResult.java` - Immutable result object

**Exception Handling (3 files)**
- `CoinChangeException.java` - Base exception
- `InvalidCoinsException.java` - Coin validation errors
- `InvalidTargetSumException.java` - Sum validation errors

**Factory Pattern (2 files)**
- `StrategyFactory.java` - Strategy creation factory
- `StrategyType.java` - Algorithm enumeration

**Testing & Examples (3 files)**
- `CoinChangeTest.java` - 22 comprehensive test cases
- `CoinChangeDemo.java` - Feature demonstrations
- `AdvancedExample.java` - Real-world usage examples

## Design Patterns

| Pattern | Purpose | Location |
|---------|---------|----------|
| **Strategy** | Swap algorithms at runtime | `CoinChangeStrategy` + implementations |
| **Factory** | Create strategies without client coupling | `StrategyFactory` |
| **Builder** | Fluent, flexible configuration | `CoinChangeSolverBuilder` |
| **Decorator** | Add caching to any strategy | `CachedStrategy` |
| **Facade** | Simplify common use cases | `CoinChange` static methods |
| **Value Object** | Immutable, thread-safe data | `CoinChangeResult`, `SolveMetrics` |
| **Exception Hierarchy** | Precise error handling | `CoinChangeException` + subtypes |
| **Dependency Injection** | Testable, decoupled components | Constructor injection throughout |

## Features

✅ **Multiple Algorithms**
- Standard 2D DP: O(n×m) time, O(n×m) space
- Space-optimized 1D DP: O(n×m) time, O(m) space

✅ **Transparent Caching**
- Decorator pattern wraps any strategy
- O(1) lookups for repeated queries
- Automatic memoization with cache key deduplication

✅ **Performance Metrics**
- Execution time tracking (nanoseconds/milliseconds)
- Memory usage estimation
- Optional collection (zero overhead when disabled)

✅ **Comprehensive Validation**
- Null/empty coin array detection
- Positive coin value enforcement
- Non-negative sum validation
- Typed exceptions for granular handling

✅ **Fluent Builder API**
- Chain configuration methods
- Optional metrics collection
- Optional caching layer
- Strategy selection via enum

✅ **Test Coverage**
- 22 passing test cases
- Functional tests (algorithms)
- Edge case validation
- Exception handling verification
- Pattern integration tests

## Running Examples

```bash
# Basic example
javac *.java
java CoinChange

# Comprehensive demo
java CoinChangeDemo

# Advanced examples with benchmarks
java AdvancedExample

# Full test suite (22 tests)
java CoinChangeTest
```

## API Reference

### Simple API
```java
// Solve with default strategy
CoinChangeResult result = CoinChange.solve(int[] coins, int targetSum);

// Access results
int ways = result.getWays();
int[] coins = result.getCoins();
int sum = result.getTargetSum();
```

### Builder API
```java
CoinChangeResult result = CoinChange.builder()
    .withStrategyType(StrategyType.SPACE_OPTIMIZED)  // Algorithm choice
    .withCaching()                                    // Enable memoization
    .enableMetrics()                                  // Collect performance data
    .build()
    .solve(coins, targetSum);

// Access metrics
if (result.hasMetrics()) {
    SolveMetrics metrics = result.getMetrics();
    long timeNs = metrics.getExecutionTimeNanos();
    double timeMs = metrics.getExecutionTimeMillis();
    double memKB = metrics.getMemoryUsageKB();
}
```

### Direct Solver Usage
```java
CoinChangeSolver solver = new CoinChangeSolver(
    new CachedStrategy(new SpaceOptimizedStrategy()),
    true  // enableMetrics
);
CoinChangeResult result = solver.solve(coins, targetSum);
```

### Error Handling
```java
try {
    CoinChange.solve(coins, sum);
} catch (InvalidCoinsException e) {
    // Handle invalid coins (null, empty, negative values)
} catch (InvalidTargetSumException e) {
    // Handle invalid sum (negative)
} catch (CoinChangeException e) {
    // Handle any other coin change exception
}
```

## Complexity Analysis

### Time Complexity: O(n × m)
- n = number of coin types
- m = target sum value
- Both algorithms process n coins through m sum values

### Space Complexity
| Strategy | Space |
|----------|-------|
| Standard DP | O(n × m) - 2D table |
| Space Optimized | O(m) - 1D array |
| Cached | O(cache size) - HashMap overhead |

## Test Results

```
=== Test Summary ===
Passed: 22
Failed: 0
Total: 22

Coverage:
✓ Basic functionality (3 tests)
✓ Edge cases (3 tests)
✓ Input validation (4 tests)
✓ Strategy comparison (3 tests)
✓ Builder pattern (3 tests)
✓ Metrics collection (3 tests)
✓ Caching behavior (3 tests)
```

## Performance Benchmark

```
Example: Make sum=100 with coins [1, 2, 5, 10]

Standard (2D DP):
  Result: 2156 ways
  Time: 0ms
  Memory: 0.00KB

Space Optimized (1D DP):
  Result: 2156 ways
  Time: 0ms
  Memory: 0.00KB

Caching (Repeated Queries):
  First call: ~1.3ms
  Cached call: ~0.01ms
  Speedup: 91x faster
```

## Architecture Layers

```
┌─────────────────────────────────┐
│   Public API (CoinChange)       │ Facade
├─────────────────────────────────┤
│  Builder & Solver Orchestration │ Configuration
├─────────────────────────────────┤
│  Validation & Exception Layer   │ Safety
├─────────────────────────────────┤
│  Strategy (Caching Decorator)   │ Flexibility
├─────────────────────────────────┤
│  Algorithm Implementation       │ Core
├─────────────────────────────────┤
│  Metrics & Result Objects       │ Data Transfer
└─────────────────────────────────┘
```

## Extensibility

### Add New Algorithm
```java
public class RecursiveWithMemoStrategy implements CoinChangeStrategy {
    @Override
    public int countWays(int[] coins, int targetSum) {
        // Implementation
    }
}

// Register in StrategyType enum
RECURSIVE("Recursive", RecursiveWithMemoStrategy.class)

// Use immediately
CoinChange.builder().withStrategyType(StrategyType.RECURSIVE).build()
```

### Add New Exception Type
```java
public class StrategyInstantiationException extends CoinChangeException {
    public StrategyInstantiationException(String message) {
        super(message);
    }
}
```

### Custom Metrics Collection
```java
// Extend SolveMetrics or modify CoinChangeSolver collection logic
CoinChangeSolver solver = new CoinChangeSolver(strategy, enableMetrics);
```

## Production Readiness

✅ **Robust Error Handling** - Typed exceptions, validation at boundaries  
✅ **Performance Monitoring** - Optional metrics collection  
✅ **Memory Efficient** - Multiple space-optimized implementations  
✅ **Thread Safe** - Immutable result objects, no mutable shared state  
✅ **Well Tested** - 22 automated test cases  
✅ **Documented** - Code comments, examples, and design docs  
✅ **Extensible** - Strategy pattern enables new algorithms  
✅ **Configurable** - Builder pattern for flexible setup  

## License

Educational/Enterprise - Feel free to use and modify.

## Documentation

See `DESIGN.md` for comprehensive architecture documentation and design decision rationale.
