import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Graph {
    private final List<List<Integer>> adjacencyList;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Graph fromEdges(int vertexCount, int[][] edges) {
        validateInputs(vertexCount, edges);

        List<List<Integer>> adjacencyList = initializeAdjacencyList(vertexCount);
        for (int[] edge : edges) {
            addUndirectedEdge(adjacencyList, edge[0], edge[1]);
        }

        return new Graph(toUnmodifiableAdjacencyList(adjacencyList));
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertexIndex(vertex);
        return adjacencyList.get(vertex);
    }

    private static List<List<Integer>> initializeAdjacencyList(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void addUndirectedEdge(
            List<List<Integer>> adjacencyList,
            int source,
            int destination) {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }

    private static List<List<Integer>> toUnmodifiableAdjacencyList(
            List<List<Integer>> adjacencyList) {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(neighbors));
        }

        return Collections.unmodifiableList(immutableAdjacencyList);
    }

    private static void validateInputs(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
            validateEdge(edges[edgeIndex], vertexCount, edgeIndex);
        }
    }

    private static void validateEdge(int[] edge, int vertexCount, int edgeIndex) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException(
                    "Each edge must contain exactly two vertices. Invalid edge at index "
                            + edgeIndex
                            + ".");
        }

        validateVertex(edge[0], vertexCount, edgeIndex);
        validateVertex(edge[1], vertexCount, edgeIndex);
    }

    private static void validateVertex(int vertex, int vertexCount, int edgeIndex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Edge at index "
                            + edgeIndex
                            + " references invalid vertex "
                            + vertex
                            + " for graph size "
                            + vertexCount
                            + ".");
        }
    }

    private void validateVertexIndex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IndexOutOfBoundsException(
                    "Vertex "
                            + vertex
                            + " is out of bounds for graph size "
                            + adjacencyList.size()
                            + ".");
        }
    }
}
