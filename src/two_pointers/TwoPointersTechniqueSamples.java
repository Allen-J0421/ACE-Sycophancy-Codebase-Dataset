package two_pointers;

final class TwoPointersTechniqueSamples {

    private static final int[] UNSORTED_SAMPLE = {2, -3, 1, 0, -1};
    private static final int[] SORTED_SAMPLE = {-3, -1, 0, 1, 2};
    private static final int TARGET = -2;

    private TwoPointersTechniqueSamples() {
        // Shared sample data only.
    }

    static int[] unsortedSample() {
        return UNSORTED_SAMPLE.clone();
    }

    static int[] sortedSample() {
        return SORTED_SAMPLE.clone();
    }

    static int target() {
        return TARGET;
    }
}
