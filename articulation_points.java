import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {
    private static final int ROOT_PARENT = -1;
    private static final int NO_ARTICULATION_POINTS = -1;

    static ArrayList<ArrayList<Integer>> buildAdjacencyList(int vertexCount, int[][] edges) {
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacencyList.get(from).add(to);
            adjacencyList.get(to).add(from);
        }

        return adjacencyList;
    }

    static ArrayList<Integer> articulationPoints(int vertexCount, ArrayList<ArrayList<Integer>> adjacencyList) {
        DepthFirstSearchState searchState = new DepthFirstSearchState(vertexCount, adjacencyList);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!searchState.isVisited(vertex)) {
                searchState.visit(vertex, ROOT_PARENT);
            }
        }

        return searchState.collectArticulationPoints();
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        ArrayList<ArrayList<Integer>> adjacencyList = buildAdjacencyList(vertexCount, edges);
        ArrayList<Integer> articulationPoints = articulationPoints(vertexCount, adjacencyList);

        for (int vertex : articulationPoints) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    private static final class DepthFirstSearchState {
        private final List<List<Integer>> adjacencyList;
        private final int[] discoveryTime;
        private final int[] lowLink;
        private final boolean[] visited;
        private final boolean[] articulationPoint;
        private int currentTime;

        private DepthFirstSearchState(int vertexCount, ArrayList<ArrayList<Integer>> adjacencyList) {
            this.adjacencyList = new ArrayList<>(adjacencyList);
            this.discoveryTime = new int[vertexCount];
            this.lowLink = new int[vertexCount];
            this.visited = new boolean[vertexCount];
            this.articulationPoint = new boolean[vertexCount];
            this.currentTime = 0;
        }

        private boolean isVisited(int vertex) {
            return visited[vertex];
        }

        private void visit(int vertex, int parent) {
            visited[vertex] = true;
            discoveryTime[vertex] = ++currentTime;
            lowLink[vertex] = discoveryTime[vertex];

            int childCount = 0;
            for (int neighbor : adjacencyList.get(vertex)) {
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
