# Sorting

A small, well-factored sorting package, grown from a single procedural
`bubble_sort.java` file into a set of interchangeable algorithms behind one
Strategy interface.

## Structure

All sources live in the `sorting` package:

| File                     | Responsibility                                                       |
|--------------------------|---------------------------------------------------------------------|
| `Sorter.java`            | Strategy interface for in-place, comparator-driven sorting.         |
| `SortObserver.java`      | Hook notified of each comparison and swap (default no-ops).         |
| `SortStats.java`         | `SortObserver` that counts comparisons and swaps.                   |
| `IntComparator.java`     | Primitive `int` comparison function (no boxing).                    |
| `BubbleSort.java`        | Bubble sort (early-exit optimized): generic + primitive `int[]`.    |
| `InsertionSort.java`     | Insertion sort: O(n) on nearly-sorted input, stable.                |
| `MergeSort.java`         | Top-down merge sort: O(n log n) always, stable.                     |
| `QuickSort.java`         | In-place quicksort: median-of-three pivot, O(log n) stack.          |
| `SortSupport.java`       | Package-private shared helpers (element swap, argument checks).      |
| `IntArrayFormatter.java` | Formats `int[]` for display, separate from the algorithm and I/O.   |
| `SortingDemo.java`       | `main` entry point; demonstrates swapping strategies.               |
| `SortingTest.java`       | Dependency-free test harness; non-zero exit on failure.             |

## Algorithms

Every algorithm implements `Sorter`, so client code picks one at runtime and
sorts any object type with any `Comparator`:

```java
Sorter sorter = new MergeSort();        // or BubbleSort / InsertionSort / QuickSort
sorter.sort(values);                    // natural order
sorter.sort(values, Comparator.reverseOrder());
```

| Algorithm   | Best     | Average    | Worst      | Stable | Extra space |
|-------------|----------|------------|------------|--------|-------------|
| Bubble      | O(n)     | O(n²)      | O(n²)      | yes    | O(1)        |
| Insertion   | O(n)     | O(n²)      | O(n²)      | yes    | O(1)        |
| Merge       | O(n log n) | O(n log n) | O(n log n) | yes | O(n)        |
| Quick       | O(n log n) | O(n log n) | O(n²)    | no     | O(log n)    |

`BubbleSort` additionally offers an allocation-free primitive `int[]` path with
an `IntComparator` — something the JDK's `Arrays.sort(int[])` cannot do.

## Instrumentation

Every generic sort accepts an optional `SortObserver`, notified of each
comparison and swap. `SortStats` is a ready-made counting observer:

```java
SortStats stats = new SortStats();
new BubbleSort().sort(values, Comparator.naturalOrder(), stats);
System.out.println(stats);   // e.g. "21 comparisons, 14 swaps"
```

This makes algorithmic behavior observable and testable — for instance, bubble
sort on already-sorted input reports `n-1` comparisons and zero swaps (its
early exit), whereas reverse input costs `n(n-1)/2` of each. Insertion and merge
sort shift/copy rather than swap, so they report zero swaps. The observer
defaults to `SortObserver.NO_OP`, which the two- and one-argument `sort`
overloads use, so the uninstrumented path carries no overhead. The primitive
`int[]` paths are not instrumented.

### History

Refactored from the original single-file `bubble_sort.java`: separated the
algorithm, formatting, demo, and tests; made the algorithm generic and
comparator-driven; gave the primitive path comparator symmetry; then added the
insertion/merge/quick implementations behind the `Sorter` interface. The
original primitive bubble-sort output is still reproduced by `SortingDemo`.

## Build & run

```sh
javac sorting/*.java

java sorting.SortingDemo   # run the demo
java sorting.SortingTest   # run the tests
```
