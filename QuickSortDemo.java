import java.util.Objects;

public final class QuickSortDemo {
    private static final int[] SAMPLE_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = QuickSort.sortedCopy(SAMPLE_VALUES);

        System.out.print(formatValues(values));
    }

    static String formatValues(int[] values) {
        Objects.requireNonNull(values, "values");

        StringBuilder formattedValues = new StringBuilder();
        for (int value : values) {
            formattedValues.append(value).append(' ');
        }

        return formattedValues.toString();
    }
}
