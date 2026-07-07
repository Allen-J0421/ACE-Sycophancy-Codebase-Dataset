package graph.cycle;

import java.util.ArrayList;
import java.util.List;

public class DirectedGraph {
    private final int vertices;
    private final List<List<Integer>> adj;

    public DirectedGraph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    public int size() { return vertices; }

    public Iterable<Integer> neighbors(int v) { return adj.get(v); }
}
