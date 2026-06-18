import java.util.Objects;

public final class QuickSort {
    private QuickSort() {
    }

    public static int[] sortedCopy(int[] values) {
        int[] sortedValues = Objects.requireNonNull(values, "values").clone();

        sort(sortedValues);

        return sortedValues;
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        quickSort(values, 0, values.length - 1);
    }

    private static void quickSort(int[] values, int startIndex, int endIndex) {
        while (startIndex < endIndex) {
            PartitionBounds partitionBounds = partition(values, startIndex, endIndex);

            int leftPartitionSize = partitionBounds.leftPartitionSize(startIndex);
            int rightPartitionSize = partitionBounds.rightPartitionSize(endIndex);

            // Recurse into the smaller partition first to limit stack growth.
            if (leftPartitionSize < rightPartitionSize) {
                quickSort(values, startIndex, partitionBounds.lessThanEndIndex);
                startIndex = partitionBounds.greaterThanStartIndex;
            } else {
                quickSort(values, partitionBounds.greaterThanStartIndex, endIndex);
                endIndex = partitionBounds.lessThanEndIndex;
            }
        }
    }

    private static PartitionBounds partition(int[] values, int startIndex, int endIndex) {
        int pivot = values[choosePivotIndex(startIndex, endIndex)];
        int nextLowerValueIndex = startIndex;
        int currentIndex = startIndex;
        int nextHigherValueIndex = endIndex;

        while (currentIndex <= nextHigherValueIndex) {
            if (values[currentIndex] < pivot) {
                swap(values, nextLowerValueIndex, currentIndex);
                nextLowerValueIndex++;
                currentIndex++;
            } else if (values[currentIndex] > pivot) {
                swap(values, currentIndex, nextHigherValueIndex);
                nextHigherValueIndex--;
            } else {
                currentIndex++;
            }
        }

        return new PartitionBounds(nextLowerValueIndex - 1, nextHigherValueIndex + 1);
    }

    private static int choosePivotIndex(int startIndex, int endIndex) {
        return startIndex + (endIndex - startIndex) / 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }

        int temporaryValue = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temporaryValue;
    }

    public static void main(String[] args) {
        QuickSortDemo.main(args);
    }

    private static final class PartitionBounds {
        private final int lessThanEndIndex;
        private final int greaterThanStartIndex;

        private PartitionBounds(int lessThanEndIndex, int greaterThanStartIndex) {
            this.lessThanEndIndex = lessThanEndIndex;
            this.greaterThanStartIndex = greaterThanStartIndex;
        }

        private int leftPartitionSize(int startIndex) {
            return Math.max(0, lessThanEndIndex - startIndex + 1);
        }

        private int rightPartitionSize(int endIndex) {
            return Math.max(0, endIndex - greaterThanStartIndex + 1);
        }
    }
}
