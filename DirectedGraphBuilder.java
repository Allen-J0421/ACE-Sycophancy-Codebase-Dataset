import java.util.ArrayList;
import java.util.List;

public class DirectedGraphBuilder {
    private int vertexCount;
    private final List<Edge> edges = new ArrayList<>();

    public DirectedGraphBuilder withVertexCount(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("vertexCount must be positive, got: " + vertexCount);
        }
        this.vertexCount = vertexCount;
        return this;
    }

    public DirectedGraphBuilder addEdge(int from, int to) {
        edges.add(new Edge(from, to));
        return this;
    }

    public DirectedGraph build() {
        if (vertexCount <= 0) {
            throw new IllegalStateException("withVertexCount must be called before build()");
        }
        List<List<Integer>> adjList = new ArrayList<>(vertexCount + 1);
        for (int i = 0; i <= vertexCount; i++) {
            adjList.add(new ArrayList<>());
        }
        for (Edge edge : edges) {
            int from = edge.getSource();
            int to = edge.getDestination();
            if (from < 1 || from > vertexCount || to < 1 || to > vertexCount) {
                throw new IllegalArgumentException(
                        "Edge " + edge + " references vertex outside valid range [1, " + vertexCount + "]");
            }
            adjList.get(from).add(to);
        }
        return new DirectedGraph(vertexCount, adjList);
    }
}
