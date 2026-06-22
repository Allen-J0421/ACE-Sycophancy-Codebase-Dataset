import java.util.StringJoiner;

public final class BubbleSortDemo {

    private static final int[] DEMO_VALUES = { 64, 34, 25, 12, 22, 11, 90 };

    private BubbleSortDemo() {
        // Demo utility class.
    }

    public static void main(String[] args) {
        int[] values = DEMO_VALUES.clone();
        BubbleSort.sortInPlace(values);

        System.out.println("Sorted array: ");
        System.out.println(formatArray(values));
    }

    private static String formatArray(int[] values) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int value : values) {
            joiner.add(Integer.toString(value));
        }

        return joiner.toString();
    }
}
