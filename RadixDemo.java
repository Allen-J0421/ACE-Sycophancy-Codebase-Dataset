public final class RadixDemo {
    private RadixDemo() {
    }

    public static void main(String[] args) {
        int[] values = {170, 45, 75, 90, 802, 24, 2, 66};

        Radix.radixSort(values);
        Radix.print(values, values.length);
    }
}
