import java.util.ArrayList;
import java.util.List;

final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    private DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    static DirectedGraph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return new DirectedGraph(adjacencyList);
    }

    static DirectedGraph fromEdges(int vertexCount, int[][] edges) {
        DirectedGraph graph = withVertexCount(vertexCount);
        for (int[] edge : edges) {
            validateEdgeShape(edge);
            graph.addEdge(edge[0], edge[1]);
        }
        return graph;
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    int[] buildIndegreeTable() {
        int[] indegree = new int[vertexCount()];
        for (int vertex = 0; vertex < vertexCount(); vertex++) {
            for (int next : neighborsOf(vertex)) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    void addEdge(int source, int destination) {
        validateVertex(source);
        validateVertex(destination);
        adjacencyList.get(source).add(destination);
    }

    private static void validateEdgeShape(int[] edge) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("Each edge must contain exactly two vertices.");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of bounds: " + vertex);
        }
    }
}
