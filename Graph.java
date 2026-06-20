import java.util.List;
import java.util.Objects;

final class Graph {
    private final GraphType type;
    private final List<List<Integer>> adjacencyList;

    private Graph(GraphType type, List<List<Integer>> adjacencyList) {
        this.type = Objects.requireNonNull(type, "type");
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

    static Graph create(GraphType type, List<List<Integer>> adjacencyList) {
        return new Graph(type, adjacencyList);
    }

    int vertexCount() {
        return adjacencyList.size();
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

    List<Integer> neighborsOf(int vertex) {
        GraphValidation.requireVertex(vertex, vertexCount(), "vertex");
        return adjacencyList.get(vertex);
    }

    <Result> Result analyze(GraphAnalysis<Result> analysis) {
        Objects.requireNonNull(analysis, "analysis");
        return analysis.analyze(this);
    }
}
