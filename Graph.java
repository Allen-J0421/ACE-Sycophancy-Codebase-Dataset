import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private final int vertexCount;
    private final List<Set<Integer>> adjacencyList;

    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        this.vertexCount = vertexCount;
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new LinkedHashSet<>());
        }
    }

    public void addEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        if (u == v) {
            throw new IllegalArgumentException("Self-loops are not supported");
        }
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public Set<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableSet(adjacencyList.get(vertex));
    }

    public int vertexCount() {
        return vertexCount;
    }

    private void validateVertex(int vertex) {
        if (vertexCount == 0) {
            throw new IllegalArgumentException("Graph has no vertices");
        }
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex must be in range [0, " + (vertexCount - 1) + "]"
            );
        }
    }
}
