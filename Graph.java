import java.util.ArrayList;
import java.util.List;

class Graph implements GraphView {
    private final int n;
    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int n) {
        this.n = n;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    @Override
    public List<Integer> getNeighbors(int u) {
        return adj.get(u);
    }

    @Override
    public int size() {
        return n;
    }
}
