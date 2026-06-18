import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static DirectedGraphBuilder builder(int vertexCount) {
        return new DirectedGraphBuilder(vertexCount);
    }

    public static DirectedGraph withVertexCount(int vertexCount) {
        return builder(vertexCount).build();
    }

    public static DirectedGraph fromEdges(int vertexCount, Iterable<DirectedEdge> edges) {
        DirectedGraphBuilder builder = builder(vertexCount);
        for (DirectedEdge edge : edges) {
            GraphValidation.requireEdge(edge);
            builder.addEdge(edge);
        }
        return builder.build();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        GraphValidation.requireVertex(vertex, adjacencyList.size());
        return adjacencyList.get(vertex);
    }

    static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
        GraphValidation.requireVertexCount(vertexCount);

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
        return new DirectedGraph(freezeAdjacencyList(adjacencyList));
    }

    private static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }
        return Collections.unmodifiableList(frozenAdjacencyList);
    }
}
