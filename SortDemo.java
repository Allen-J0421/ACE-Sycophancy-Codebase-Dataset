import java.util.Comparator;

public class SortDemo {
    public static void main(String[] args) {
        testBasicSorting();
        testReverseSorting();
        testWithLogging();
        testCustomThreshold();
        testValidation();
    }

    private static void testBasicSorting() {
        Integer[] array = {38, 27, 43, 10};
        MergeSort<Integer> sorter = new MergeSort<>();
        sorter.sort(array);
        printResult("Ascending order", array, sorter.getStatistics());
    }

    private static void testReverseSorting() {
        Integer[] array = {38, 27, 43, 10};
        MergeSort<Integer> sorter = new MergeSort<Integer>(Comparator.reverseOrder());
        sorter.sort(array);
        printResult("Descending order", array, sorter.getStatistics());
    }

    private static void testWithLogging() {
        Integer[] array = {38, 27, 43, 10};
        SortConfiguration<Integer> config = SortConfiguration.<Integer>builder()
                .withComparator(Comparable::compareTo)
                .withLogger(new SortConfiguration.ConsoleLogger())
                .build();
        MergeSort<Integer> sorter = new MergeSort<>(config);
        sorter.sort(array);
        printResult("With logging", array, sorter.getStatistics());
    }

    private static void testCustomThreshold() {
        Integer[] array = new Integer[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = array.length - i;
        }

        SortConfiguration<Integer> config = SortConfiguration.<Integer>builder()
                .withComparator(Comparable::compareTo)
                .withThreshold(20)
                .build();
        MergeSort<Integer> sorter = new MergeSort<>(config);
        sorter.sort(array);
        printResult("Custom threshold (20)", array, sorter.getStatistics());
    }

    private static void testValidation() {
        Integer[] array = {5, 2, 8, 1, 9};
        SortConfiguration<Integer> config = SortConfiguration.<Integer>builder()
                .withComparator(Comparable::compareTo)
                .withValidation(true)
                .build();
        MergeSort<Integer> sorter = new MergeSort<>(config);
        sorter.sort(array);
        printResult("With validation", array, sorter.getStatistics());
    }

    private static <T> void printResult(String label, T[] array, SortStatistics stats) {
        System.out.println("\n" + label + ":");
        printArray(array, 10);
        if (stats != null) {
            System.out.println("Statistics: " + stats);
        }
    }

    private static <T> void printArray(T[] array, int limit) {
        System.out.print("Result: ");
        for (int i = 0; i < Math.min(array.length, limit); i++) {
            System.out.print(array[i] + " ");
        }
        if (array.length > limit) {
            System.out.print("...");
        }
        System.out.println();
    }
}
