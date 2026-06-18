import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch implements GraphTraversal {

    @Override
    public TraversalResult traverse(Graph graph) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<Integer> result = new ArrayList<>();
        int components = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsFromVertex(graph, i, visited, result);
                components++;
            }
        }

        return new TraversalResult(result, components);
    }

    private void dfsFromVertex(Graph graph, int vertex, boolean[] visited, List<Integer> result) {
        visited[vertex] = true;
        result.add(vertex);

        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfsFromVertex(graph, neighbor, visited, result);
            }
        }
    }
}
