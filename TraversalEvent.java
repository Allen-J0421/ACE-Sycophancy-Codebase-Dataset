public class TraversalEvent {
    public enum EventType {
        TRAVERSAL_STARTED,
        VERTEX_VISITED,
        COMPONENT_DISCOVERED,
        TRAVERSAL_COMPLETED
    }

    private final EventType type;
    private final int vertex;
    private final long timestamp;
    private final int componentId;

    private TraversalEvent(EventType type, int vertex, int componentId) {
        this.type = type;
        this.vertex = vertex;
        this.componentId = componentId;
        this.timestamp = System.currentTimeMillis();
    }

    public static TraversalEvent vertexVisited(int vertex, int componentId) {
        return new TraversalEvent(EventType.VERTEX_VISITED, vertex, componentId);
    }

    public static TraversalEvent componentDiscovered(int componentId) {
        return new TraversalEvent(EventType.COMPONENT_DISCOVERED, -1, componentId);
    }

    public static TraversalEvent traversalStarted() {
        return new TraversalEvent(EventType.TRAVERSAL_STARTED, -1, -1);
    }

    public static TraversalEvent traversalCompleted() {
        return new TraversalEvent(EventType.TRAVERSAL_COMPLETED, -1, -1);
    }

    public EventType getType() {
        return type;
    }

    public int getVertex() {
        return vertex;
    }

    public int getComponentId() {
        return componentId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
