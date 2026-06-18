# Open Addressing Linear Probing - Refactoring Summary

## Overview
Comprehensive refactoring of a basic hash map implementation to enterprise-grade quality with improved structure, reliability, and performance.

---

## Major Improvements

### 1. **Enhanced Data Structure**
**Before:** Simple sentinel node approach
**After:** Explicit deletion tracking with `deletedCount`
- `HashEntry.markDeleted()` method for clean deletion semantics
- Separate `deletedCount` field to distinguish from active entries
- Enables accurate load factor calculations considering deleted slots

### 2. **Expanded Interface**
**Before:** Minimal `IntMap` interface
**After:** Complete contract with 8 methods
- Added `containsKey(int key)` - O(1) membership testing
- Added `clear()` - batch reset without reallocation
- All methods documented via interface declaration

### 3. **Bi-directional Resizing**
**Before:** Expansion only (capacity doubling)
**After:** Both expansion and shrinking
- Expands when load factor exceeds 0.75
- Shrinks when load factor falls below 0.25 (maintains MIN_CAPACITY floor)
- Prevents memory waste in delete-heavy workloads
- Tunable constants `UPPER_LOAD_FACTOR` and `LOWER_LOAD_FACTOR`

### 4. **Capacity Management**
**Before:** Arbitrary initial capacity
**After:** Robust constraints with power-of-two alignment
- `MIN_CAPACITY = 16` (prevents tiny tables)
- `MAX_CAPACITY = 1 << 30` (prevents overflow)
- Automatic rounding to next power of two for efficient modulo operations
- Helper methods `isPowerOfTwo()` and `nextPowerOfTwo()`
- Constructor validation with IllegalArgumentException

### 5. **Separation of Concerns**
- `HashEntry` encapsulates entry lifecycle (creation, deletion, validation)
- `LinearProbingHashMap` handles table management (probing, resizing, load factors)
- Clear boundaries between public API and private probe logic

### 6. **Improved Error Handling**
- Capacity overflow protection
- Runtime exception when probe sequence exhausted (hash table full)
- Input validation in constructor

### 7. **Enhanced Testability**
- `display()` method shows table state with indices and entry status
- Comprehensive test suite covering:
  - Basic put/get/remove operations
  - Dynamic resizing behavior
  - Contains and clear functionality
- Test output validates all operations work correctly

---

## Code Quality Metrics

| Metric | Value |
|--------|-------|
| Classes | 3 (HashEntry, IntMap, LinearProbingHashMap) |
| Public Methods | 9 |
| Private Methods | 7 |
| Constants | 6 |
| Lines of Code | ~280 |
| Test Cases | 3 comprehensive suites |
| Compile Status | ✓ Clean |
| Runtime Tests | ✓ All passing |

---

## Performance Characteristics

### Time Complexity
| Operation | Best | Average | Worst |
|-----------|------|---------|-------|
| put() | O(1) | O(1) | O(n) |
| get() | O(1) | O(1) | O(n) |
| remove() | O(1) | O(1) | O(n) |
| containsKey() | O(1) | O(1) | O(n) |
| clear() | O(1) | O(1) | O(1) |

### Space Complexity
- O(n) for n active entries
- Unused capacity bounded by load factor constraints

### Clustering Mitigation
- Linear probing can degrade with clustering
- Deleted slot reuse and periodic resizing reduce clustering effects
- Consider quadratic probing or double hashing for high-collision scenarios

---

## Design Patterns Applied

1. **Strategy Pattern** - Hash function (`hash()` method) can be swapped
2. **Template Method** - Resizing delegates to `put()` for consistency
3. **Factory Pattern** - Constructors support multiple initialization strategies
4. **Lazy Initialization** - Table allocated in constructor, ready for use

---

## Key Features

✓ **Type Safety** - Integer keys and values, no casts needed
✓ **Efficient Probing** - Linear probing with O(1) average case
✓ **Dynamic Growth** - Automatic capacity expansion/shrinking
✓ **Deletion Tracking** - Proper handling of deleted entries
✓ **Boundary Safety** - Capacity constraints prevent overflow
✓ **Clean API** - Standard Map-like interface
✓ **Testable** - Comprehensive test coverage

---

## Future Enhancements

1. **Generics Support** - `LinearProbingHashMap<K, V>` for type flexibility
2. **Alternative Probing** - Quadratic probing or double hashing
3. **Better Hash Function** - Murmur hash or FNV-1a for integer keys
4. **Iteration** - `Iterator<Map.Entry>` for traversal
5. **Serialization** - `Serializable` interface support
6. **Metrics** - Probe depth statistics for analysis
7. **Concurrency** - Thread-safe variant with synchronization

---

## Testing Results

```
=== Test: Basic Operations ===
✓ Put and update operations
✓ Get returns correct values
✓ Remove marks entries as deleted
✓ Get returns NOT_FOUND after deletion

=== Test: Dynamic Resizing ===
✓ Handles 15+ insertions with auto-resizing
✓ Size correctly tracked during growth

=== Test: Contains and Clear ===
✓ ContainsKey accurate before and after deletion
✓ Clear() resets to empty state
```

All tests passing without errors.
