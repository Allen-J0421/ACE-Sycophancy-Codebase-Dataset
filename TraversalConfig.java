import java.util.ArrayList;
import java.util.List;

public class TraversalConfig {
    private GraphTraversal strategy;
    private boolean enableCache;
    private boolean enableEvents;
    private List<TraversalEventListener> eventListeners;
    private List<VertexVisitor> vertexVisitors;

    private TraversalConfig() {
        this.strategy = new BreadthFirstSearch();
        this.enableCache = true;
        this.enableEvents = false;
        this.eventListeners = new ArrayList<>();
        this.vertexVisitors = new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public GraphTraversal getStrategy() {
        return strategy;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public boolean isEnableEvents() {
        return enableEvents;
    }

    public List<TraversalEventListener> getEventListeners() {
        return new ArrayList<>(eventListeners);
    }

    public List<VertexVisitor> getVertexVisitors() {
        return new ArrayList<>(vertexVisitors);
    }

    public static class Builder {
        private final TraversalConfig config = new TraversalConfig();

        public Builder withStrategy(GraphTraversal strategy) {
            config.strategy = strategy;
            return this;
        }

        public Builder withBFS() {
            config.strategy = new BreadthFirstSearch();
            return this;
        }

        public Builder withDFS() {
            config.strategy = new DepthFirstSearch();
            return this;
        }

        public Builder withCache(boolean enable) {
            config.enableCache = enable;
            return this;
        }

        public Builder withEvents(boolean enable) {
            config.enableEvents = enable;
            return this;
        }

        public Builder addEventListener(TraversalEventListener listener) {
            config.eventListeners.add(listener);
            return this;
        }

        public Builder addVertexVisitor(VertexVisitor visitor) {
            config.vertexVisitors.add(visitor);
            return this;
        }

        public TraversalConfig build() {
            return config;
        }
    }
}
