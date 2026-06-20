import java.util.Arrays;

public final class CountingSortDemo {

    private CountingSortDemo() {
        // Demo utility class.
    }

    public static void main(String[] args) {
        int[] values = {2, 5, 3, 0, 2, 3, 0, 3};
        System.out.println(Arrays.toString(CountingSort.sort(values)));
    }
}
