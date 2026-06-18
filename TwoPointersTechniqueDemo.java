public final class TwoPointersTechniqueDemo {

    private TwoPointersTechniqueDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        int[] unsortedValues = {2, -3, 1, 0, -1};
        int[] sortedValues = {-3, -1, 0, 1, 2};
        int target = -2;

        System.out.println(TwoPointersTechnique.hasPairWithSum(unsortedValues, target));
        System.out.println(TwoPointersTechnique.hasPairWithSumSorted(sortedValues, target));
    }
}
