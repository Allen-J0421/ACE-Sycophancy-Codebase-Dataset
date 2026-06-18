final class MergeSortDemo {

    private MergeSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        MergeSort.sort(values);
        printValues(values);
    }

    private static void printValues(int[] values) {
        System.out.println(formatValues(values));
    }

    private static String formatValues(int[] values) {
        if (values.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(values[0]);

        for (int i = 1; i < values.length; i++) {
            result.append(' ').append(values[i]);
        }

        return result.toString();
    }
}
