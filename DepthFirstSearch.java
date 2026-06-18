import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch {

    private final Graph graph;
    private boolean[] visited;
    private List<Integer> result;

    public DepthFirstSearch(Graph graph) {
        this.graph = graph;
    }

    public List<Integer> traverse() {
        visited = new boolean[graph.getVertexCount()];
        result = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                traverseRecursive(i);
            }
        }

        return result;
    }

    private void traverseRecursive(int vertex) {
        visited[vertex] = true;
        result.add(vertex);

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                traverseRecursive(neighbor);
            }
        }
    }
}
