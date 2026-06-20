import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {
    private static final int ROOT_PARENT = -1;
    private static final int NO_ARTICULATION_POINTS = -1;

    static Graph buildGraph(int vertexCount, int[][] edges) {
        return Graph.fromEdges(vertexCount, edges);
    }

    static ArrayList<Integer> articulationPoints(Graph graph) {
        return graph.analyze(new ArticulationPointAnalysis());
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        Graph graph = buildGraph(vertexCount, edges);
        ArrayList<Integer> articulationPoints = articulationPoints(graph);

        for (int vertex : articulationPoints) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    private static final class Graph {
        private final List<List<Integer>> adjacencyList;

        private Graph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        private static Graph fromEdges(int vertexCount, int[][] edges) {
            List<List<Integer>> adjacencyList = new ArrayList<>();
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }

            for (int[] edge : edges) {
                int from = edge[0];
                int to = edge[1];
                adjacencyList.get(from).add(to);
                adjacencyList.get(to).add(from);
            }

            return new Graph(adjacencyList);
        }

        private int vertexCount() {
            return adjacencyList.size();
        }

        private List<Integer> neighborsOf(int vertex) {
            return adjacencyList.get(vertex);
        }

        private <Result> Result analyze(GraphAnalysis<Result> analysis) {
            return analysis.analyze(this);
        }
    }

    private interface GraphAnalysis<Result> {
        Result analyze(Graph graph);
    }

    private abstract static class DepthFirstGraphAnalysis<Result> {
        private final Graph graph;
        private final boolean[] visited;
        private final int[] discoveryTime;
        private int currentTime;

        private DepthFirstGraphAnalysis(Graph graph) {
            this.graph = graph;
            this.discoveryTime = new int[graph.vertexCount()];
            this.visited = new boolean[graph.vertexCount()];
            this.currentTime = 0;
        }

        protected final Result run() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    depthFirstSearch(vertex, ROOT_PARENT);
                }
            }

            return buildResult();
        }

        protected final int discoveryTimeOf(int vertex) {
            return discoveryTime[vertex];
        }

        private void depthFirstSearch(int vertex, int parent) {
            visited[vertex] = true;
            discoveryTime[vertex] = ++currentTime;
            onDiscoverVertex(vertex, parent);

            int childCount = 0;
            for (int neighbor : graph.neighborsOf(vertex)) {
                if (!visited[neighbor]) {
                    childCount++;
                    depthFirstSearch(neighbor, vertex);
                    afterChildTraversal(vertex, neighbor, parent);
                } else if (neighbor != parent) {
                    onBackEdge(vertex, neighbor);
                }
            }

            onCompleteVertex(vertex, parent, childCount);
        }

        protected void onDiscoverVertex(int vertex, int parent) {
        }

        protected void afterChildTraversal(int vertex, int child, int parent) {
        }

        protected void onBackEdge(int vertex, int neighbor) {
        }

        protected void onCompleteVertex(int vertex, int parent, int childCount) {
        }

        protected abstract Result buildResult();
    }

    private static final class ArticulationPointAnalysis implements GraphAnalysis<ArrayList<Integer>> {
        @Override
        public ArrayList<Integer> analyze(Graph graph) {
            return new ArticulationPointTraversal(graph).run();
        }
    }

    private static final class ArticulationPointTraversal extends DepthFirstGraphAnalysis<ArrayList<Integer>> {
        private final int[] lowLink;
        private final boolean[] articulationPoint;

        private ArticulationPointTraversal(Graph graph) {
            super(graph);
            this.lowLink = new int[graph.vertexCount()];
            this.articulationPoint = new boolean[graph.vertexCount()];
        }

        @Override
        protected void onDiscoverVertex(int vertex, int parent) {
            lowLink[vertex] = discoveryTimeOf(vertex);
        }

        @Override
        protected void afterChildTraversal(int vertex, int child, int parent) {
            lowLink[vertex] = Math.min(lowLink[vertex], lowLink[child]);

            if (parent != ROOT_PARENT && lowLink[child] >= discoveryTimeOf(vertex)) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected void onBackEdge(int vertex, int neighbor) {
            lowLink[vertex] = Math.min(lowLink[vertex], discoveryTimeOf(neighbor));
        }

        @Override
        protected void onCompleteVertex(int vertex, int parent, int childCount) {
            if (parent == ROOT_PARENT && childCount > 1) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected ArrayList<Integer> buildResult() {
            ArrayList<Integer> result = new ArrayList<>();

            for (int vertex = 0; vertex < articulationPoint.length; vertex++) {
                if (articulationPoint[vertex]) {
                    result.add(vertex);
                }
            }

            if (result.isEmpty()) {
                result.add(NO_ARTICULATION_POINTS);
            }

            return result;
        }
    }
}
