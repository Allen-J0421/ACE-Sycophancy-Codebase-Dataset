# Open Addressing Linear Probing - Architecture Documentation

## System Overview

This is a fully modular, pluggable hash map implementation based on open addressing with linear probing. The architecture uses strategy patterns to allow customization of core behaviors while maintaining a clean, extensible design.

---

## Core Components

### 1. **Data Structure Layer**

#### `HashEntry`
- Encapsulates a single key-value pair with lifecycle tracking
- Maintains `deleted` flag for soft deletion without reallocation
- Methods: `isActive()`, `matches(key)`, `markDeleted()`
- Immutable key, mutable value

#### `HashStats`
- Tracks performance metrics for collision analysis
- Records: probe depth, max probe depth, collision count
- Enables performance profiling without affecting core logic
- Optional compilation flag `trackStats` for zero-overhead when disabled

---

### 2. **Strategy Layer** (Pluggable Algorithms)

#### `ResizeStrategy` Interface
```
shouldExpand(size, deletedCount, capacity): boolean
shouldShrink(size, capacity): boolean
nextCapacity(currentCapacity, expand): int
```
- Controls when and how table resizing occurs
- Decouples resize logic from core operations
- Enables custom load factor strategies

**Implementation:** `DefaultResizeStrategy`
- Upper load factor: 0.75 (expands)
- Lower load factor: 0.25 (shrinks)
- Capacity range: [16, 2^30]

#### `HashFunction` Interface
```
hash(key, capacity): int
```
- Computes initial probe index
- Allows pluggable hashing algorithms

**Implementation:** `ModuloHashFunction`
- Uses modulo operation: `Math.abs(key) % capacity`
- Efficient with power-of-two capacities

#### `ProbeSequence` Interface
```
nextProbe(startIndex, probeCount, capacity): int
```
- Determines sequence of indices to probe
- Supports various collision resolution strategies

**Implementation:** `LinearProbeSequence`
- Linear probing: `(startIndex + probeCount) % capacity`
- Can be extended for quadratic probing, double hashing, etc.

---

### 3. **Core Implementation**

#### `LinearProbingHashMap`
- Main hash map implementation
- Uses dependency injection for strategies
- Dual-mode operation:
  - **Stats tracking** (development): detailed metrics
  - **Normal mode** (production): minimal overhead

**Constructor Variants:**
```java
LinearProbingHashMap()
// Default: 16 capacity, default strategies, stats off

LinearProbingHashMap(int capacity, boolean trackStats)
// Custom capacity with stats toggle

LinearProbingHashMap(int capacity, ResizeStrategy, HashFunction, 
                     ProbeSequence, boolean trackStats)
// Full customization
```

---

### 4. **Utility Layer**

#### `PowerOfTwo`
- Static utilities for power-of-two operations
- `isPowerOfTwo(n)`: O(1) check using bit manipulation
- `nextPowerOfTwo(n, max)`: Rounding with overflow protection

---

## Algorithm Details

### Put Operation
```
1. Check resize threshold (expand if needed)
2. Find slot using probing strategy
3. If new or updating, place entry
4. Track deleted count for load factor
5. Increment size if new entry
```

**Time:** O(1) average, O(n) worst case
**Space:** O(1)

### Get Operation
```
1. Compute initial hash index
2. Probe sequence until:
   - Found matching active entry (return value)
   - Empty slot (not found)
3. Track probe depth for statistics
```

**Time:** O(1) average, O(n) worst case
**Space:** O(1)

### Remove Operation
```
1. Find existing entry via get
2. Mark as deleted (soft deletion)
3. Decrement size, increment deletedCount
4. Check shrink threshold (resize down if needed)
```

**Time:** O(1) average, O(n) worst case
**Space:** O(1)

### Resize Operation
```
1. Allocate new table with new capacity
2. Rehash all active entries
3. Reset size and deletedCount
4. Preserve load factor invariant
```

**Time:** O(n) - must rehash all entries
**Space:** O(n)

---

## Design Patterns

| Pattern | Implementation |
|---------|-----------------|
| **Strategy** | ResizeStrategy, HashFunction, ProbeSequence interfaces |
| **Template Method** | Resize delegates to put() for consistency |
| **Dependency Injection** | Constructor accepts strategies |
| **Factory** | Multiple constructors with different configurations |
| **Observer** | HashStats records events during operations |

---

## Capacity Management

### Power-of-Two Alignment
- All capacities are powers of 2
- Enables efficient modulo: `key % capacity` without division
- Bit manipulation: `(index + i) % capacity` ≡ `(index + i) & (capacity - 1)`

### Load Factor Dynamics
```
Load Factor = size / capacity

Expand when:  (size + deletedCount) >= capacity * 0.75
Shrink when:  size <= capacity * 0.25

Invariants:
- Capacity always ∈ [16, 2^30]
- After resize: load factor → [0.25, 0.75)
- No shrinking below MIN_CAPACITY
```

