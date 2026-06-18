# QuickSort Refactoring

## Overview
This project implements a generic, reusable QuickSort algorithm with improved code structure and quality.

## Architecture

### Interfaces
- **`Sorter<T>`** - Generic interface defining the sorting contract for any comparable type

### Classes
- **`QuickSortImpl<T>`** - Generic implementation of the Sorter interface using the QuickSort algorithm
  - Time Complexity: O(n log n) average, O(n²) worst case
  - Space Complexity: O(log n) due to recursion stack
  - In-place sorting algorithm

- **`QuickSort`** - Main class with demo usage

## Key Improvements

### 1. **Generics Support**
   - Works with any type implementing `Comparable<T>`
   - Type-safe sorting for integers, strings, custom objects, etc.

### 2. **Encapsulation**
   - Separated concerns: interface (Sorter), implementation (QuickSortImpl), usage (QuickSort)
   - Private helper methods protect implementation details

### 3. **Code Quality**
   - Clearer variable names (pivot, partitionIndex vs pi)
   - Consistent formatting and proper indentation
   - No unnecessary imports or unused code
   - Boundary check removed from loop: `j < high` instead of `j <= high - 1`

### 4. **Better Output**
   - Uses `Arrays.toString()` for readable output
   - Clear display of sorted results

### 5. **Defensive Programming**
   - Null-safe array length check before sorting
   - Proper bounds handling in partition logic

## Usage

```java
// Sort integers
Integer[] numbers = {10, 7, 8, 9, 1, 5};
Sorter<Integer> sorter = new QuickSortImpl<>();
sorter.sort(numbers);

// Sort strings
String[] words = {"zebra", "apple", "mango"};
Sorter<String> stringSorter = new QuickSortImpl<>();
stringSorter.sort(words);

// Sort custom objects (must implement Comparable)
class Person implements Comparable<Person> {
    int age;
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age);
    }
}
```

## Algorithm Explanation

QuickSort is a divide-and-conquer algorithm that works as follows:

1. **Partition**: Select a pivot element and partition the array so elements smaller than pivot are on the left, larger on the right
2. **Recursively Sort**: Apply the same process to left and right sub-arrays
3. **Combine**: Arrays are already sorted in-place during partitioning

The choice of pivot (here, the last element) affects performance. For better average case performance, consider median-of-three or random pivot selection.
