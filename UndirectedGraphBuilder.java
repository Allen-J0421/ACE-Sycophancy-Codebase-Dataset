import java.util.ArrayList;
import java.util.List;

class UndirectedGraphBuilder {
    private final int V;
    private final List<int[]> edges = new ArrayList<>();

    UndirectedGraphBuilder(int V) {
        this.V = V;
    }

    UndirectedGraphBuilder addEdge(int u, int v) {
        edges.add(new int[]{u, v});
        return this;
    }

    UndirectedGraph build() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return new UndirectedGraph(adj);
    }
}
