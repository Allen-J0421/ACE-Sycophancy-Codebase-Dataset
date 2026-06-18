# QuickSort Refactoring - Architecture & Design Decisions

## Refactoring Summary

This refactoring transforms a basic, single-purpose quicksort implementation into a modular, reusable, and extensible sorting framework.

### Before vs. After

| Aspect | Before | After |
|--------|--------|-------|
| **Reusability** | Hardcoded for `int[]` | Generic interface for any `Comparable<T>` |
| **Extensibility** | Monolithic class | Interface-based design allows multiple implementations |
| **Encapsulation** | Static methods, public logic | Private helper methods, clear boundaries |
| **Type Safety** | None | Full generic type checking |
| **Testing** | No test infrastructure | Comprehensive test suite (12 test cases) |
| **Documentation** | Minimal comments | Detailed README and inline logic |

## Architecture

```
┌─────────────────────────────────────────┐
│         Sorter<T> Interface             │
│  + sort(T[] array): void                │
└──────────────┬──────────────────────────┘
               │
               │ implements
               │
┌──────────────▼──────────────────────────┐
│   QuickSortImpl<T> Implementation        │
│  - quickSort(T[], int, int): void       │
│  - partition(T[], int, int): int        │
│  - swap(T[], int, int): void            │
└──────────────┬──────────────────────────┘
               │
               │ used by
               │
┌──────────────▼──────────────────────────┐
│     QuickSort (Demo/Main Entry)         │
│  + main(String[]): void                 │
└─────────────────────────────────────────┘
```

## Design Patterns Applied

### 1. **Strategy Pattern**
- `Sorter<T>` interface defines the sorting strategy contract
- `QuickSortImpl` is one concrete strategy
- Easy to add other implementations (MergeSort, HeapSort, etc.) without changing client code

### 2. **Generic Programming**
- Type parameters allow code reuse across different types
- Works with any type implementing `Comparable<T>`
- Type-safe at compile time

### 3. **Encapsulation**
- Private helper methods (`quickSort`, `partition`, `swap`) protect implementation details
- Public interface exposes only necessary methods
- Clear separation between interface and implementation

## Key Improvements

### 1. **Generics Support** (Major)
```java
// Before: Limited to int[]
static void quickSort(int[] arr, int low, int high)

// After: Works with any Comparable type
class QuickSortImpl<T extends Comparable<T>> implements Sorter<T>
```

Benefits:
- Reusable for integers, strings, custom objects
- Type-safe sorting
- Eliminates code duplication

### 2. **Interface Abstraction** (Major)
```java
interface Sorter<T extends Comparable<T>> {
    void sort(T[] array);
}
```

Benefits:
- Client code depends on abstraction, not implementation
- Easy to swap algorithms (QuickSort ↔ MergeSort)
- Testable through interface contracts

### 3. **Code Quality** (Minor but Important)

**Loop Boundary Fix:**
```java
// Before: j <= high - 1
for (int j = low; j <= high - 1; j++)

// After: j < high (cleaner, more idiomatic)
for (int j = low; j < high; j++)
```

**Variable Naming:**
```java
// Before: pi (ambiguous)
int pi = partition(arr, low, high);

// After: partitionIndex (clear intent)
int partitionIndex = partition(array, low, high);
```

**Output Formatting:**
```java
// Before: Loop-based printing
for (int val : arr) {
    System.out.print(val + " ");
}

// After: Uses Arrays.toString()
System.out.println("Sorted array: " + Arrays.toString(numbers));
```

### 4. **Defensive Programming**
- Empty array check prevents unnecessary recursion:
  ```java
  if (array.length > 0) {
      quickSort(array, 0, array.length - 1);
  }
  ```

### 5. **Comprehensive Testing**
12 test cases covering:
- Basic sorting scenarios
- Edge cases (empty, single element, duplicates)
- Already sorted / reverse sorted arrays
- Negative numbers
- Custom objects
- Different data types (Integer, String, custom classes)

**Test Coverage:**
- Correctness verification for various input patterns
- Regression prevention
- Documentation through examples

## Performance Characteristics

- **Time Complexity:** 
  - Average: O(n log n)
  - Worst case: O(n²) when pivot selection is poor
  - Best case: O(n log n)

- **Space Complexity:** O(log n) due to recursion stack

## Future Enhancement Opportunities

### 1. **Pivot Selection Strategies**
```java
- Random pivot (better average case)
- Median-of-three (hybrid approach)
- Randomized quicksort (probabilistic guarantees)
```

### 2. **Dual-Pivot Quicksort**
- Slightly faster due to cache efficiency
- More complex implementation

### 3. **Hybrid Approaches**
- Switch to insertion sort for small subarrays
- Use introsort (quicksort + heapsort fallback)

### 4. **More Sorter Implementations**
```java
class MergeSortImpl<T extends Comparable<T>> implements Sorter<T> { ... }
class HeapSortImpl<T extends Comparable<T>> implements Sorter<T> { ... }
```

### 5. **Comparator Support**
```java
interface Sorter<T> {
    void sort(T[] array);
    void sort(T[] array, Comparator<T> comparator);
}
```

## Files Included

1. **quick_sort.java** - Core implementation with demo
2. **QuickSortTest.java** - Comprehensive test suite
3. **README.md** - Usage guide and algorithm explanation
4. **ARCHITECTURE.md** - This document

## Testing & Verification

All 12 tests pass successfully:
- ✓ Basic sorting
- ✓ Edge cases
- ✓ Multiple data types
- ✓ Custom objects
- ✓ Error conditions

```
=== Test Results ===
Passed: 12/12
✓ All tests passed!
```

## Maintenance & Extensibility

- **Low coupling:** Clients depend only on `Sorter<T>` interface
- **High cohesion:** Related sorting logic grouped in `QuickSortImpl`
- **Easy testing:** Can mock `Sorter<T>` for testing dependent classes
- **Clear contracts:** Interface defines expected behavior
