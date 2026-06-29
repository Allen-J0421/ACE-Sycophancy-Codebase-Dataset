class BinarySearch {
    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedNumbers[mid] == target) {
                return mid;
            }

            if (sortedNumbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    private static void printSearchResult(int result) {
        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }

    public static void main(String[] args) {
        int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedNumbers, target);

        printSearchResult(result);
    }
}
