class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        for (int currentIndex = 1; currentIndex < values.length; currentIndex++) {
            insertValue(values, currentIndex);
        }
    }

    private static void insertValue(int[] values, int currentIndex) {
        int valueToInsert = values[currentIndex];
        int scanIndex = currentIndex - 1;

        while (scanIndex >= 0 && values[scanIndex] > valueToInsert) {
            values[scanIndex + 1] = values[scanIndex];
            scanIndex--;
        }

        values[scanIndex + 1] = valueToInsert;
    }

    static void printArray(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }

        System.out.println();
    }

    public static void main(String[] args) {
        int[] values = {12, 11, 13, 5, 6};

        sort(values);
        printArray(values);
    }
}
