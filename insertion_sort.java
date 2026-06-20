final class InsertionSort {

    void sort(int[] values) {
        for (int currentIndex = 1; currentIndex < values.length; currentIndex++) {
            int currentValue = values[currentIndex];
            int sortedIndex = currentIndex - 1;

            while (sortedIndex >= 0 && values[sortedIndex] > currentValue) {
                values[sortedIndex + 1] = values[sortedIndex];
                sortedIndex--;
            }

            values[sortedIndex + 1] = currentValue;
        }
    }

    static void printArray(int[] values) {
        StringBuilder output = new StringBuilder();

        for (int value : values) {
            output.append(value).append(' ');
        }

        System.out.println(output);
    }

    public static void main(String[] args) {
        int[] values = {12, 11, 13, 5, 6};

        InsertionSort sorter = new InsertionSort();
        sorter.sort(values);

        printArray(values);
    }
}
