package graph.cycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectedGraph {
    private final int vertices;
    private final List<List<Integer>> adj;

    private DirectedGraph(int vertices, List<List<Integer>> adj) {
        this.vertices = vertices;
        List<List<Integer>> frozen = new ArrayList<>(vertices);
        for (List<Integer> neighbors : adj) {
            frozen.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        this.adj = Collections.unmodifiableList(frozen);
    }

    public int size() { return vertices; }

    public Iterable<Integer> neighbors(int v) { return adj.get(v); }

    public static Builder builder(int vertices) {
        return new Builder(vertices);
    }

    public static class Builder {
        private final int vertices;
        private final List<List<Integer>> adj;

        private Builder(int vertices) {
            this.vertices = vertices;
            adj = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) {
                adj.add(new ArrayList<>());
            }
        }

        public Builder addEdge(int u, int v) {
            adj.get(u).add(v);
            return this;
        }

        public DirectedGraph build() {
            return new DirectedGraph(vertices, adj);
        }
    }
}
