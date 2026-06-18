public final class PrefixSumDemo {

    private PrefixSumDemo() {
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        int[] prefixSums = PrefixSum.build(values);
        System.out.println(joinWithSpaces(prefixSums));
    }

    private static String joinWithSpaces(int[] values) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                output.append(' ');
            }
            output.append(values[i]);
        }

        return output.toString();
    }
}
