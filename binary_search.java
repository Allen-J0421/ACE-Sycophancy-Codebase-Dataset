class BinarySearch {
    static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        int low = 0, high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        Integer[] numbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(numbers, target);

        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
