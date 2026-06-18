public final class TwoPointersDemo {

    private TwoPointersDemo() {
    }

    public static void main(String[] args) {
        SortedIntArray sortedValues = SortedIntArray.copyOf(new int[] {-3, -1, 0, 1, 2});
        long target = -2;

        System.out.println(TwoPointers.findPairWithSum(sortedValues, target));
    }
}
