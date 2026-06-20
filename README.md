# Radix Sort

A small Java implementation of radix sort for `int[]` values.

## Behavior

- `Radix.radixSort(...)` sorts signed integers, including negative values and `Integer.MIN_VALUE`.
- Full-array, prefix, and subrange overloads are available.
- `Radix.countSort(...)` is retained as a decimal digit counting-sort helper for non-negative prefixes.
- `Radix.format(...)` returns space-separated values without printing to stdout.
- `Radix.print(...)` preserves the original trailing-space output behavior.

## Commands

Run the regression tests:

```sh
make test
```

Run the demo:

```sh
make run
```

Remove compiled class files:

```sh
make clean
```
