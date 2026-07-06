package countingsort;

public class CountingSort {

    private static final SortStateFactory DEFAULT_FACTORY = CountingSortState::new;

    private CountingSort() {}

    public static int[] sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (arr.length == 0) {
            return new int[0];
        }
        SortState state = DEFAULT_FACTORY.create(arr);
        state.accumulateCounts();
        return state.buildSorted();
    }
}
