final class QuickSelectEngine {

    private final SelectionContext context;
    private final Partitioner partitioner;

    QuickSelectEngine(SelectionContext context, PivotSelector pivotSelector) {
        this.context = context;
        this.partitioner = new Partitioner(context, pivotSelector);
    }

    static int select(SelectionContext context, PivotSelector pivotSelector) {
        return new QuickSelectEngine(context, pivotSelector).select();
    }

    int select() {
        while (context.searchBounds().isActive()) {
            int partitionIndex = partitioner.partition();

            if (context.isTargetIndex(partitionIndex)) {
                return context.valueAt(partitionIndex);
            }

            context.narrowSearchBounds(partitionIndex);
        }

        throw new IllegalStateException("Quickselect failed to locate the target index");
    }
}
