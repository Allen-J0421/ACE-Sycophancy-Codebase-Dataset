import java.util.Arrays;

class Main {

    public static void main(String[] args) {
        runCase("Random order", new Integer[]{ 64, 34, 25, 12, 22, 11, 90 });
        runCase("Already sorted", new Integer[]{ 1, 2, 3, 4, 5 });
        runCase("Reverse order", new Integer[]{ 5, 4, 3, 2, 1 });
        runCase("Single element", new Integer[]{ 42 });
        runCase("Strings", new String[]{ "banana", "apple", "cherry" });
    }

    private static <T extends Comparable<T>> void runCase(String label, T[] arr) {
        Sorter<T> sorter = new BubbleSort<>();
        sorter.sort(arr);
        System.out.println(label + ": " + Arrays.toString(arr));
    }
}
