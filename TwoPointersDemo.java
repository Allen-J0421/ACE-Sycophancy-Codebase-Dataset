public final class TwoPointersDemo {

    private TwoPointersDemo() {
    }

    public static void main(String[] args) {
        SortedIntArray sortedValues = SortedIntArray.of(-3, -1, 0, 1, 2);
        long target = -2;

        System.out.println(sortedValues.findPairWithSum(target));
    }
}
