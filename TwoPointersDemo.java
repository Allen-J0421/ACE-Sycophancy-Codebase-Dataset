public final class TwoPointersDemo {

    private TwoPointersDemo() {
    }

    public static void main(String[] args) {
        int[] sortedValues = {-3, -1, 0, 1, 2};
        int target = -2;

        System.out.println(TwoPointers.findPairWithSum(sortedValues, target));
    }
}
