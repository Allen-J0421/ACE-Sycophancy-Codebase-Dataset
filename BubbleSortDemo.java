public final class BubbleSortDemo {
    private BubbleSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = {64, 34, 25, 12, 22, 11, 90};

        BubbleSort.sort(values);

        System.out.println("Sorted array: ");
        BubbleSort.printArray(values);
    }
}
