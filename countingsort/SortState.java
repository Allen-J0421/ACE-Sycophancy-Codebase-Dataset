package countingsort;

interface SortState {
    void accumulateCounts();
    int[] buildSorted();
}
