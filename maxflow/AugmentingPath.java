package maxflow;

import java.util.Arrays;

public final class AugmentingPath {
    private final int source;
    private final int sink;
    private final int[] parents;

    AugmentingPath(int source, int sink, int[] parents) {
        this.source = source;
        this.sink = sink;
        this.parents = Arrays.copyOf(parents, parents.length);
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }

    int parentOf(int vertex) {
        return parents[vertex];
    }
}
