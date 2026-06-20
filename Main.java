import java.util.Arrays;

/** Small demo for {@link BucketSort}. */
public class Main {

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        BucketSort.sort(arr);

        System.out.println("Sorted array is:");
        System.out.println(Arrays.toString(arr));
    }
}
