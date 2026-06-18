package graph.analysis;

import graph.algorithm.DepthFirstSearch;
import graph.algorithm.BreadthFirstSearch;
import graph.algorithm.TraversalStats;
import graph.cache.AnalysisCache;
import graph.core.IGraph;
import graph.utility.Logger;

import java.util.List;
import java.util.Optional;

public class GraphAnalyzer {
    private final IGraph graph;
    private final Logger logger;
    private final AnalysisCache cache;
    private ConnectedComponentsAnalyzer componentsAnalyzer;

    public GraphAnalyzer(IGraph graph) {
        this(graph, new Logger.NoOpLogger());
    }

    public GraphAnalyzer(IGraph graph, Logger logger) {
        this.graph = graph;
        this.logger = logger;
        this.cache = new AnalysisCache(60000);
    }

    public AnalysisResult analyze() {
        String cacheKey = "analysis_" + graph.getVertexCount() + "_" + graph.getEdgeCount();
        AnalysisResult cached = cache.get(cacheKey, AnalysisResult.class);
        if (cached != null) {
            logger.debug("Cache hit for graph analysis");
            return cached;
        }

        AnalysisResult result = new AnalysisResult(
                graph,
                getConnectedComponentsAnalyzer(),
                logger
        );

        cache.put(cacheKey, result);
        return result;
    }

    public TraversalStats traverseWithDFS(boolean useRecursive) {
        DepthFirstSearch dfs = useRecursive ? DepthFirstSearch.recursive() : DepthFirstSearch.iterative();
        return dfs.traverseWithStats(graph);
    }

    public TraversalStats traverseWithBFS() {
        BreadthFirstSearch bfs = new BreadthFirstSearch();
        return bfs.traverseWithStats(graph);
    }

    private ConnectedComponentsAnalyzer getConnectedComponentsAnalyzer() {
        if (componentsAnalyzer == null) {
            componentsAnalyzer = new ConnectedComponentsAnalyzer(graph);
        }
        return componentsAnalyzer;
    }

    public static class AnalysisResult {
        private final IGraph graph;
        private final ConnectedComponentsAnalyzer componentsAnalyzer;
        private final Logger logger;

        AnalysisResult(IGraph graph, ConnectedComponentsAnalyzer componentsAnalyzer, Logger logger) {
            this.graph = graph;
            this.componentsAnalyzer = componentsAnalyzer;
            this.logger = logger;
        }

        public int getVertexCount() {
            return graph.getVertexCount();
        }

        public int getEdgeCount() {
            return graph.getEdgeCount();
        }

        public int getComponentCount() {
            return componentsAnalyzer.getComponentCount();
        }

        public boolean isConnected() {
            return componentsAnalyzer.isConnected();
        }

        public List<List<Integer>> getConnectedComponents() {
            return componentsAnalyzer.findConnectedComponents();
        }

        public List<Integer> traverseDFS() {
            return DepthFirstSearch.recursive().traverse(graph);
        }

        public List<Integer> traverseBFS() {
            return new BreadthFirstSearch().traverse(graph);
        }

        public String getReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔════════════════════════════════════════════════════════════╗\n");
            sb.append("║            GRAPH ANALYSIS REPORT                          ║\n");
            sb.append("╚════════════════════════════════════════════════════════════╝\n\n");

            sb.append("BASIC PROPERTIES:\n");
            sb.append("  Vertices: ").append(getVertexCount()).append("\n");
            sb.append("  Edges: ").append(getEdgeCount()).append("\n");
            sb.append("  Connected: ").append(isConnected()).append("\n");
            sb.append("  Components: ").append(getComponentCount()).append("\n\n");

            sb.append("COMPONENTS:\n");
            List<List<Integer>> components = getConnectedComponents();
            for (int i = 0; i < components.size(); i++) {
                sb.append("  Component ").append(i).append(": ").append(components.get(i)).append("\n");
            }

            return sb.toString();
        }
    }
}
