final class QuickSortAlgorithm {

    private static final int INSERTION_SORT_THRESHOLD = 12;

    private QuickSortAlgorithm() {
    }

    static void sort(int[] values, IndexRange range) {
        quickSort(values, range);
    }

    private static void quickSort(int[] values, IndexRange range) {
        while (range.length() > INSERTION_SORT_THRESHOLD) {
            Partition partition = partition(values, range, values[selectPivotIndex(values, range)]);

            if (partition.left().length() < partition.right().length()) {
                quickSort(values, partition.left());
                range = partition.right();
            } else {
                quickSort(values, partition.right());
                range = partition.left();
            }
        }

        insertionSort(values, range);
    }

    private static Partition partition(int[] values, IndexRange range, int pivotValue) {
        int lessThanBoundary = range.fromIndex;
        int currentIndex = range.fromIndex;
        int greaterThanBoundary = range.toIndex;

        while (currentIndex < greaterThanBoundary) {
            if (values[currentIndex] < pivotValue) {
                swap(values, lessThanBoundary, currentIndex);
                lessThanBoundary++;
                currentIndex++;
            } else if (values[currentIndex] > pivotValue) {
                greaterThanBoundary--;
                swap(values, currentIndex, greaterThanBoundary);
            } else {
                currentIndex++;
            }
        }

        return new Partition(
                IndexRange.of(values.length, range.fromIndex, lessThanBoundary),
                IndexRange.of(values.length, greaterThanBoundary, range.toIndex));
    }

    private static int selectPivotIndex(int[] values, IndexRange range) {
        int lastIndex = range.toIndex - 1;
        int middleIndex = range.fromIndex + (range.length() >>> 1);
        return medianOfThreeIndex(values, range.fromIndex, middleIndex, lastIndex);
    }

    private static int medianOfThreeIndex(int[] values, int firstIndex, int secondIndex, int thirdIndex) {
        int firstValue = values[firstIndex];
        int secondValue = values[secondIndex];
        int thirdValue = values[thirdIndex];

        if (firstValue < secondValue) {
            if (secondValue < thirdValue) {
                return secondIndex;
            }

            return firstValue < thirdValue ? thirdIndex : firstIndex;
        }

        if (firstValue < thirdValue) {
            return firstIndex;
        }

        return secondValue < thirdValue ? thirdIndex : secondIndex;
    }

    private static void insertionSort(int[] values, IndexRange range) {
        for (int index = range.fromIndex + 1; index < range.toIndex; index++) {
            int currentValue = values[index];
            int insertionIndex = index - 1;

            while (insertionIndex >= range.fromIndex && values[insertionIndex] > currentValue) {
                values[insertionIndex + 1] = values[insertionIndex];
                insertionIndex--;
            }

            values[insertionIndex + 1] = currentValue;
        }
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        if (leftIndex == rightIndex) {
            return;
        }

        int temporaryValue = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporaryValue;
    }

    private static final class Partition {

        private final IndexRange left;
        private final IndexRange right;

        private Partition(IndexRange left, IndexRange right) {
            this.left = left;
            this.right = right;
        }

        private IndexRange left() {
            return left;
        }

        private IndexRange right() {
            return right;
        }
    }
}
