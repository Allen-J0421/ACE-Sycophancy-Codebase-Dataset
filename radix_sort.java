import java.util.Arrays;
import java.util.function.ToIntFunction;

class RadixSorter<T> {

    private final ToIntFunction<T> keyOf;

    public RadixSorter(ToIntFunction<T> keyOf) {
        this.keyOf = keyOf;
    }

    public void sort(T[] arr) {
        int max = getMax(arr);
        for (int exp = 1; max / exp > 0; exp *= 10)
            countSort(arr, exp);
    }

    private int getMax(T[] arr) {
        int max = keyOf.applyAsInt(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            int k = keyOf.applyAsInt(arr[i]);
            if (k > max) max = k;
        }
        return max;
    }

    @SuppressWarnings("unchecked")
    private void countSort(T[] arr, int exp) {
        // new T[n] is illegal due to type erasure; Object[] is safe because we only read T back out
        T[] output = (T[]) new Object[arr.length];
        int[] count = new int[10];

        for (T item : arr)
            count[(keyOf.applyAsInt(item) / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = arr.length - 1; i >= 0; i--) {
            int bucket = (keyOf.applyAsInt(arr[i]) / exp) % 10;
            output[count[bucket] - 1] = arr[i];
            count[bucket]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }
}

class Radix {

    public static void main(String[] args) {
        Integer[] numbers = { 170, 45, 75, 90, 802, 24, 2, 66 };
        new RadixSorter<>(Integer::intValue).sort(numbers);
        System.out.println(Arrays.toString(numbers));

        String[] words = { "banana", "fig", "apple", "kiwi", "date" };
        new RadixSorter<>(String::length).sort(words);
        System.out.println(Arrays.toString(words));
    }
}
