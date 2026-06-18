# Implementation Guide - Open Addressing Hash Map

## Overview

This document provides a comprehensive guide to the latest iteration of the LinearProbingHashMap implementation, which now includes advanced features, builder pattern support, and comprehensive error handling.

---

## New Features in This Iteration

### 1. **Advanced Probe Sequences**

#### Linear Probing (Original)
```java
int nextProbe(int startIndex, int probeCount, int capacity) {
    return (startIndex + probeCount) % capacity;
}
```
- Sequential slot examination
- Cache-friendly (spatial locality)
- Susceptible to primary and secondary clustering

#### Quadratic Probing (New)
```java
int nextProbe(int startIndex, int probeCount, int capacity) {
    int c1 = 1, c2 = 3;
    long offset = c1 * probeCount + c2 * probeCount * probeCount;
    return (int) ((startIndex + offset) % capacity);
}
```
- Reduced clustering compared to linear probing
- Better distribution of probe sequences
- Slightly less cache-efficient than linear

### 2. **Enhanced Hash Functions**

#### Modulo Hash (Original)
```java
int hash(int key, int capacity) {
    return Math.abs(key) % capacity;
}
```
- Simple and efficient
- Works well with power-of-two capacities
- May have poor distribution for weak keys

#### Quadratic Hash (New)
```java
int hash(int key, int capacity) {
    int h = Math.abs(key) * MULTIPLIER;
    h = h ^ (h >>> 16);
    return h % capacity;
}
```
- Better distribution for weak key patterns
- XOR mixing to break structure
- Similar performance to modulo hash

### 3. **Builder Pattern**

Clean, fluent API for complex configurations:

```java
LinearProbingHashMap map = new HashMapBuilder()
    .capacity(32)
    .hashFunction(new QuadraticHashFunction())
    .probeSequence(new QuadraticProbeSequence())
    .strategy(new DefaultResizeStrategy())
    .trackStats(true)
    .build();
```

**Benefits:**
- Readable configuration
- Default sensible values
- No constructor parameter explosion
- Chainable method calls

### 4. **Enhanced Statistics Tracking**

`HashStats` now tracks:
- **Total Probes**: Sum of all probe counts across operations
- **Max Probe Depth**: Longest probe sequence
- **Collisions**: Number of deleted slots encountered
- **Resize Count**: How many times table was resized

Accessors for programmatic use:
```java
int totalProbes = stats.getTotalProbes();
int maxDepth = stats.getMaxProbeDepth();
int collisions = stats.getCollisions();
int resizes = stats.getResizeCount();
```

### 5. **Improved Error Handling**

**Key Validation:**
```java
if (key == NOT_FOUND) {  // -1 is reserved
    throw new IllegalArgumentException("Key cannot be -1");
}
```

**Capacity Validation:**
```java
if (capacity < strategy.getMinCapacity()) {
    throw new IllegalArgumentException(
        "Capacity " + capacity + " below minimum: " + minCap);
}
```

**Probe Index Bounds Checking:**
```java
if (probeIndex < 0 || probeIndex >= capacity) {
    probeIndex = probeIndex % capacity;
    if (probeIndex < 0) probeIndex += capacity;
}
```

### 6. **Enhanced Display Methods**

**Compact View (Default):**
```java
map.display();
// Only shows non-empty entries
```

**Full View (With Empties):**
```java
map.display(true);
// Shows all entries including EMPTY slots
```

**Statistics Display:**
```java
map.printStats();
// Shows load factor, hash function, probe sequence, and metrics
```

---

## Architecture Refinements

### Strategy Interface Enhancement

The `ResizeStrategy` interface now includes capacity bounds:

```java
interface ResizeStrategy {
    boolean shouldExpand(int size, int deletedCount, int capacity);
    boolean shouldShrink(int size, int capacity);
    int nextCapacity(int currentCapacity, boolean expand);
    int getMinCapacity();    // New
    int getMaxCapacity();    // New
}
```

