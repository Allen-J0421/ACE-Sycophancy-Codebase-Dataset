final class BinarySearch {
    private BinarySearch() {
    }

    static int indexOf(int[] sortedValues, int target) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int midpoint = low + (high - low) / 2;
            int value = sortedValues[midpoint];

            if (value == target) {
                return midpoint;
            }

            if (value < target) {
                low = midpoint + 1;
            } else {
                high = midpoint - 1;
            }
        }

        return -1;
    }

    static boolean contains(int[] sortedValues, int target) {
        return indexOf(sortedValues, target) >= 0;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int index = indexOf(values, target);

        if (index == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + index);
        }
    }
}
