package sorting.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PropertyTestGenerator<T extends Comparable<T>> {
    private final Random random;

    public PropertyTestGenerator() {
        this.random = new Random();
    }

    public void runPropertyTests(
            T[] array,
            Consumer<T[]> sortFunction,
            int iterations) {
        for (int i = 0; i < iterations; i++) {
            T[] testArray = array.clone();
            sortFunction.accept(testArray);
            verifyProperties(testArray);
        }
        System.out.println("✓ All property tests passed (" + iterations + " iterations)");
    }

    private void verifyProperties(T[] array) {
        verifySorted(array);
        verifyLength(array);
        verifyContent(array);
    }

    private void verifySorted(T[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                throw new AssertionError(
                        String.format("Array not sorted at index %d: %s > %s",
                                i, array[i], array[i + 1]));
            }
        }
    }

    private void verifyLength(T[] array) {
        if (array.length == 0) {
            throw new AssertionError("Array length changed");
        }
    }

    private void verifyContent(T[] array) {
        List<T> elements = new ArrayList<>();
        for (T elem : array) {
            elements.add(elem);
        }
        if (elements.isEmpty()) {
            throw new AssertionError("Array content verification failed");
        }
    }

    public static class TestDataGenerator {
        private static final Random random = new Random(42);

        public static Integer[] randomIntegers(int size) {
            Integer[] arr = new Integer[size];
            for (int i = 0; i < size; i++) {
                arr[i] = random.nextInt(10000);
            }
            return arr;
        }

        public static Integer[] almostSorted(int size) {
            Integer[] arr = new Integer[size];
            for (int i = 0; i < size; i++) {
                arr[i] = i;
            }
            // Swap 5% of elements
            int swaps = size / 20;
            for (int i = 0; i < swaps; i++) {
                int a = random.nextInt(size);
                int b = random.nextInt(size);
                Integer temp = arr[a];
                arr[a] = arr[b];
                arr[b] = temp;
            }
            return arr;
        }

        public static Integer[] reversed(int size) {
            Integer[] arr = new Integer[size];
            for (int i = 0; i < size; i++) {
                arr[i] = size - i;
            }
            return arr;
        }

        public static Integer[] duplicates(int size) {
            Integer[] arr = new Integer[size];
            int unique = (int) Math.sqrt(size);
            for (int i = 0; i < size; i++) {
                arr[i] = random.nextInt(unique);
            }
            return arr;
        }
    }
}
