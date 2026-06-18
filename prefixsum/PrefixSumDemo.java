package prefixsum;

public final class PrefixSumDemo {

    private static final int[] SAMPLE_VALUES = {10, 20, 10, 5, 15};

    private PrefixSumDemo() {
    }

    public static void main(String[] args) {
        PrefixSumArray prefixSums = PrefixSumArray.from(readValues(args));
        System.out.println(prefixSums);
    }

    private static int[] readValues(String[] args) {
        if (args.length == 0) {
            return SAMPLE_VALUES;
        }

        int[] values = new int[args.length];

        for (int i = 0; i < args.length; i++) {
            values[i] = Integer.parseInt(args[i]);
        }

        return values;
    }
}