This allows strategies to define their own capacity constraints.

### Power-of-Two Utilities

Added `previousPowerOfTwo()` for potential shrinking strategies:
```java
static int previousPowerOfTwo(int n) {
    // Returns largest power of 2 that is ≤ n
}
```

### Default Resize Strategy Accessors

Now exposes load factor constants:
```java
float upperLF = strategy.getUpperLoadFactor();  // 0.75
float lowerLF = strategy.getLowerLoadFactor();  // 0.25
```

---

## Usage Examples

### Example 1: Basic Usage
```java
LinearProbingHashMap map = new LinearProbingHashMap();
map.put(1, 100);
map.put(2, 200);
System.out.println(map.get(1));  // 100
map.remove(2);
System.out.println(map.size());  // 1
```

### Example 2: Custom Configuration with Builder
```java
LinearProbingHashMap map = new HashMapBuilder()
    .capacity(64)
    .hashFunction(new QuadraticHashFunction())
    .probeSequence(new QuadraticProbeSequence())
    .trackStats(true)
    .build();

map.put(42, 420);
map.printStats();
```

### Example 3: Low-Collision Benchmark
```java
LinearProbingHashMap linearMap = new HashMapBuilder()
    .probeSequence(new LinearProbeSequence())
    .trackStats(true)
    .build();

LinearProbingHashMap quadraticMap = new HashMapBuilder()
    .probeSequence(new QuadraticProbeSequence())
    .trackStats(true)
    .build();

// Insert same data into both
for (int i = 0; i < 100; i++) {
    linearMap.put(i * 7, i * 100);
    quadraticMap.put(i * 7, i * 100);
}

linearMap.printStats();      // Compare probe statistics
quadraticMap.printStats();
```

### Example 4: Error Handling
```java
try {
    map.put(-1, 100);  // Error: -1 is reserved
} catch (IllegalArgumentException e) {
    System.err.println("Invalid key: " + e.getMessage());
}

try {
    new LinearProbingHashMap(5, false);  // Error: too small
} catch (IllegalArgumentException e) {
    System.err.println("Invalid capacity: " + e.getMessage());
}
```

---

## Performance Comparison: Linear vs. Quadratic Probing

### Clustering Analysis

**Linear Probing:**
- **Primary Clustering**: O(n) contiguous occupied slots
- **Secondary Clustering**: Related keys probe same sequence
- **Average Probe Depth**: ~(1 + 1/(2(1-LF)²)) with good hash
- **Worst Case**: O(n) when heavily clustered

**Quadratic Probing:**
- **Primary Clustering**: Eliminated by quadratic offset
- **Secondary Clustering**: Still present if keys hash to same value
- **Average Probe Depth**: ~(-ln(1-LF)/LF) with good hash
- **Worst Case**: O(n) but less common than linear

### Cache Efficiency

| Strategy | L1 Cache Hits | Memory Bandwidth | Best For |
|----------|---------------|------------------|----------|
| Linear | Excellent | High utilization | Small tables, hot access |
| Quadratic | Good | Medium utilization | Large tables, uniform keys |

---

## Design Patterns Used

### 1. **Strategy Pattern**
- `ResizeStrategy` - Pluggable resizing algorithms
- `HashFunction` - Pluggable hash functions
- `ProbeSequence` - Pluggable probe strategies

### 2. **Builder Pattern**
- `HashMapBuilder` - Fluent configuration API
- Default values provided
- Type-safe composition

### 3. **Template Method Pattern**
- `LinearProbingHashMap.resize()` delegates to `put()`
- Ensures consistency during rehashing

### 4. **Dependency Injection**
- All strategies injected via constructor
- Enables testing and customization

### 5. **Observer Pattern**
- `HashStats` records events
- Optional compilation with `trackStats` flag

---

## Validation and Constraints

### Key Constraints
- **Key Value**: Cannot be -1 (reserved for NOT_FOUND sentinel)
- **Key Uniqueness**: No duplicate keys (overwrites previous value)
- **Range**: Integer keys [Integer.MIN_VALUE to Integer.MAX_VALUE], excluding -1

