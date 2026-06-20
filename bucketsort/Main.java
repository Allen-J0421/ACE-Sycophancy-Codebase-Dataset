package bucketsort;

public final class Main {

    private Main() {
        // Utility class.
    }

    public static void main(String[] args) {
        float[] values = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        float[] sorted = BucketSort.sortedCopy(values, 4);

        System.out.println("Original array is:");
        for (float value : values) {
            System.out.print(value + " ");
        }
        System.out.println();

        System.out.println("Sorted array is:");
        for (float value : sorted) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
