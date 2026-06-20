import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectedGraph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    public DirectedGraph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>(vertexCount + 1);
        for (int i = 0; i <= vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int from, int to) {
        adjacencyList.get(from).add(to);
    }

    public List<Integer> neighbors(int vertex) {
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public DirectedGraph reverse() {
        DirectedGraph reversed = new DirectedGraph(vertexCount);
        for (int u = 1; u <= vertexCount; u++) {
            for (int v : neighbors(u)) {
                reversed.addEdge(v, u);
            }
        }
        return reversed;
    }
}
