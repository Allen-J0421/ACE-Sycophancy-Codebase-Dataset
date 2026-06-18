import java.util.List;

public class DepthFirstSearch extends AbstractTraversal {

    @Override
    protected void traverseComponent(Graph graph, int startVertex, boolean[] visited, List<Integer> result, int componentId) {
        dfsRecursive(graph, startVertex, visited, result, componentId);
    }

    private void dfsRecursive(Graph graph, int vertex, boolean[] visited, List<Integer> result, int componentId) {
        visited[vertex] = true;
        result.add(vertex);
        publishEvent(TraversalEvent.vertexVisited(vertex, componentId));

        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfsRecursive(graph, neighbor, visited, result, componentId);
            }
        }
    }
}
