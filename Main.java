public final class Main {

    private Main() {
        // Utility class.
    }

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        float[] sorted = BucketSort.sortedCopy(arr);

        System.out.println("Original array is:");
        for (float num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.println("Sorted array is:");
        for (float num : sorted) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
