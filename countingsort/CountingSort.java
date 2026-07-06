package countingsort;

public class CountingSort {

    private CountingSort() {}

    public static int[] sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (arr.length == 0) {
            return new int[0];
        }
        SortState state = new CountingSortState(arr);
        state.accumulateCounts();
        return state.buildSorted();
    }
}
