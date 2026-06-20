import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class KosarajuStronglyConnectedComponentsFinder
    implements StronglyConnectedComponentsFinder {
    @Override
    public StronglyConnectedComponentsResult findComponents(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }

        GraphTraversal forwardTraversal = GraphTraversal.forGraph(graph);
        Deque<Integer> finishingOrder = forwardTraversal.buildFinishingOrder();

        GraphTraversal reversedTraversal = GraphTraversal.forGraph(graph.reverse());
        List<StronglyConnectedComponent> components = new ArrayList<>();

        while (!finishingOrder.isEmpty()) {
            int vertex = finishingOrder.pop();
            if (reversedTraversal.hasVisited(vertex)) {
                continue;
            }

            components.add(
                StronglyConnectedComponent.fromVertices(
                    reversedTraversal.collectReachableVerticesFrom(vertex)
                )
            );
        }

        return new StronglyConnectedComponentsResult(components);
    }
}
