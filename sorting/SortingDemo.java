package sorting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demonstrates the {@link Sorter} strategy: the same client code sorts data
 * with any algorithm simply by swapping the strategy instance. Also retains the
 * original primitive {@code int[]} bubble-sort output.
 */
public final class SortingDemo {

    private SortingDemo() {
        // Entry-point holder; not instantiable.
    }

    public static void main(String[] args) {
        // Original behavior: primitive, allocation-free bubble sort.
        int[] data = { 64, 34, 25, 12, 22, 11, 90 };
        BubbleSort.sort(data);
        System.out.println("Sorted array: ");
        System.out.println(IntArrayFormatter.format(data));

        // Strategy pattern: interchangeable algorithms behind one interface.
        Map<String, Sorter> strategies = new LinkedHashMap<>();
        strategies.put("bubble", new BubbleSort());
        strategies.put("insertion", new InsertionSort());
        strategies.put("merge", new MergeSort());
        strategies.put("quick", new QuickSort());

        Integer[] sample = { 64, 34, 25, 12, 22, 11, 90 };
        System.out.println();
        System.out.println("Unsorted:   " + Arrays.toString(sample));
        strategies.forEach((name, sorter) -> {
            Integer[] copy = sample.clone();
            sorter.sort(copy);
            System.out.printf("%-11s %s%n", name + ":", Arrays.toString(copy));
        });

        // Same algorithm, different ordering — just pass a different comparator.
        Integer[] descending = sample.clone();
        new QuickSort().sort(descending, Comparator.reverseOrder());
        System.out.println("quick desc: " + Arrays.toString(descending));
    }
}