### Capacity Constraints
- **Minimum**: 16 (prevents tiny tables)
- **Maximum**: 2³⁰ (prevents overflow)
- **Alignment**: Always power of two
- **Normalization**: Automatically rounds up to next power of two

### Load Factor Ranges
- **Expansion**: Triggered at ≥75% load factor
- **Shrinking**: Triggered at ≤25% load factor
- **Active Range**: [25%, 75%] after operations

---

## Thread Safety

**Current Status**: NOT thread-safe

The implementation is single-threaded by design. For multi-threaded use:

```java
// Wrap with synchronization (simple but potentially slow)
LinearProbingHashMap map = new LinearProbingHashMap();
synchronized(map) {
    map.put(key, value);
}

// Or use external locking
ReadWriteLock lock = new ReentrantReadWriteLock();
lock.writeLock().lock();
try {
    map.put(key, value);
} finally {
    lock.writeLock().unlock();
}
```

Future versions may include concurrent variants with fine-grained locking.

---

## Memory Usage Analysis

### Space Complexity

**Fixed Overhead:**
- `HashEntry[]` array: `8 * capacity` bytes (array reference)
- Instance fields: ~40 bytes
- Strategy references: ~32 bytes

**Entry Storage:**
- Per entry: 32 bytes (key: 4, value: 4, deleted: 1, padding: 23)
- Total: `32 * size` bytes for active entries

**Wasted Space:**
- Deleted entries: `32 * deletedCount` bytes
- Empty slots: capacity-dependent (managed by load factors)

### Example: 1000 entries
```
Array:        8 KB
Active:      32 KB (1000 entries)
Deleted:      0-4 KB (up to 250 deleted)
Empty:       16+ KB (load factor management)
Overhead:     1 KB (constants, refs)
───────────────────
Total:       ~57 KB minimum
```

---

## Extensibility Roadmap

### Phase 2: Generics Support
```java
public class LinearProbingHashMap<K, V> {
    // Type-safe implementation
}
```

### Phase 3: Iteration Support
```java
public Iterator<Map.Entry<Integer, Integer>> iterator() {
    // Safe iteration over active entries
}
```

### Phase 4: Concurrent Variants
```java
public class ConcurrentLinearProbingHashMap {
    // Read-write lock aware
    // Atomic statistics
}
```

### Phase 5: Advanced Metrics
```java
public class HashMapMetrics {
    double getClusteringCoefficient();
    int[] getProbeDepthDistribution();
    double getPerfectHashingScore();
}
```

---

## Testing Checklist

- [x] Basic put/get/remove operations
- [x] Dynamic resizing (expand/shrink)
- [x] Key containment checking
- [x] Clear operation
- [x] Custom strategies
- [x] Statistics collection
- [x] Builder pattern
- [x] Multiple probe sequences
- [x] Error handling (invalid keys/capacity)
- [x] Load factor verification
- [x] Capacity normalization

---

## Known Limitations

1. **Integer-only**: No generic support yet
2. **Single-threaded**: No synchronization
3. **-1 reserved**: Cannot use -1 as key
4. **No iteration**: No Iterator/Enumeration support
5. **Basic stats**: No per-operation metrics
6. **Deterministic resizing**: No adaptive strategies
7. **Power-of-two only**: Capacity always 2^n

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Initial | Basic linear probing |
| 2.0 | Iteration 2 | Bi-directional resizing |
| 3.0 | Iteration 3 | Modular architecture |
| 4.0 | Iteration 4 | **Advanced probing, builder, enhanced stats** |

---

## References

- **Linear Probing**: Knuth, D. E. (1973). TAOCP Vol. 3.
- **Quadratic Probing**: Addressing clustering issues, Weiss (2006).
- **Hash Functions**: Murmurhash by Austin Appleby; Hash-based benchmarks.
- **Builder Pattern**: Effective Java by Joshua Bloch.
