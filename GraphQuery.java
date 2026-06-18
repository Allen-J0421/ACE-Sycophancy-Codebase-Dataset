import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphQuery {
    private final Graph graph;
    private final TraversalCache cache;
    private final TraversalEventBus eventBus;

    public GraphQuery(Graph graph) {
        this.graph = graph;
        this.cache = new TraversalCache();
        this.eventBus = new TraversalEventBus();
    }

    public TraversalQuery traverse() {
        return new TraversalQuery(this);
    }

    public ConnectivityQuery connectivity() {
        return new ConnectivityQuery(graph);
    }

    public DegreeQuery degree() {
        return new DegreeQuery(graph);
    }

    public VertexQuery vertices() {
        return new VertexQuery(graph);
    }

    public Graph getGraph() {
        return graph;
    }

    public TraversalCache getCache() {
        return cache;
    }

    public TraversalEventBus getEventBus() {
        return eventBus;
    }

    public static class DegreeQuery {
        private final Graph graph;

        public DegreeQuery(Graph graph) {
            this.graph = graph;
        }

        public int getDegree(int vertex) {
            return graph.getAdjacent(vertex).size();
        }

        public List<Integer> getVerticesWithDegree(int degree) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < graph.getVertexCount(); i++) {
                if (getDegree(i) == degree) {
                    result.add(i);
                }
            }
            return result;
        }

        public List<Integer> getHighestDegreeVertices(int count) {
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < graph.getVertexCount(); i++) {
                vertices.add(i);
            }
            vertices.sort((a, b) -> Integer.compare(getDegree(b), getDegree(a)));
            return vertices.subList(0, Math.min(count, vertices.size()));
        }

        public int getMaxDegree() {
            int maxDegree = 0;
            for (int i = 0; i < graph.getVertexCount(); i++) {
                maxDegree = Math.max(maxDegree, getDegree(i));
            }
            return maxDegree;
        }

        public int getMinDegree() {
            int minDegree = Integer.MAX_VALUE;
            for (int i = 0; i < graph.getVertexCount(); i++) {
                minDegree = Math.min(minDegree, getDegree(i));
            }
            return minDegree == Integer.MAX_VALUE ? 0 : minDegree;
        }

        public double getAverageDegree() {
            int totalDegree = 0;
            for (int i = 0; i < graph.getVertexCount(); i++) {
                totalDegree += getDegree(i);
            }
            return graph.getVertexCount() == 0 ? 0 : (double) totalDegree / graph.getVertexCount();
        }
    }

    public static class TraversalQuery {
        private final GraphQuery graphQuery;
        private GraphTraversal strategy;
        private boolean useCache = true;

        public TraversalQuery(GraphQuery graphQuery) {
            this.graphQuery = graphQuery;
            this.strategy = new BreadthFirstSearch();
        }

        public TraversalQuery usingBFS() {
            this.strategy = new BreadthFirstSearch();
            return this;
        }

        public TraversalQuery usingDFS() {
            this.strategy = new DepthFirstSearch();
            return this;
        }

        public TraversalQuery withoutCache() {
            this.useCache = false;
            return this;
        }

        public TraversalResult execute() {
            String cacheKey = graphQuery.cache.generateKey(graphQuery.graph, strategy);

            if (useCache) {
                TraversalResult cached = graphQuery.cache.get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }

            graphQuery.eventBus.publish(TraversalEvent.traversalStarted());
            TraversalResult result = strategy.traverse(graphQuery.graph);
            graphQuery.eventBus.publish(TraversalEvent.traversalCompleted());

            if (useCache) {
                graphQuery.cache.put(cacheKey, result);
            }

            return result;
        }
    }

    public static class VertexQuery {
        private final Graph graph;

        public VertexQuery(Graph graph) {
            this.graph = graph;
        }

        public List<Integer> allVertices() {
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < graph.getVertexCount(); i++) {
                vertices.add(i);
            }
            return vertices;
        }

        public List<Integer> withDegree(int minDegree) {
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < graph.getVertexCount(); i++) {
                if (graph.getAdjacent(i).size() >= minDegree) {
                    vertices.add(i);
                }
            }
            return vertices;
        }

        public List<Integer> leafVertices() {
            return withDegree(0);
        }

        public boolean hasVertex(int vertex) {
            return graph.isValidVertex(vertex);
        }
    }
}
