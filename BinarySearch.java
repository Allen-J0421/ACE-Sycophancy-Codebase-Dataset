import java.util.Objects;

final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int indexOf(int[] sortedArray, int target) {
        Objects.requireNonNull(sortedArray, "sortedArray");

        int left = 0;
        int right = sortedArray.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int middleValue = sortedArray[middle];

            if (middleValue == target) {
                return middle;
            }
            if (middleValue < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    static boolean contains(int[] sortedArray, int target) {
        return indexOf(sortedArray, target) != NOT_FOUND;
    }
}
