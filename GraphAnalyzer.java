import java.util.List;
import java.util.Optional;

public class GraphAnalyzer {
    private final Graph graph;
    private ConnectedComponentsAnalyzer componentsAnalyzer;
    private CycleDetector cycleDetector;
    private PathFinder pathFinder;

    public GraphAnalyzer(Graph graph) {
        this.graph = graph;
    }

    public AnalysisResult analyze() {
        return new AnalysisResult(
                graph,
                getConnectedComponentsAnalyzer(),
                getCycleDetector(),
                getPathFinder()
        );
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
        private final Graph graph;
        private final ConnectedComponentsAnalyzer componentsAnalyzer;
        private final CycleDetector cycleDetector;
        private final PathFinder pathFinder;

        AnalysisResult(Graph graph, ConnectedComponentsAnalyzer componentsAnalyzer,
                      CycleDetector cycleDetector, PathFinder pathFinder) {
            this.graph = graph;
            this.componentsAnalyzer = componentsAnalyzer;
            this.cycleDetector = cycleDetector;
            this.pathFinder = pathFinder;
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

        public double getDensity() {
            int maxEdges = graph.getVertexCount() * (graph.getVertexCount() - 1) / 2;
            return maxEdges > 0 ? (double) graph.getEdgeCount() / maxEdges : 0;
        }

        public List<Integer> traverseDFS() {
            return DepthFirstSearch.recursive().traverse(graph);
        }

        public List<Integer> traverseBFS() {
            return new BreadthFirstSearch().traverse(graph);
        }
    }
}
