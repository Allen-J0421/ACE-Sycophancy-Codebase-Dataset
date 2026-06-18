public class GraphService {
    private final GraphQuery query;
    private final TraversalCache cache;
    private final TraversalEventBus eventBus;
    private final Graph graph;
    private TraversalProfile currentProfile;

    public GraphService(Graph graph) {
        this.graph = graph;
        this.query = new GraphQuery(graph);
        this.cache = query.getCache();
        this.eventBus = query.getEventBus();
        this.currentProfile = new TraversalProfile(TraversalProfile.Profile.PERFORMANCE);
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

        if (currentProfile.getProfile().isValidationEnabled()) {
            ResultValidator validator = new ResultValidator(result, graph);
            currentProfile.setValidationReport(validator.validate());
        }

        currentProfile.setMetrics(result.getPerformanceMetrics());

        return result;
    }

    public TraversalResult traverseWithProfile(TraversalProfile.Profile profile) {
        this.currentProfile = new TraversalProfile(profile);
        TraversalConfig config = profile.toConfig();
        return traverse(config);
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

    public GraphOperations operations() {
        return new GraphOperations(graph);
    }

    public void clearCache() {
        cache.clear();
    }

    public TraversalProfile getCurrentProfile() {
        return currentProfile;
    }
}
