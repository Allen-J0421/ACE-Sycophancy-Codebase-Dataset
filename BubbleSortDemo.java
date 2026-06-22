import java.util.StringJoiner;

public final class BubbleSortDemo {

    private static final int[] DEMO_VALUES = { 64, 34, 25, 12, 22, 11, 90 };
    private static final String SORTED_HEADER = "Sorted array: ";

    private BubbleSortDemo() {
        // Demo utility class.
    }

    public static void main(String[] args) {
        int[] values = BubbleSort.sortedCopy(DEMO_VALUES);

        System.out.println(SORTED_HEADER);
        System.out.println(joinValues(values));
    }

    private static String joinValues(int[] values) {
        StringJoiner joiner = new StringJoiner(" ");

        for (int value : values) {
            joiner.add(Integer.toString(value));
        }

        return joiner.toString();
    }
}
