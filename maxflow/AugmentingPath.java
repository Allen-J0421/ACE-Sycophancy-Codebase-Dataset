package maxflow;

import java.util.Arrays;

public final class AugmentingPath {
    private final int source;
    private final int sink;
    private final int[] parents;
    private final int flow;

    AugmentingPath(int source, int sink, int[] parents, int flow) {
        if (flow <= 0) {
            throw new IllegalArgumentException("flow must be positive");
        }

        this.source = source;
        this.sink = sink;
        this.parents = Arrays.copyOf(parents, parents.length);
        this.flow = flow;
    }

    public int source() {
        return source;
    }

    public int sink() {
        return sink;
    }

    public int flow() {
        return flow;
    }

    int parentOf(int vertex) {
        return parents[vertex];
    }
}
