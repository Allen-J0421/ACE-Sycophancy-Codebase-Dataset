public final class TwoPointersTechniqueDemo {

    private static final int[] UNSORTED_SAMPLE = {2, -3, 1, 0, -1};
    private static final int[] SORTED_SAMPLE = {-3, -1, 0, 1, 2};
    private static final int TARGET = -2;

    private TwoPointersTechniqueDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        printResult("unsorted", TwoPointersTechnique.hasPairWithSum(UNSORTED_SAMPLE, TARGET));
        printResult("sorted", TwoPointersTechnique.hasPairWithSumSorted(SORTED_SAMPLE, TARGET));
    }

    private static void printResult(String label, boolean result) {
        System.out.println(label + ": " + result);
    }
}
