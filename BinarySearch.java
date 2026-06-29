class BinarySearch {
    static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedArray, int target) {
        java.util.Objects.requireNonNull(sortedArray, "sortedArray");

        int left = 0;
        int right = sortedArray.length - 1;

        while (left <= right) {
            int midpoint = midpoint(left, right);
            int comparison = Integer.compare(sortedArray[midpoint], target);

            if (comparison == 0) {
                return midpoint;
            }

            if (comparison < 0) {
                left = midpoint + 1;
            } else {
                right = midpoint - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
