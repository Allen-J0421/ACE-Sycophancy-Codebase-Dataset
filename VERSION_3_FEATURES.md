# Version 3.0 - Iterator & Functional Operations

## Major Features Added

### 1. **Iterator Support**

**Key Iterator:**
```java
Iterator<K> keyIterator = map.keyIterator();
while (keyIterator.hasNext()) {
    K key = keyIterator.next();
    keyIterator.remove();  // Optional removal
}
```

**Entry Iterator:**
```java
Iterator<MapEntry<K, V>> entryIter = map.entryIterator();
while (entryIter.hasNext()) {
    MapEntry<K, V> entry = entryIter.next();
    K key = entry.getKey();
    V value = entry.getValue();
}
```

### 2. **Collection Views**

**EntrySet:**
```java
Set<MapEntry<K, V>> entries = map.entrySet();
for (MapEntry<K, V> entry : entries) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

**KeySet:**
```java
Set<K> keys = map.keySet();
for (K key : keys) {
    System.out.println(key);
}
```

**Values:**
```java
Collection<V> values = map.values();
for (V value : values) {
    System.out.println(value);
}
```

### 3. **Functional Operations**

**getOrDefault:**
```java
V value = map.getOrDefault("key", defaultValue);
```

**putIfAbsent:**
```java
V oldValue = map.putIfAbsent("key", newValue);
```

**computeIfAbsent:**
```java
V value = map.computeIfAbsent("key", k -> computeValue(k));
```

**computeIfPresent:**
```java
V newValue = map.computeIfPresent("key", (k, v) -> transform(v));
```

**forEach:**
```java
map.forEach((key, value) -> System.out.println(key + ": " + value));
```

## Implementation Details

### Iterator Classes

#### HashMapIterator<K, V>
- Implements `Iterator<K>` for key iteration
- Supports `remove()` operation
- Safe concurrent deletion via `markDeleted()`
- O(1) per iteration, O(n) total

#### HashMapEntryIterator<K, V>
- Implements `Iterator<MapEntry<K, V>>`
- Returns immutable `SimpleEntry` objects
- Supports `remove()` operation
- O(1) per iteration, O(n) total

#### SimpleEntry<K, V>
- Implements `MapEntry<K, V>` interface
- Immutable key, mutable value
- Proper `equals()` and `hashCode()` implementations
- Thread-safe value reading

### Functional Operations Semantics

**getOrDefault:**
- Time: O(1) average
- Returns null if key not found, then default value
- Non-modifying operation

**putIfAbsent:**
- Time: O(1) average
- Returns old value if key exists, null otherwise
- No modification if key present

**computeIfAbsent:**
- Time: O(1) average
- Mapping function called only if key absent
- Returns computed value or null
- Function result stored if non-null

**computeIfPresent:**
- Time: O(1) average
- Remapping function called only if key present
- If function returns null, entry removed
- Returns new value or null

**forEach:**
- Time: O(n) for all entries
- Non-modifying iteration
- Functional interface for lambda support

## Code Organization

### New Structure
```
open_addressing_linear_probing.java
├── CORE DATA STRUCTURES
│   ├── HashEntry<K,V>
│   └── HashStats
├── INTERFACES & STRATEGIES
│   ├── MapEntry<K,V>
│   ├── HashMap<K,V> (main interface)
│   ├── ResizeStrategy
│   ├── HashFunction<K>
│   └── ProbeSequence
├── ITERATORS & VIEWS
│   ├── HashMapIterator<K,V>
│   ├── HashMapEntryIterator<K,V>
│   └── SimpleEntry<K,V>
├── MAIN IMPLEMENTATION
│   └── LinearProbingHashMap<K,V>
├── UTILITIES
│   ├── PowerOfTwo
│   └── HashMapBuilder<K,V>
└── TESTING
    └── Demo
```

### Section Comments
- Clear visual separators (80-char lines)
- Logical grouping of related code
- Easy to navigate and extend

## Testing Coverage

### testIterators()
- Key iterator with hasNext/next
- Entry iterator with MapEntry access
- Iteration count verification

### testEntrySet()
- EntrySet creation and size
- Entry property access (getKey/getValue)
- Unmodifiable guarantees

### testFunctionalOperations()
- getOrDefault with missing keys
- putIfAbsent with existing/new keys
- computeIfAbsent with key creation
- computeIfPresent with remapping
- Value transformation verification

### testForEach()
- Lambda-based iteration
- BiConsumer functional interface
- All entries visited

## Performance Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| keyIterator() | O(n) | O(1) | Creates iterator |
| Iterator.next() | O(1) | O(1) | Per element |
| entryIterator() | O(n) | O(1) | Creates iterator |
| entrySet() | O(n) | O(n) | Creates view copy |
| getOrDefault() | O(1) avg | O(1) | No modification |
| putIfAbsent() | O(1) avg | O(1) | Conditional put |
| computeIfAbsent() | O(1) avg | O(1) | Function applied |
| computeIfPresent() | O(1) avg | O(1) | Function applied |
| forEach() | O(n) | O(1) | Iteration only |

## Backward Compatibility

✓ **Fully compatible** with v2.0
- All v2 methods preserved
- New methods added
- No signature changes
- No semantic changes to existing methods

## Example Usage Patterns

### Pattern 1: Filtering with Iterator
```java
Iterator<K> iter = map.keyIterator();
while (iter.hasNext()) {
    K key = iter.next();
    if (shouldRemove(key)) {
        iter.remove();
    }
}
```

### Pattern 2: Transformation with computeIfPresent
```java
map.computeIfPresent("count", (k, v) -> v + 1);
```

### Pattern 3: Lazy Initialization with computeIfAbsent
```java
Cache cache = map.computeIfAbsent("cache", k -> new Cache());
```

### Pattern 4: Safe Access with getOrDefault
```java
int value = map.getOrDefault("score", 0);
```

### Pattern 5: Functional Iteration
```java
map.forEach((name, score) ->
    System.out.println(name + ": " + score)
);
```

## Future Extensions

### v3.1: Parallel Operations
- `parallelForEach(action, threads)`
- Partition-based parallel iteration
- Thread-safe concurrent processing

### v3.2: Stream Support
- `stream()` -> `Stream<K>`
- `entryStream()` -> `Stream<MapEntry<K,V>>`
- Intermediate and terminal operations

### v3.3: Collection Operations
- `putAll(Map<K,V> other)`
- `removeAll(Collection<K> keys)`
- `retainAll(Collection<K> keys)`
- Bulk import/export operations

### v3.4: Advanced Predicates
- `filterKeys(Predicate<K>)`
- `filterValues(Predicate<V>)`
- `mapValues(Function<V, V>)`
- Conditional transformation

## Conclusion

Version 3.0 brings **complete Java Collections compatibility** with:
- ✓ Full Iterator support
- ✓ Collection views (keySet, values, entrySet)
- ✓ Functional operations (compute*, getOrDefault, putIfAbsent)
- ✓ Lambda-friendly forEach
- ✓ Clean modular organization

The implementation maintains **zero breaking changes** while extending functionality to modern Java patterns.
