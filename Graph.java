import java.util.ArrayList;
import java.util.List;

class Graph {
    private final ArrayList<ArrayList<Edge>> adj;

    Graph(int vertices) {
        adj = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }

    List<Edge> neighbors(int u) {
        return adj.get(u);
    }

    int size() {
        return adj.size();
    }
}
