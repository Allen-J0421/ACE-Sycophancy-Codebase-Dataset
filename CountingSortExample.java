import java.util.Arrays;

public final class CountingSortExample {

    private CountingSortExample() {
    }

    public static void main(String[] args) {
        int[] values = {2, 5, 3, 0, 2, 3, 0, 3, -4, -1};
        int[] sortedValues = CountingSort.sortedCopy(values);
        System.out.println(Arrays.toString(sortedValues));
    }
}
