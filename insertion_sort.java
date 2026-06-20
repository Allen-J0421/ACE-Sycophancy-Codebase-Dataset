final class InsertionSort {

    private static final int FIRST_UNSORTED_INDEX = 1;
    private static final char VALUE_SEPARATOR = ' ';

    private InsertionSort() {
    }

    static void sort(int[] values) {
        for (int unsortedIndex = FIRST_UNSORTED_INDEX; unsortedIndex < values.length; unsortedIndex++) {
            insert(values, unsortedIndex);
        }
    }

    static void printArray(int[] values) {
        System.out.println(formatArray(values));
    }

    public static void main(String[] args) {
        int[] values = exampleValues();

        sort(values);
        printArray(values);
    }

    private static int[] exampleValues() {
        return new int[] {12, 11, 13, 5, 6};
    }

    private static String formatArray(int[] values) {
        StringBuilder output = new StringBuilder();

        for (int value : values) {
            output.append(value).append(VALUE_SEPARATOR);
        }

        return output.toString();
    }

    private static void insert(int[] values, int unsortedIndex) {
        if (isAlreadyOrdered(values, unsortedIndex)) {
            return;
        }

        int valueToInsert = values[unsortedIndex];
        int insertionIndex = shiftLargerValuesRight(values, unsortedIndex, valueToInsert);

        values[insertionIndex] = valueToInsert;
    }

    private static int shiftLargerValuesRight(int[] values, int unsortedIndex, int valueToInsert) {
        int sortedIndex = unsortedIndex - 1;

        while (hasLargerSortedValue(values, sortedIndex, valueToInsert)) {
            values[sortedIndex + 1] = values[sortedIndex];
            sortedIndex--;
        }

        return sortedIndex + 1;
    }

    private static boolean hasLargerSortedValue(int[] values, int sortedIndex, int valueToInsert) {
        return sortedIndex >= 0 && values[sortedIndex] > valueToInsert;
    }

    private static boolean isAlreadyOrdered(int[] values, int unsortedIndex) {
        return values[unsortedIndex - 1] <= values[unsortedIndex];
    }
}
