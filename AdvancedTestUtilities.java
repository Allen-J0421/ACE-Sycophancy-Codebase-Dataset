import java.util.Arrays;
import java.util.Random;

class AdvancedTestUtilities {

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static Integer[] generateRandomArray(int size, int seed) {
        Integer[] array = new Integer[size];
        Random random = new Random(seed);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }
        return array;
    }

    public static Integer[] generateSortedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    public static Integer[] generateReverseSortedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }

    public static Integer[] generateNearlySortedArray(int size, double sortedRatio, int seed) {
        Integer[] array = generateSortedArray(size);
        Random random = new Random(seed);
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

    public static Integer[] generateDuplicateArray(int size, double uniqueRatio, int seed) {
        Integer[] array = new Integer[size];
        int uniqueCount = Math.max(1, (int) (size * uniqueRatio));
        Random random = new Random(seed);

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(uniqueCount);
        }

        return array;
    }

    public static Integer[] generateSawtoothArray(int size) {
        Integer[] array = new Integer[size];
        int segmentSize = (int) Math.sqrt(size);

        for (int i = 0; i < size; i++) {
            array[i] = i % segmentSize;
        }

        return array;
    }

    public static <T extends Comparable<T>> String analyzeArray(T[] array) {
        if (array.length == 0) {
            return "Empty array";
        }

        int inversions = countInversions(array);
        boolean isSorted = isSorted(array);
        int uniqueCount = countUnique(array);

        return String.format(
            "Array Analysis: length=%d, sorted=%s, inversions=%d, unique=%d (%.1f%%)",
            array.length, isSorted, inversions, uniqueCount,
            (100.0 * uniqueCount / array.length)
        );
    }

    private static <T extends Comparable<T>> int countInversions(T[] array) {
        int count = 0;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i].compareTo(array[j]) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static <T extends Comparable<T>> int countUnique(T[] array) {
        if (array.length == 0) return 0;

        T[] sorted = array.clone();
        Arrays.sort(sorted);

        int count = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i - 1].compareTo(sorted[i]) != 0) {
                count++;
            }
        }
        return count;
    }
}
