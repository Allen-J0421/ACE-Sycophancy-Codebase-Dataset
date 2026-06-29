class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int value = numbers[middle];

            if (value == target) {
                return middle;
            }

            if (value < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] numbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(numbers, target);

        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
