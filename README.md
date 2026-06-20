# Counting Sort

Small Java implementation of stable counting sort for signed integers.

## Layout

- `src/main/java/countingsort/CountingSort.java`
- `src/main/java/countingsort/CountingSortDemo.java`
- `src/test/java/countingsort/CountingSortTest.java`

## Build

```bash
javac -d out $(find src -name '*.java' | sort)
```

## Run

```bash
java -cp out countingsort.CountingSortDemo
java -cp out countingsort.CountingSortTest
```
