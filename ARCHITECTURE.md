# Architecture & Design Decisions

## Design Evolution

### Initial State (Single File)
```
coin_change.java
├── Static method
├── Inline validation
└── No error handling
```

**Issues**: Monolithic, inflexible, no reusability

### First Refactor
```
CoinChange.java
├── InputValidator
├── DynamicProgrammingStrategy
└── CoinChangeResult
```

**Improvement**: Separated concerns, basic patterns

### Advanced Refactor (Current)
```
Core Strategy Layer
├── CoinChangeStrategy (interface)
├── DynamicProgrammingStrategy
└── SpaceOptimizedStrategy

Orchestration Layer
├── CoinChangeSolver
└── CoinChangeSolverBuilder (fluent API)

Supporting Infrastructure
├── InputValidator (typed exceptions)
├── CachedStrategy (decorator)
├── StrategyFactory
└── StrategyType (enum)

Data Transfer Objects
├── CoinChangeResult (immutable)
└── SolveMetrics (metrics)

Exception Hierarchy
├── CoinChangeException (base)
├── InvalidCoinsException
└── InvalidTargetSumException
```

## Key Design Decisions

### 1. Strategy Pattern for Algorithms
**Decision**: Use interface-based strategy pattern instead of if/else branching

**Rationale**:
- Open/Closed Principle: Open for extension (new algorithms), closed for modification
- Single Responsibility: Each strategy class has one job
- Runtime flexibility: Switch algorithms without recompiling
- Testability: Mock strategies easily in tests

