import java.util.ArrayList;
import java.util.List;

class Graph implements GraphView {
    private final int capacity;
    private final List<List<Integer>> adj;

    Graph(int capacity) {
        this.capacity = capacity;
        adj = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
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
        return capacity;
    }
}
