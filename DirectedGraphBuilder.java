import java.util.ArrayList;
import java.util.List;

public class DirectedGraphBuilder {
    private int vertexCount;
    private final List<Edge> edges = new ArrayList<>();

    public DirectedGraphBuilder withVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
        return this;
    }

    public DirectedGraphBuilder addEdge(int from, int to) {
        edges.add(new Edge(from, to));
        return this;
    }

    public DirectedGraph build() {
        DirectedGraph graph = new DirectedGraph(vertexCount);
        for (Edge edge : edges) {
            graph.addEdge(edge.getSource(), edge.getDestination());
        }
        return graph;
    }
}
