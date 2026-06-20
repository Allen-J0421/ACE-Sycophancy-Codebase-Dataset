final class InsertionSort {

    private InsertionSort() {
    }

    static void sort(int[] values) {
        for (int currentIndex = 1; currentIndex < values.length; currentIndex++) {
            insert(values, currentIndex);
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

    private static void insert(int[] values, int currentIndex) {
        int currentValue = values[currentIndex];
        int insertionIndex = shiftLargerValuesRight(values, currentIndex, currentValue);

        values[insertionIndex] = currentValue;
    }

    private static int shiftLargerValuesRight(int[] values, int currentIndex, int currentValue) {
        int sortedIndex = currentIndex - 1;

        while (sortedIndex >= 0 && values[sortedIndex] > currentValue) {
            values[sortedIndex + 1] = values[sortedIndex];
            sortedIndex--;
        }

        return sortedIndex + 1;
    }
}
