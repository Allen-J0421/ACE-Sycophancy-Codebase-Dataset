# Bubble Sort

A small, well-factored bubble-sort package, refactored from a single
procedural `bubble_sort.java` file.

## Structure

All sources live in the `sorting` package:

| File                     | Responsibility                                                        |
|--------------------------|----------------------------------------------------------------------|
| `Sorter.java`            | Strategy interface for in-place, comparator-driven sorting.          |
| `BubbleSort.java`        | Bubble sort (early-exit optimized): generic + primitive `int[]`.     |
| `IntArrayFormatter.java` | Formats `int[]` for display, separate from the algorithm and I/O.    |
| `BubbleSortDemo.java`    | `main` entry point; reproduces the original program's output.        |
| `BubbleSortTest.java`    | Dependency-free test harness; non-zero exit on failure.              |

### What changed from the original

- **Separation of concerns** — sorting, formatting, the demo, and tests are no
  longer crammed into one class.
- **Reusable algorithm** — a `Sorter` Strategy interface plus a generic,
  `Comparator`-based `BubbleSort` work on any element type and ordering, while a
  primitive `int[]` overload preserves the original allocation-free path.
- **Removed the redundant `n` parameter** — length is derived from the array.
- **Tests** — edge cases (empty, single, duplicates, negatives, reverse) and
  both the primitive and generic paths are covered.
- **Behavior preserved** — `BubbleSortDemo` prints exactly what the original did.

## Build & run

```sh
javac sorting/*.java

java sorting.BubbleSortDemo   # run the demo
java sorting.BubbleSortTest   # run the tests
```
