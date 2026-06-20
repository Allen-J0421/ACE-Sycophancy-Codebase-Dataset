final class InsertionSort {

    private InsertionSort() {
    }

    static void sort(int[] values) {
        for (int unsortedIndex = 1; unsortedIndex < values.length; unsortedIndex++) {
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
            output.append(value).append(' ');
        }

        return output.toString();
    }

    private static void insert(int[] values, int unsortedIndex) {
        int valueToInsert = values[unsortedIndex];
        int insertionIndex = shiftLargerValuesRight(values, unsortedIndex, valueToInsert);

        values[insertionIndex] = valueToInsert;
    }

    private static int shiftLargerValuesRight(int[] values, int unsortedIndex, int valueToInsert) {
        int sortedIndex = unsortedIndex - 1;

        while (sortedIndex >= 0 && values[sortedIndex] > valueToInsert) {
            values[sortedIndex + 1] = values[sortedIndex];
            sortedIndex--;
        }

        return sortedIndex + 1;
    }
}
