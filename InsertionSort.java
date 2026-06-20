import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class InsertionSort {

    private InsertionSort() {}

    private static void requireNonNull(Object obj, String name) {
        Objects.requireNonNull(obj, name + " must not be null");
    }

    @FunctionalInterface
    private interface IndexPredicate {
        boolean test(int a, int b);
    }

    @FunctionalInterface
    private interface IndexSwapper {
        void swap(int a, int b);
    }

    private static void sortCore(int fromIndex, int toIndex,
            IndexPredicate isOutOfOrder, IndexSwapper swapper) {
        for (int i = fromIndex + 1; i < toIndex; i++) {
            int j = i;
            while (j > fromIndex && isOutOfOrder.test(j - 1, j)) {
                swapper.swap(j - 1, j);
                j--;
            }
        }
    }

    // byte[]

    public static void sort(byte[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(byte[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { byte t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static byte[] sortedCopy(byte[] arr) {
        requireNonNull(arr, "arr");
        byte[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // char[]

    public static void sort(char[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(char[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { char t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static char[] sortedCopy(char[] arr) {
        requireNonNull(arr, "arr");
        char[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // short[]

    public static void sort(short[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(short[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { short t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static short[] sortedCopy(short[] arr) {
        requireNonNull(arr, "arr");
        short[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // int[]

    public static void sort(int[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(int[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static int[] sortedCopy(int[] arr) {
        requireNonNull(arr, "arr");
        int[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // long[]

    public static void sort(long[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(long[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { long t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static long[] sortedCopy(long[] arr) {
        requireNonNull(arr, "arr");
        long[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // float[]

    public static void sort(float[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(float[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> Float.compare(arr[i], arr[j]) > 0,
            (i, j) -> { float t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static float[] sortedCopy(float[] arr) {
        requireNonNull(arr, "arr");
        float[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // double[]

    public static void sort(double[] arr) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length);
    }

    public static void sort(double[] arr, int fromIndex, int toIndex) {
        requireNonNull(arr, "arr");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> Double.compare(arr[i], arr[j]) > 0,
            (i, j) -> { double t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static double[] sortedCopy(double[] arr) {
        requireNonNull(arr, "arr");
        double[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    // T[]

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        requireNonNull(arr, "arr");
        sort(arr, 0, arr.length, comparator);
    }

    public static <T extends Comparable<? super T>> void sort(T[] arr, int fromIndex, int toIndex) {
        sort(arr, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        requireNonNull(arr, "arr");
        requireNonNull(comparator, "comparator");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> comparator.compare(arr[i], arr[j]) > 0,
            (i, j) -> { T t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] arr) {
        requireNonNull(arr, "arr");
        T[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    public static <T> T[] sortedCopy(T[] arr, Comparator<? super T> comparator) {
        requireNonNull(arr, "arr");
        requireNonNull(comparator, "comparator");
        T[] copy = arr.clone();
        sort(copy, comparator);
        return copy;
    }

    // List<T>

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        sort(list, Comparator.naturalOrder());
    }

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        requireNonNull(list, "list");
        sort(list, 0, list.size(), comparator);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list, int fromIndex, int toIndex) {
        sort(list, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(List<T> list, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        requireNonNull(list, "list");
        requireNonNull(comparator, "comparator");
        Objects.checkFromToIndex(fromIndex, toIndex, list.size());
        sortCore(fromIndex, toIndex,
            (i, j) -> comparator.compare(list.get(i), list.get(j)) > 0,
            (i, j) -> Collections.swap(list, i, j));
    }

    public static <T extends Comparable<? super T>> List<T> sortedCopy(List<T> list) {
        return sortedCopy(list, Comparator.naturalOrder());
    }

    public static <T> List<T> sortedCopy(List<T> list, Comparator<? super T> comparator) {
        requireNonNull(list, "list");
        requireNonNull(comparator, "comparator");
        List<T> copy = new ArrayList<>(list);
        sort(copy, comparator);
        return copy;
    }
}
