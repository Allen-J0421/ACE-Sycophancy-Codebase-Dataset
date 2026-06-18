import java.util.ArrayList;
import java.util.List;

public final class Graph implements IntGraph {
    private final List<List<Integer>> adjacencyList;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Graph withVertices(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return new Graph(adjacencyList);
    }

    public void addUndirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public int neighborCount(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex).size();
    }

    @Override
    public int neighborAt(int vertex, int neighborIndex) {
        validateVertex(vertex);

        List<Integer> neighbors = adjacencyList.get(vertex);
        if (neighborIndex < 0 || neighborIndex >= neighbors.size()) {
            throw new IllegalArgumentException("neighbor index out of bounds: " + neighborIndex);
        }

        return neighbors.get(neighborIndex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }
}
