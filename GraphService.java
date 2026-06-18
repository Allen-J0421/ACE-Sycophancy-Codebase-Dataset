public class GraphService {
    private final GraphQuery query;
    private final TraversalCache cache;
    private final TraversalEventBus eventBus;

    public GraphService(Graph graph) {
        this.query = new GraphQuery(graph);
        this.cache = query.getCache();
        this.eventBus = query.getEventBus();
    }

    public TraversalResult traverse(TraversalConfig config) {
        GraphTraversal strategy = config.getStrategy();

        if (strategy instanceof AbstractTraversal) {
            AbstractTraversal abstractStrategy = (AbstractTraversal) strategy;
            if (config.isEnableEvents()) {
                setupEventListeners(config);
                abstractStrategy.setEventBus(eventBus);
            }
        }

        String cacheKey = cache.generateKey(query.getGraph(), strategy);

        if (config.isEnableCache()) {
            TraversalResult cached = cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        TraversalResult result = strategy.traverse(query.getGraph());

        for (VertexVisitor visitor : config.getVertexVisitors()) {
            for (int vertex : result.getVertices()) {
                visitor.visit(vertex);
            }
        }

        if (config.isEnableCache()) {
            cache.put(cacheKey, result);
        }

        return result;
    }

    private void setupEventListeners(TraversalConfig config) {
        eventBus.clear();
        for (TraversalEventListener listener : config.getEventListeners()) {
            eventBus.subscribe(listener);
        }
    }

    public GraphQuery getQuery() {
        return query;
    }

    public ConnectivityQuery connectivity() {
        return query.connectivity();
    }

    public GraphQuery.DegreeQuery degree() {
        return query.degree();
    }

    public void clearCache() {
        cache.clear();
    }
}
