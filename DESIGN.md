# Coin Change Solver - Design Documentation

## Architecture Overview

The refactored codebase implements a modular, extensible design using established software patterns:

### Core Components

**1. CoinChangeStrategy (Interface)**
- Defines the contract for different solving algorithms
- Enables Strategy pattern for algorithm swapping
- Allows new algorithms to be added without modifying existing code

**2. Algorithm Implementations**
- `DynamicProgrammingStrategy`: Classic 2D DP table approach (O(n×m) time, O(n×m) space)
- `SpaceOptimizedStrategy`: Memory-efficient 1D DP approach (O(n×m) time, O(m) space)
- Easy to add more strategies (memoization, recursion, etc.) by implementing the interface

**3. InputValidator**
- Centralized validation logic
- Validates coin array (non-empty, positive values)
- Validates target sum (non-negative)
- Single responsibility principle

**4. CoinChangeResult**
- Immutable data class for encapsulating results
- Provides clean API for consumers
- Includes metadata (coins array, target sum)
- Better than returning raw integers

**5. CoinChangeSolver**
- Orchestrates the solving process
- Accepts strategy via constructor injection (Dependency Injection)
- Handles edge cases (targetSum == 0)
- Bridges validation and algorithm execution

**6. CoinChange (Entry Point)**
- Public API for library users
- Uses default strategy (DynamicProgrammingStrategy)
- Simplifies common use case

### Design Patterns Applied

| Pattern | Class | Benefit |
|---------|-------|---------|
| Strategy | CoinChangeStrategy + implementations | Easy to swap algorithms |
| Dependency Injection | CoinChangeSolver constructor | Flexible, testable |
| Immutable Value Object | CoinChangeResult | Thread-safe, clear semantics |
| Single Responsibility | Each class has one reason to change | Maintainable, focused |
| Facade | CoinChange static solve() | Simple API for common cases |

### Usage Examples

**Simple API:**
```java
CoinChangeResult result = CoinChange.solve(new int[]{1, 2, 3}, 5);
System.out.println(result.getWays());  // Output: 5
```

**Custom Strategy:**
```java
CoinChangeSolver solver = new CoinChangeSolver(new SpaceOptimizedStrategy());
CoinChangeResult result = solver.solve(new int[]{1, 2, 3}, 5);
```

**Demo with Multiple Strategies:**
```bash
java CoinChangeDemo
```

### Extensibility Points

1. **Add new algorithm**: Create new class implementing `CoinChangeStrategy`
2. **Modify validation**: Update `InputValidator` class
3. **Change result format**: Extend `CoinChangeResult` class
4. **Add benchmarking**: Create `BenchmarkRunner` comparing strategies

### Time & Space Complexity

- **DynamicProgrammingStrategy**: O(n×m) time, O(n×m) space
  - n = number of coins
  - m = target sum
  
- **SpaceOptimizedStrategy**: O(n×m) time, O(m) space
  - Same time complexity with reduced memory footprint

Both produce identical results with different resource tradeoffs.