### Example Timeline
```
Initial:    capacity=16, size=0, LF=0.00
Insert 12:  capacity=16, size=12, LF=0.75 → RESIZE
After:      capacity=32, size=12, LF=0.375
Insert 12:  capacity=32, size=24, LF=0.75 → RESIZE
After:      capacity=64, size=24, LF=0.375
Delete 18:  capacity=64, size=6, LF=0.09 → RESIZE
After:      capacity=32, size=6, LF=0.1875
```

---

## Collision Handling

### Linear Probing
- Simplest probing strategy
- Cache-friendly (sequential access)
- Susceptible to clustering with poor hash function

### Clustering Analysis
- **Primary clustering:** Long runs of occupied slots
- **Secondary clustering:** Dependent on initial position
- Mitigated by:
  1. Good hash function (uniform distribution)
  2. Deleted slot reuse
  3. Periodic resizing to break clusters

### Metrics Tracked
- **Probe Depth:** Number of slots examined per operation
- **Max Probe Depth:** Worst-case probes in one operation
- **Collision Count:** Number of deleted slots encountered

---

## Performance Characteristics

### Time Complexity
| Operation | Best | Average | Worst | Notes |
|-----------|------|---------|-------|-------|
| put() | O(1) | O(1) | O(n) | Includes resize |
| get() | O(1) | O(1) | O(n) | Linear probing |
| remove() | O(1) | O(1) | O(n) | Includes resize |
| containsKey() | O(1) | O(1) | O(n) | Uses get |
| clear() | O(1) | O(1) | O(1) | Allocates new table |
| resize() | — | O(n) | O(n) | Rehashes all entries |

### Space Complexity
- **Data:** O(n) for n active entries
- **Wasted:** O(capacity - size - deletedCount)
- **Bounded by:** Load factor constraints [0.25, 0.75]

### Cache Efficiency
- Linear probing → sequential cache access
- Small entries → better cache utilization
- Resizing → occasional full-table traversal

---

## Extensibility Points

### 1. Custom Resize Strategy
Implement `ResizeStrategy` for alternative load factors:
```java
class AgressiveResizeStrategy implements ResizeStrategy {
    // Expand at 0.5, shrink at 0.125
    // More frequent resizes, less wasted space
}
```

### 2. Custom Hash Function
Implement `HashFunction` for different key types:
```java
class MurmurHashFunction implements HashFunction {
    // Better distribution for weak keys
}
```

### 3. Custom Probe Sequence
Implement `ProbeSequence` for different collision resolution:
```java
class QuadraticProbeSequence implements ProbeSequence {
    // Reduce clustering
}

class DoubleHashFunction implements ProbeSequence {
    // Secondary hash for independence
}
```

---

## Testing Strategy

### Unit Tests
- `testBasicOperations()` - put/get/remove correctness
- `testResizing()` - dynamic capacity management
- `testContainsAndClear()` - membership and reset
- `testCustomStrategies()` - pluggability
- `testStatistics()` - metrics collection

### Validation Checks
1. **Correctness**: All operations return expected values
2. **Invariants**: Size and capacity always in valid range
3. **Consistency**: Deleted entries properly skipped
4. **Performance**: Statistics show collision patterns

---

## Known Limitations

1. **Integer-only keys/values** - No generic support yet
2. **Linear probing only** - No alternative strategies shipped
3. **No concurrency** - Not thread-safe
4. **Single-threaded stats** - Race conditions if enabled with threads
5. **Small initial size** - Minimum capacity of 16 may be excessive for tiny use cases

---

## Future Enhancements

### Phase 1: Generics
- `LinearProbingHashMap<K, V>` for arbitrary types
- Generic hash function interface
- Type-safe API

### Phase 2: Algorithms
- Quadratic probing implementation
- Double hashing implementation
- Pluggable comparison for custom equality

### Phase 3: Concurrency
- ReentrantReadWriteLock for thread-safety
- Concurrent statistics without synchronization
- Atomic capacity changes

### Phase 4: Iteration
- `Iterator<Map.Entry<K, V>>` interface
- Safe concurrent modification detection
- EntrySet, KeySet, ValueCollection views

### Phase 5: Advanced Features
- Custom serialization format
- Snapshot/restore for transactional use
- Performance monitoring and adaptive tuning

---

## Comparison with Standard Libraries

| Feature | LinearProbingHashMap | java.util.HashMap |
|---------|----------------------|-------------------|
| Probing | Linear | Chaining (bucketing) |
| Load Factor | [0.25, 0.75] | [0, 0.75] |
| Cache Friendly | Good | Fair |
| Delete Efficient | Fair (soft) | Excellent (remove chain) |
| Clustering Risk | Medium | None |
| Generics | No | Yes |
| Thread-Safe | No | No (sync version) |
| Null Keys | No | Yes |

---

## References

- Knuth, D. E. (1973). The Art of Computer Programming, Volume 3: Sorting and Searching.
- Weiss, M. A. (2006). Data Structures and Problem Solving Using Java.
- Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). Introduction to Algorithms.
