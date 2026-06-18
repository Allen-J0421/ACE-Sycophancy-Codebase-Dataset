import java.util.List;

public class DepthFirstSearch extends AbstractTraversal {

    @Override
    protected void traverseComponent(Graph graph, int startVertex, boolean[] visited, List<Integer> result) {
        dfsRecursive(graph, startVertex, visited, result);
    }

    private void dfsRecursive(Graph graph, int vertex, boolean[] visited, List<Integer> result) {
        visited[vertex] = true;
        result.add(vertex);

        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfsRecursive(graph, neighbor, visited, result);
            }
        }
    }
}
