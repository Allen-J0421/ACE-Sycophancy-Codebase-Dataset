final class QuickSelectEngine {

    private final int[] values;
    private final int targetIndex;
    private final Partitioner partitioner;
    private final SearchBounds searchBounds;

    QuickSelectEngine(int[] values, int targetIndex, PivotSelector pivotSelector) {
        this.values = values;
        this.targetIndex = targetIndex;
        this.partitioner = new Partitioner(values, pivotSelector);
        this.searchBounds = SearchBounds.initial(values.length);
    }

    static int select(SelectionRequest request, PivotSelector pivotSelector) {
        return new QuickSelectEngine(request.workingValues(), request.targetIndex(), pivotSelector)
            .select();
    }

    int select() {
        while (searchBounds.isActive()) {
            int partitionIndex = partitioner.partition(searchBounds);

            if (partitionIndex == targetIndex) {
                return values[partitionIndex];
            }

            searchBounds.narrowToward(targetIndex, partitionIndex);
        }

        throw new IllegalStateException("Quickselect failed to locate the target index");
    }
}
