import java.util.ArrayList;
import java.util.List;

final class KahnTopologicalSorter implements TopologicalSorter {
    @Override
    public TopologicalOrder sort(DirectedGraph graph) {
        TraversalState traversal = TraversalState.from(graph);

        while (traversal.hasReadyVertices()) {
            int vertex = traversal.removeNextReadyVertex();
            traversal.record(vertex);
            for (int next : graph.neighborsOf(vertex)) {
                traversal.consumeIncomingEdge(next);
            }
        }

        if (!traversal.isCompleteFor(graph)) {
            throw new IllegalStateException("Topological sort requires a directed acyclic graph.");
        }

        return new TopologicalOrder(traversal.order());
    }
}
