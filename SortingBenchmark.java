import java.util.Arrays;
import java.util.Random;

class SortingBenchmark {
    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASUREMENT_ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║         QuickSort Performance Benchmark Suite          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        benchmarkSortingAlgorithms();
    }

    private static void benchmarkSortingAlgorithms() {
        int[] sizes = {1000, 10000, 100000};

        for (int size : sizes) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Array Size: " + size + " elements");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            benchmarkPattern(size, "Random", generateRandomArray(size));
            benchmarkPattern(size, "Nearly Sorted (90%)", generateNearlySortedArray(size, 0.9));
            benchmarkPattern(size, "Reverse Sorted", generateReverseSortedArray(size));
            benchmarkPattern(size, "Many Duplicates", generateDuplicateArray(size, 0.1));

            System.out.println();
        }
    }

    private static void benchmarkPattern(int size, String pattern, Integer[] baseArray) {
        System.out.println("\nPattern: " + pattern);

        Sorter<Integer>[] sorters = new Sorter[]{
            new QuickSortImpl<Integer>(),
            new HybridQuickSort<Integer>(),
            new HybridQuickSort<Integer>(new MedianOfThreePivotSelector<Integer>()),
            new HybridQuickSort<Integer>(new RandomPivotSelector<Integer>()),
            new ThreeWayQuickSort<Integer>(),
            new IntroSort<Integer>(),
            new HeapSort<Integer>()
        };

        String[] names = {
            "QuickSortImpl",
            "HybridQS (Default)",
            "HybridQS (Median-3)",
            "HybridQS (Random)",
            "ThreeWayQS",
            "IntroSort",
            "HeapSort"
        };

        for (int i = 0; i < sorters.length; i++) {
            long totalTime = 0;

            for (int iter = 0; iter < WARMUP_ITERATIONS + MEASUREMENT_ITERATIONS; iter++) {
                Integer[] array = baseArray.clone();
                long startTime = System.nanoTime();
                sorters[i].sort(array);
                long endTime = System.nanoTime();

                if (iter >= WARMUP_ITERATIONS) {
                    totalTime += (endTime - startTime);
                }

                if (!isSorted(array)) {
                    System.out.println("  ✗ " + names[i] + " - FAILED VERIFICATION!");
                    totalTime = Long.MAX_VALUE;
                    break;
                }
            }

            double avgTimeMs = (totalTime / 1_000_000.0) / MEASUREMENT_ITERATIONS;
            System.out.printf("  %-20s: %8.3f ms\n", names[i], avgTimeMs);
        }
    }

    private static Integer[] generateRandomArray(int size) {
        Integer[] array = new Integer[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }
        return array;
    }

    private static Integer[] generateNearlySortedArray(int size, double sortedRatio) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }

        Random random = new Random(42);
        int swapCount = (int) (size * (1 - sortedRatio) / 2);
        for (int i = 0; i < swapCount; i++) {
            int idx1 = random.nextInt(size);
            int idx2 = random.nextInt(size);
            int temp = array[idx1];
            array[idx1] = array[idx2];
            array[idx2] = temp;
        }

        return array;
    }

    private static Integer[] generateReverseSortedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }

    private static Integer[] generateDuplicateArray(int size, double uniqueRatio) {
        Integer[] array = new Integer[size];
        int uniqueCount = (int) (size * uniqueRatio);
        Random random = new Random(42);

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(uniqueCount);
        }

        return array;
    }

    private static boolean isSorted(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }
}
