import java.util.ArrayList;
import java.util.List;

public class DirectedGraph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        List<List<Integer>> sealed = new ArrayList<>(vertexCount + 1);
        for (List<Integer> neighbors : adjacencyList) {
            sealed.add(List.copyOf(neighbors));
        }
        this.adjacencyList = List.copyOf(sealed);
    }

    public List<Integer> neighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public DirectedGraph reverse() {
        List<List<Integer>> reversed = new ArrayList<>(vertexCount + 1);
        for (int i = 0; i <= vertexCount; i++) {
            reversed.add(new ArrayList<>());
        }
        for (int u = 1; u <= vertexCount; u++) {
            for (int v : adjacencyList.get(u)) {
                reversed.get(v).add(u);
            }
        }
        return new DirectedGraph(vertexCount, reversed);
    }
}
