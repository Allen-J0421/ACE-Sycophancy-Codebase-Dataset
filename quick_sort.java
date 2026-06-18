class QuickSort {
    private static final int[] SAMPLE_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSort() {
    }

    static void sort(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        sortRange(values, 0, values.length - 1);
    }

    private static void sortRange(int[] values, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }

        int pivotIndex = partition(values, startIndex, endIndex);

        sortRange(values, startIndex, pivotIndex - 1);
        sortRange(values, pivotIndex + 1, endIndex);
    }

    private static int partition(int[] values, int startIndex, int endIndex) {
        int pivot = values[endIndex];
        int partitionIndex = startIndex - 1;

        for (int currentIndex = startIndex; currentIndex < endIndex; currentIndex++) {
            if (values[currentIndex] < pivot) {
                partitionIndex++;
                swap(values, partitionIndex, currentIndex);
            }
        }

        int pivotIndex = partitionIndex + 1;
        swap(values, pivotIndex, endIndex);

        return pivotIndex;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }

        int temporaryValue = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temporaryValue;
    }

    private static void printValues(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
    }

    public static void main(String[] args) {
        int[] values = SAMPLE_VALUES.clone();

        sort(values);

        printValues(values);
    }
}
