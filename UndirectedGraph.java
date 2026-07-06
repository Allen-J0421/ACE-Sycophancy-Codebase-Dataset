import java.util.Collections;
import java.util.List;

class UndirectedGraph implements Graph {
    private final List<List<Integer>> adj;

    UndirectedGraph(List<List<Integer>> adj) {
        this.adj = adj;
    }

    @Override
    public int vertexCount() {
        return adj.size();
    }

    @Override
    public List<Integer> neighbors(int v) {
        return Collections.unmodifiableList(adj.get(v));
    }
}
