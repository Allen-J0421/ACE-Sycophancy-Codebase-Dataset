import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {

    private static final int NO_PARENT = -1;
    private static final int NO_ARTICULATION_POINT = -1;

    private ArticulationPoints() {
    }

    static List<List<Integer>> constructAdj(int vertexCount, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return adj;
    }

    static List<Integer> articulationPoints(int vertexCount, List<List<Integer>> adj) {

        SearchState state = new SearchState(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!state.isVisited(vertex)) {
                depthFirstSearch(adj, vertex, NO_PARENT, state);
            }
        }

        return collectArticulationPoints(vertexCount, state);
    }

    private static List<Integer> collectArticulationPoints(int vertexCount, SearchState state) {
        List<Integer> articulationPoints = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (state.isArticulationPoint(vertex)) {
                articulationPoints.add(vertex);
            }
        }

        if (articulationPoints.isEmpty()) {
            articulationPoints.add(NO_ARTICULATION_POINT);
        }
        return articulationPoints;
    }

    private static void depthFirstSearch(
            List<List<Integer>> adj,
            int vertex,
            int parent,
            SearchState state
    ) {
        state.visit(vertex);
        int childCount = 0;

        for (int neighbor : adj.get(vertex)) {
            if (!state.isVisited(neighbor)) {
                childCount++;
                depthFirstSearch(adj, neighbor, vertex, state);
                state.updateLowestReachableFromChild(vertex, neighbor);

                if (isNonRootArticulationPoint(vertex, neighbor, parent, state)) {
                    state.markArticulationPoint(vertex);
                }
            } else if (neighbor != parent) {
                state.updateLowestReachableFromBackEdge(vertex, neighbor);
            }
        }

        if (isRootArticulationPoint(parent, childCount)) {
            state.markArticulationPoint(vertex);
        }
    }

    private static boolean isNonRootArticulationPoint(
            int vertex,
            int child,
            int parent,
            SearchState state
    ) {
        return parent != NO_PARENT
                && state.lowestReachableTime(child) >= state.discoveryTime(vertex);
    }

    private static boolean isRootArticulationPoint(int parent, int childCount) {
        return parent == NO_PARENT && childCount > 1;
    }

    private static final class SearchState {
        private final boolean[] visited;
        private final int[] discoveryTime;
        private final int[] lowestReachableTime;
        private final boolean[] isArticulationPoint;
        private int time;

        private SearchState(int vertexCount) {
            visited = new boolean[vertexCount];
            discoveryTime = new int[vertexCount];
            lowestReachableTime = new int[vertexCount];
            isArticulationPoint = new boolean[vertexCount];
        }

        private void visit(int vertex) {
            visited[vertex] = true;
            discoveryTime[vertex] = lowestReachableTime[vertex] = ++time;
        }

        private boolean isVisited(int vertex) {
            return visited[vertex];
        }

        private boolean isArticulationPoint(int vertex) {
            return isArticulationPoint[vertex];
        }

        private void markArticulationPoint(int vertex) {
            isArticulationPoint[vertex] = true;
        }

        private int discoveryTime(int vertex) {
            return discoveryTime[vertex];
        }

        private int lowestReachableTime(int vertex) {
            return lowestReachableTime[vertex];
        }

        private void updateLowestReachableFromChild(int vertex, int child) {
            lowestReachableTime[vertex] = Math.min(
                    lowestReachableTime[vertex],
                    lowestReachableTime[child]
            );
        }

        private void updateLowestReachableFromBackEdge(int vertex, int ancestor) {
            lowestReachableTime[vertex] = Math.min(
                    lowestReachableTime[vertex],
                    discoveryTime[ancestor]
            );
        }
    }

    public static void main(String[] args) {
        runSample();
    }

    private static void runSample() {
        int vertexCount = 5;

        List<List<Integer>> adj = constructAdj(vertexCount, sampleEdges());
        List<Integer> articulationPoints = articulationPoints(vertexCount, adj);

        printVertices(articulationPoints);
    }

    private static int[][] sampleEdges() {
        return new int[][]{{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
    }

    private static void printVertices(List<Integer> vertices) {
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
