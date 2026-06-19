# KMP Pattern Searching

This project contains a small Java implementation of Knuth-Morris-Pratt pattern
searching.

## Structure

- `KMPSearch.java` contains the reusable search API.
- `KmpPatternSearchingDemo.java` contains the demo runner.
- `kmp_pattern_searching.java` keeps the original lowercase launcher working.
- `KMPSearchTest.java` contains lightweight regression tests with no external
  dependencies.

## Commands

Compile and run tests:

```sh
make test
```

Run the demo:

```sh
make demo
```

Run the legacy launcher:

```sh
make legacy
```

Remove local build output:

```sh
make clean
```
