import java.util.Arrays;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        float[] values = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        BucketSort.sort(values);
        System.out.println("Sorted array is:");
        System.out.println(Arrays.toString(values));
    }
}
