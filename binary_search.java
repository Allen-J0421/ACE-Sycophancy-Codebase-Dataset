interface SearchStrategy<T extends Comparable<? super T>> {
    int search(T[] array, T target);
}

class BinarySearchStrategy<T extends Comparable<? super T>> implements SearchStrategy<T> {
    @Override
    public int search(T[] array, T target) {
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
}

class BinarySearch {
    public static void main(String[] args) {
        Integer[] numbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        SearchStrategy<Integer> searchStrategy = new BinarySearchStrategy<>();
        int result = searchStrategy.search(numbers, target);

        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
