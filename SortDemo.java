import java.util.Comparator;

public class SortDemo {
    public static void main(String[] args) {
        testBasicSorting();
        testReverseSorting();
        testPerformance();
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

    private static void testPerformance() {
        Integer[] array = new Integer[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 10000);
        }

        MergeSort<Integer> sorter = new MergeSort<>();
        sorter.sort(array);
        printResult("Large array (1000 elements)", array, sorter.getStatistics());
    }

    private static <T> void printResult(String label, T[] array, SortStatistics stats) {
        System.out.println("\n" + label + ":");
        printArray(array, 10);
        System.out.println("Statistics: " + stats);
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
