public final class TwoPointersTechniqueDemo {

    private TwoPointersTechniqueDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        int[] values = {2, -3, 1, 0, -1};
        int target = -2;

        System.out.println(TwoPointersTechnique.hasPairWithSum(values, target));
    }
}
