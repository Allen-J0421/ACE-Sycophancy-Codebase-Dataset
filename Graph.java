import java.util.List;
import java.util.Objects;

final class Graph {
    private final GraphType type;
    private final List<Edge> edges;
    private final List<List<Integer>> adjacencyList;

    private Graph(GraphType type, List<Edge> edges, List<List<Integer>> adjacencyList) {
        this.type = Objects.requireNonNull(type, "type");
        this.edges = GraphValidation.freezeEdges(Objects.requireNonNull(edges, "edges"));
        this.adjacencyList = GraphValidation.freezeAdjacencyList(
            Objects.requireNonNull(adjacencyList, "adjacencyList")
        );
    }

    static Graph fromEdges(int vertexCount, int[][] edges) {
        return GraphBuilder.undirected(vertexCount).addEdges(edges).build();
    }

    static GraphBuilder builder(int vertexCount, GraphType type) {
        return new GraphBuilder(vertexCount, type);
    }

    static Graph create(GraphType type, List<Edge> edges, List<List<Integer>> adjacencyList) {
        return new Graph(type, edges, adjacencyList);
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    int edgeCount() {
        return edges.size();
    }

    GraphType type() {
        return type;
    }

    boolean isDirected() {
        return type == GraphType.DIRECTED;
    }

    boolean isUndirected() {
        return type == GraphType.UNDIRECTED;
    }

    List<Edge> edges() {
        return edges;
    }

    List<Integer> neighborsOf(int vertex) {
        GraphValidation.requireVertex(vertex, vertexCount(), "vertex");
        return adjacencyList.get(vertex);
    }

    GraphBuilder copyBuilder() {
        return builder(vertexCount(), type).addEdges(edges);
    }

    <Result> Result analyze(GraphAnalysis<Result> analysis) {
        Objects.requireNonNull(analysis, "analysis");
        return analysis.analyze(this);
    }
}
