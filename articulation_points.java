import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {
    private static final int ROOT_PARENT = -1;
    private static final int NO_ARTICULATION_POINTS = -1;

    static Graph buildGraph(int vertexCount, int[][] edges) {
        return Graph.fromEdges(vertexCount, edges);
    }

    static ArrayList<Integer> articulationPoints(Graph graph) {
        return new ArticulationPointFinder(graph).find();
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
    }

    private static final class ArticulationPointFinder {
        private final Graph graph;
        private final int[] discoveryTime;
        private final int[] lowLink;
        private final boolean[] visited;
        private final boolean[] articulationPoint;
        private int currentTime;

        private ArticulationPointFinder(Graph graph) {
            int vertexCount = graph.vertexCount();
            this.graph = graph;
            this.discoveryTime = new int[vertexCount];
            this.lowLink = new int[vertexCount];
            this.visited = new boolean[vertexCount];
            this.articulationPoint = new boolean[vertexCount];
            this.currentTime = 0;
        }

        private ArrayList<Integer> find() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    visit(vertex, ROOT_PARENT);
                }
            }

            return collectArticulationPoints();
        }

        private void visit(int vertex, int parent) {
            visited[vertex] = true;
            discoveryTime[vertex] = ++currentTime;
            lowLink[vertex] = discoveryTime[vertex];

            int childCount = 0;
            for (int neighbor : graph.neighborsOf(vertex)) {
                if (!visited[neighbor]) {
                    childCount++;
                    visit(neighbor, vertex);
                    lowLink[vertex] = Math.min(lowLink[vertex], lowLink[neighbor]);

                    if (parent != ROOT_PARENT && lowLink[neighbor] >= discoveryTime[vertex]) {
                        articulationPoint[vertex] = true;
                    }
                } else if (neighbor != parent) {
                    lowLink[vertex] = Math.min(lowLink[vertex], discoveryTime[neighbor]);
                }
            }

            if (parent == ROOT_PARENT && childCount > 1) {
                articulationPoint[vertex] = true;
            }
        }

        private ArrayList<Integer> collectArticulationPoints() {
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
