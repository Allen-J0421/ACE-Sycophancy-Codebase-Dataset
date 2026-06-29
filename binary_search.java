final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int midpoint = left + (right - left) / 2;
            int midpointValue = sortedValues[midpoint];

            if (midpointValue == target) {
                return midpoint;
            }

            if (midpointValue < target) {
                left = midpoint + 1;
            } else {
                right = midpoint - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String searchMessage(int resultIndex) {
        if (resultIndex == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + resultIndex;
    }

    public static void main(String[] args) {
        int[] sortedValues = {2, 3, 4, 10, 40};
        int target = 10;
        int resultIndex = binarySearch(sortedValues, target);

        System.out.println(searchMessage(resultIndex));
    }
}
