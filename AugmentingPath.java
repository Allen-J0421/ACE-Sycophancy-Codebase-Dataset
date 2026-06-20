public final class AugmentingPath {
    private final Vertex source;
    private final Vertex sink;
    private final int bottleneck;
    private final int[] parents;

    public AugmentingPath(Vertex source, Vertex sink, int bottleneck, int[] parents) {
        this.source = source;
        this.sink = sink;
        this.bottleneck = bottleneck;
        this.parents = parents.clone();
    }

    public Vertex source() {
        return source;
    }

    public Vertex sink() {
        return sink;
    }

    public int bottleneck() {
        return bottleneck;
    }

    public Vertex parentOf(Vertex vertex) {
        return new Vertex(parents[vertex.index()]);
    }
}
