import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] numbers, int target) {
        return indexOf(numbers, target);
    }

    static int indexOf(int[] numbers, int target) {
        Objects.requireNonNull(numbers, "numbers");

        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (numbers[mid] == target) {
                return mid;
            }

            if (numbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }
}
