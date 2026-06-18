import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GraphAnalyzer {
    private final IGraph graph;
    private final Logger logger;
    private ConnectedComponentsAnalyzer componentsAnalyzer;
    private CycleDetector cycleDetector;
    private PathFinder pathFinder;

    public GraphAnalyzer(IGraph graph) {
        this(graph, new Logger.NoOpLogger());
    }

    public GraphAnalyzer(IGraph graph, Logger logger) {
        this.graph = graph;
        this.logger = logger;
    }

    public AnalysisResult analyze() {
        return new AnalysisResult(
                graph,
                getConnectedComponentsAnalyzer(),
                getCycleDetector(),
                getPathFinder(),
                logger
        );
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

    private CycleDetector getCycleDetector() {
        if (cycleDetector == null) {
            cycleDetector = new CycleDetector(graph);
        }
        return cycleDetector;
    }

    private PathFinder getPathFinder() {
        if (pathFinder == null) {
            pathFinder = new PathFinder(graph);
        }
        return pathFinder;
    }

    public static class AnalysisResult {
        private final IGraph graph;
        private final ConnectedComponentsAnalyzer componentsAnalyzer;
        private final CycleDetector cycleDetector;
        private final PathFinder pathFinder;
        private final Logger logger;
        private GraphStatistics statistics;

        AnalysisResult(IGraph graph, ConnectedComponentsAnalyzer componentsAnalyzer,
                      CycleDetector cycleDetector, PathFinder pathFinder, Logger logger) {
            this.graph = graph;
            this.componentsAnalyzer = componentsAnalyzer;
            this.cycleDetector = cycleDetector;
            this.pathFinder = pathFinder;
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

        public boolean hasCycle() {
            return cycleDetector.hasCycle();
        }

        public List<List<Integer>> getConnectedComponents() {
            return componentsAnalyzer.findConnectedComponents();
        }

        public Optional<List<Integer>> findPath(int source, int destination) {
            return pathFinder.findPath(source, destination);
        }

        public int getDistance(int source, int destination) {
            return pathFinder.getDistance(source, destination);
        }

        public GraphStatistics getStatistics() {
            if (statistics == null) {
                statistics = new GraphStatistics(graph);
            }
            return statistics;
        }

        public double getDensity() {
            return getStatistics().getDensity();
        }

        public List<Integer> traverseDFS() {
            return DepthFirstSearch.recursive().traverse(graph);
        }

        public List<Integer> traverseBFS() {
            return new BreadthFirstSearch().traverse(graph);
        }

        public String getComprehensiveReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔════════════════════════════════════════════════════════════╗\n");
            sb.append("║            COMPREHENSIVE GRAPH ANALYSIS REPORT             ║\n");
            sb.append("╚════════════════════════════════════════════════════════════╝\n\n");

            sb.append("BASIC PROPERTIES:\n");
            sb.append("  Vertices: ").append(getVertexCount()).append("\n");
            sb.append("  Edges: ").append(getEdgeCount()).append("\n");
            sb.append("  Connected: ").append(isConnected()).append("\n");
            sb.append("  Has Cycle: ").append(hasCycle()).append("\n\n");

            GraphStatistics stats = getStatistics();
            sb.append(stats.getDescription()).append("\n");

            sb.append("CONNECTIVITY:\n");
            sb.append("  Components: ").append(getComponentCount()).append("\n");
            List<List<Integer>> components = getConnectedComponents();
            for (int i = 0; i < components.size(); i++) {
                sb.append("    Component ").append(i).append(": ").append(components.get(i)).append("\n");
            }

            return sb.toString();
        }
    }
}
