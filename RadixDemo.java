public final class RadixDemo {
    private static final int[] DEFAULT_VALUES = {170, 45, 75, 90, 802, 24, 2, 66};

    private RadixDemo() {
    }

    public static void main(String[] args) {
        int[] values = args.length == 0 ? DEFAULT_VALUES.clone() : parseValues(args);

        Radix.radixSort(values);
        Radix.print(values, values.length);
    }

    static int[] parseValues(String[] args) {
        int[] values = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = Integer.parseInt(args[i]);
        }
        return values;
    }
}
