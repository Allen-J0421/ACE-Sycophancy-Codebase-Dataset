public final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        final int[] numbers = {2, 3, 4, 10, 40};
        final int target = 10;
        final int result = BinarySearch.indexOf(numbers, target);

        System.out.println(SearchResultFormatter.format(result));
    }
}
