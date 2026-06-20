public final class AugmentingPath {
    private final int source;
    private final int sink;
    private final int bottleneck;
    private final int[] parents;

    public AugmentingPath(int source, int sink, int bottleneck, int[] parents) {
        this.source = source;
        this.sink = sink;
        this.bottleneck = bottleneck;
        this.parents = parents.clone();
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }

    public int bottleneck() {
        return bottleneck;
    }

    public int parentOf(int vertex) {
        return parents[vertex];
    }
}
