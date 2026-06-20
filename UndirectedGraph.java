import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UndirectedGraph implements Graph {
    private final List<List<Integer>> adjacencyList;

    private UndirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static UndirectedGraph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return new UndirectedGraph(adjacencyList);
    }

    public static UndirectedGraph fromAdjacencyList(List<? extends List<Integer>> source) {
        if (source == null) {
            throw new IllegalArgumentException("adjacencyList cannot be null");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(source.size());
        for (int vertex = 0; vertex < source.size(); vertex++) {
            adjacencyList.add(copyAndValidateNeighbors(source, vertex));
        }

        return new UndirectedGraph(adjacencyList);
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public List<Integer> neighbors(int vertex) {
        validateVertex(vertexCount(), vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public void addEdge(int firstVertex, int secondVertex) {
        validateVertex(vertexCount(), firstVertex);
        validateVertex(vertexCount(), secondVertex);

        adjacencyList.get(firstVertex).add(secondVertex);
        adjacencyList.get(secondVertex).add(firstVertex);
    }

    private static List<Integer> copyAndValidateNeighbors(List<? extends List<Integer>> source, int vertex) {
        List<Integer> neighbors = source.get(vertex);
        if (neighbors == null) {
            throw new IllegalArgumentException("adjacency list cannot be null for vertex " + vertex);
        }

        List<Integer> copiedNeighbors = new ArrayList<>(neighbors.size());
        for (Integer neighbor : neighbors) {
            validateVertex(source.size(), neighbor);
            copiedNeighbors.add(neighbor);
        }
        return copiedNeighbors;
    }

    private static void validateVertex(int vertexCount, Integer vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("vertex cannot be null");
        }
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("vertex out of range: " + vertex);
        }
    }
}
