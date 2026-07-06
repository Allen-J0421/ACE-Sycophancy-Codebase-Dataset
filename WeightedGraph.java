import java.util.ArrayList;
import java.util.List;

final class WeightedGraph implements Graph {

    private final VertexIndex vertexIndex;
    private final AdjacencyStore store;

    private WeightedGraph(VertexIndex vertexIndex, AdjacencyStore store) {
        this.vertexIndex = vertexIndex;
        this.store = store;
    }

    static WeightedGraph from(int vertices, int[][] edges) {
        List<WeightedEdge> list = new ArrayList<>(edges.length);
        for (int[] e : edges) {
            list.add(new WeightedEdge(e[0], e[1], e[2]));
        }
        return create(vertices, list);
    }

    static WeightedGraph of(int vertices, WeightedEdge... edges) {
        return create(vertices, List.of(edges));
    }

    private static WeightedGraph create(int vertices, List<WeightedEdge> edges) {
        VertexIndex index = new IntVertexIndex(vertices);
        AdjacencyStore store = new AdjacencyStore(vertices, edges, index);
        return new WeightedGraph(index, store);
    }

    @Override
    public int vertices() {
        return vertexIndex.size();
    }

    int edgeCount() {
        return store.edgeCount();
    }

    @Override
    public List<? extends Edge> edges() {
        return store.allEdges();
    }
}
