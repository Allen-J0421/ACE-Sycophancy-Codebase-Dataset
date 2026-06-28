class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedValues, int target) {
        int lowerBound = 0;
        int upperBound = sortedValues.length - 1;

        while (lowerBound <= upperBound) {
            int mid = midpoint(lowerBound, upperBound);
            int midValue = sortedValues[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                lowerBound = mid + 1;
            } else {
                upperBound = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }

    private static int midpoint(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    static boolean isFoundIndex(int index) {
        return index != NOT_FOUND;
    }
}
