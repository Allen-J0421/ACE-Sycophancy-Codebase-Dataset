final class BinarySearchDemo {
    private static final int[] SAMPLE_VALUES = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    private static String describeSearchResult(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        int result = BinarySearch.indexOf(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(describeSearchResult(result));
    }
}
