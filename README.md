# KMP Pattern Searching

This project contains a small Java implementation of Knuth-Morris-Pratt pattern
searching.

## Structure

- `src/main/java/kmp/KMPSearch.java` contains the reusable search API.
- `src/main/java/kmp/KmpPatternSearchingDemo.java` contains the demo runner.
- `src/main/java/KMPSearch.java` keeps the default-package API facade working.
- `src/main/java/kmp_pattern_searching.java` keeps the original lowercase
  launcher working.
- `src/test/java/kmp/KMPSearchTest.java` contains lightweight regression tests
  with no external dependencies.
- `src/test/java/CompatibilityTest.java` covers the default-package facades and
  legacy launcher.

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
