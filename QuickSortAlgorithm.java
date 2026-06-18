final class QuickSortAlgorithm {

    private static final int INSERTION_SORT_THRESHOLD = 12;
    private final int[] values;

    private QuickSortAlgorithm(int[] values) {
        this.values = values;
    }

    static void sort(int[] values, IndexRange range) {
        new QuickSortAlgorithm(values).quickSort(range);
    }

    private void quickSort(IndexRange range) {
        while (range.length() > INSERTION_SORT_THRESHOLD) {
            Partition partition = partition(range, values[selectPivotIndex(range)]);

            if (partition.left().length() < partition.right().length()) {
                quickSort(partition.left());
                range = partition.right();
            } else {
                quickSort(partition.right());
                range = partition.left();
            }
        }

        insertionSort(range);
    }

    private Partition partition(IndexRange range, int pivotValue) {
        int lessThanBoundary = range.fromIndex;
        int currentIndex = range.fromIndex;
        int greaterThanBoundary = range.toIndex;

        while (currentIndex < greaterThanBoundary) {
            if (values[currentIndex] < pivotValue) {
                swap(lessThanBoundary, currentIndex);
                lessThanBoundary++;
                currentIndex++;
            } else if (values[currentIndex] > pivotValue) {
                greaterThanBoundary--;
                swap(currentIndex, greaterThanBoundary);
            } else {
                currentIndex++;
            }
        }

        return new Partition(
                IndexRange.of(values.length, range.fromIndex, lessThanBoundary),
                IndexRange.of(values.length, greaterThanBoundary, range.toIndex));
    }

    private int selectPivotIndex(IndexRange range) {
        int lastIndex = range.toIndex - 1;
        int middleIndex = range.fromIndex + (range.length() >>> 1);
        return medianOfThreeIndex(range.fromIndex, middleIndex, lastIndex);
    }

    private int medianOfThreeIndex(int firstIndex, int secondIndex, int thirdIndex) {
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

    private void insertionSort(IndexRange range) {
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

    private void swap(int leftIndex, int rightIndex) {
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
