import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTraversal implements GraphTraversal {
    protected List<Integer> vertices;
    protected int componentCount;
    protected TraversalEventBus eventBus;
    protected PerformanceMetrics metrics;

    protected AbstractTraversal() {
        this.vertices = new ArrayList<>();
        this.componentCount = 0;
        this.eventBus = null;
        this.metrics = null;
    }

    @Override
    public TraversalResult traverse(Graph graph) {
        PerformanceMetrics localMetrics = new PerformanceMetrics();
        boolean[] visitedArray = new boolean[graph.getVertexCount()];
        List<Integer> vertexList = new ArrayList<>();
        int components = 0;

        publishEvent(TraversalEvent.traversalStarted());

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visitedArray[i]) {
                traverseComponent(graph, i, visitedArray, vertexList, components);
                publishEvent(TraversalEvent.componentDiscovered(components));
                components++;
            }
        }

        publishEvent(TraversalEvent.traversalCompleted());
        localMetrics.stop();

        TraversalResult result = new TraversalResult(vertexList, components);
        result.setPerformanceMetrics(localMetrics);
        return result;
    }

    protected void publishEvent(TraversalEvent event) {
        if (eventBus != null) {
            eventBus.publish(event);
        }
    }

    public void setEventBus(TraversalEventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected abstract void traverseComponent(Graph graph, int startVertex, boolean[] visited, List<Integer> result, int componentId);
}