**Alternative Rejected**: 
- If/else in single method (rigid, violates OCP)
- Inheritance hierarchy (fragile, can't combine features)

### 2. Decorator for Caching
**Decision**: Wrap strategies with CachedStrategy decorator instead of adding caching to each implementation

**Rationale**:
- DRY: Implement caching once, use with any strategy
- Composition over inheritance
- Can combine with other decorators in future
- Strategy remains pure, cache is orthogonal

**Alternative Rejected**:
- Add caching to base class (forces all strategies to carry caching overhead)
- Create CachedDPStrategy, CachedOptimizedStrategy (duplicates caching logic)

### 3. Builder Pattern for Configuration
**Decision**: Use fluent builder instead of constructor overloading or telescoping

**Rationale**:
- Readable, self-documenting API
- Easy to add optional features without modifying signatures
- No null checks or boolean flags
- Clear intent: `builder().withCaching().enableMetrics().build()`

**Alternative Rejected**:
- Constructor overloading (confusing, method explosion)
- Static factory methods with many parameters (not fluent, hard to maintain)
- JavaBeans (mutable, thread-unsafe)

### 4. Immutable Value Objects
**Decision**: CoinChangeResult and SolveMetrics are immutable (clone arrays, final fields)

**Rationale**:
- Thread-safe: Can share across threads without synchronization
- Predictable behavior: No accidental modifications
- Good for caching: Can use as HashMap values safely
- Clear contracts: Consumer knows data won't change

**Implementation Details**:
```java
public int[] getCoins() {
    return coins.clone();  // Defensive copy on retrieval
}
```

### 5. Typed Exception Hierarchy
**Decision**: Create domain-specific exception types instead of generic exceptions

**Rationale**:
- Precise error handling: Caller can act on specific error types
- Clear intent: Exception type documents what went wrong
- Better logging: Can log different exception types differently
- Extends framework naturally: New error types just extend base

### 6. Factory for Strategy Instantiation
**Decision**: StrategyFactory centralizes strategy creation logic

**Rationale**:
- Single point of change: Modify initialization in one place
- Reflection handling: Factory handles complex instantiation
- Future proofing: Can add strategy registration, caching, validation

### 7. Metrics as Optional Feature
**Decision**: Metrics disabled by default, enabled via builder flag

**Rationale**:
- Zero overhead when not needed: No metrics collection performance cost
- Clear separation: Metrics logic in CoinChangeSolver, not in strategies
- Explicit opt-in: Developer chooses to pay the cost
- Debug-friendly: Can enable for profiling, disable for production

### 8. Validation Layer
**Decision**: Centralize all validation in InputValidator class

**Rationale**:
- Single place to update validation rules
- Reusable across solver instances
- Consistent error handling
- Testable in isolation

## Architectural Principles

### SOLID Principles Applied

**S - Single Responsibility**
- `CoinChangeStrategy` - Defines algorithm interface
- `InputValidator` - Validates input
- `CoinChangeSolver` - Orchestrates solving
- `CoinChangeResult` - Represents result

Each class has one reason to change.

**O - Open/Closed**
- Open for extension: Implement `CoinChangeStrategy` for new algorithms
- Closed for modification: Existing code unchanged when adding features
- Strategy pattern enables this

**L - Liskov Substitution**
- All `CoinChangeStrategy` implementations are interchangeable
- Can substitute one for another without breaking code
- Same interface contract

**I - Interface Segregation**
- `CoinChangeStrategy` has minimal interface (one method)
- Solvers don't depend on unused methods
- Focused contracts

**D - Dependency Inversion**
- `CoinChangeSolver` depends on `CoinChangeStrategy` (abstraction)
- Not on concrete implementations
- Injected via constructor
- Enables testing with mock strategies

### Design Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| Strategy | Algorithm implementations | Swap algorithms |
| Factory | StrategyFactory | Create strategies |
| Builder | CoinChangeSolverBuilder | Configure solver |
| Decorator | CachedStrategy | Add features |
| Facade | CoinChange static methods | Simplify API |
| Value Object | CoinChangeResult, SolveMetrics | Immutable data |
| Template Method | Could add in base class | Define algorithm steps |

### Architectural Styles

**Layered Architecture**
```
Presentation Layer (CoinChange facade)
    ↓
Configuration Layer (CoinChangeSolverBuilder)
    ↓
Orchestration Layer (CoinChangeSolver)
    ↓
Business Logic Layer (Strategies)
    ↓
Data Layer (Results, Metrics)
```

## Trade-offs Made

### Complexity vs. Flexibility
**Chosen**: More classes for better flexibility
**Reasoning**: Worth it for production code; can be simplified for small projects

### Immutability vs. Performance
**Chosen**: Immutable with defensive copies
**Reasoning**: Safety and correctness more important than tiny performance gain

### Metrics Overhead vs. Debuggability
**Chosen**: Optional metrics (no overhead when disabled)
**Reasoning**: Gives both options to developer

### Abstraction Levels
**Chosen**: Multiple levels (Facade → Builder → Solver → Strategy)
**Reasoning**: Different use cases need different levels of control

## Future Extension Points

1. **New Algorithms**
   - Implement `CoinChangeStrategy`
   - Add to `StrategyType` enum
   - Done! Works with existing infrastructure

2. **Result Transformation**
   - Extend `CoinChangeResult` or create wrapper
   - Add methods to extract actual coin combinations (not just count)

3. **Distributed Computing**
   - Implement strategy that uses MapReduce/parallel processing
   - Interface enables this transparently

4. **Caching Policies**
   - Create `LRUCachedStrategy`, `TTLCachedStrategy` variants
   - All implement same interface

5. **Event Listeners**
   - Add observer pattern for solve events
   - Fire events before/after solving
   - Enable logging, monitoring, debugging

6. **Serialization**
   - Add JSON/Protobuf support to result objects
   - Network transmission or persistence

## Testing Strategy

### Unit Tests
- Validate individual components in isolation
- Mock dependencies where appropriate
- Test edge cases and error conditions

### Integration Tests
- Test components working together
- Builder creating proper solver instances
- Caching decorator functioning with strategies

### Property-Based Tests (could add)
- Verify both strategies produce same results
- Verify commutativity of caching
- Verify metric collection doesn't affect results

## Performance Considerations

### Time Complexity: O(n × m)
Unavoidable: Must compute all states in DP table

### Space Optimization
- Standard: O(n × m) - keep full table
- Optimized: O(m) - one row at a time
- Trade-off: Simplicity vs. memory

### Caching ROI
- Single call: No benefit (overhead)
- Repeated calls: Massive speedup (91x in benchmarks)
- Break-even: ~2-3 queries to same input

## Security Considerations

- No external I/O: Safe from injection attacks
- No reflection except in factory: Contained and documented
- Immutable results: Can't be poisoned
- Input validation: Rejects malformed data
- No logging of sensitive data: Cache keys contain problem data (acceptable)

## Maintenance

### Code Organization
- Each class in separate file (easy to find and edit)
- Clear naming conventions
- Consistent formatting and style

### Documentation
- DESIGN.md for architecture
- README.md for usage
- This file for decisions and trade-offs
- Code comments only for non-obvious intent

### Testing
- 22 test cases providing regression detection
- Tests serve as documentation examples
- Easy to add new tests

## Lessons Learned

1. **Patterns aren't free**: More classes = more to maintain
2. **Interfaces are powerful**: CoinChangeStrategy enables everything
3. **Immutability is valuable**: No defensive programming needed
4. **Optional features shine**: Metrics overhead only when enabled
5. **Builder pattern pays off**: Configuration is clear and extensible
