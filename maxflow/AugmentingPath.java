package maxflow;

import java.util.Arrays;

public final class AugmentingPath {
    private final int source;
    private final int sink;
    private final int[] vertices;
    private final int flow;

    AugmentingPath(int source, int sink, int[] vertices, int flow) {
        if (vertices == null || vertices.length < 2) {
            throw new IllegalArgumentException("path must contain at least two vertices");
        }
        if (flow <= 0) {
            throw new IllegalArgumentException("flow must be positive");
        }
        if (vertices[0] != source) {
            throw new IllegalArgumentException("path must start at the source vertex");
        }
        if (vertices[vertices.length - 1] != sink) {
            throw new IllegalArgumentException("path must end at the sink vertex");
        }

        this.source = source;
        this.sink = sink;
        this.vertices = Arrays.copyOf(vertices, vertices.length);
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

    public int vertexCount() {
        return vertices.length;
    }

    public int vertexAt(int index) {
        return vertices[index];
    }
}
