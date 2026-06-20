import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class KosarajuStronglyConnectedComponentsFinder
    implements StronglyConnectedComponentsFinder {
    private final DepthFirstTraversal traversal = new DepthFirstTraversal();

    @Override
    public StronglyConnectedComponentsResult findComponents(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        Deque<Integer> finishingOrder = traversal.buildFinishingOrder(graph);

        Graph reversedGraph = graph.reverse();
        boolean[] visited = new boolean[graph.vertexCount()];
        List<StronglyConnectedComponent> components = new ArrayList<>();

        while (!finishingOrder.isEmpty()) {
            int vertex = finishingOrder.pop();
            if (visited[vertex]) {
                continue;
            }

            components.add(
                StronglyConnectedComponent.fromVertices(
                    traversal.collectReachableVertices(vertex, reversedGraph, visited)
                )
            );
        }

        return new StronglyConnectedComponentsResult(components);
    }
}
