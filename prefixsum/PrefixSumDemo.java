package prefixsum;

public final class PrefixSumDemo {

    private static final int[] SAMPLE_VALUES = {10, 20, 10, 5, 15};

    private PrefixSumDemo() {
    }

    public static void main(String[] args) {
        PrefixSumArray prefixSums = PrefixSumArray.from(SAMPLE_VALUES);
        System.out.println(prefixSums);
    }
}
