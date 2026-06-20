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
            if (!state.visited[vertex]) {
                findPoints(adj, vertex, NO_PARENT, state);
            }
        }

        return collectArticulationPoints(vertexCount, state);
    }

    private static List<Integer> collectArticulationPoints(int vertexCount, SearchState state) {
        List<Integer> articulationPoints = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (state.isArticulationPoint[vertex]) {
                articulationPoints.add(vertex);
            }
        }

        if (articulationPoints.isEmpty()) {
            articulationPoints.add(NO_ARTICULATION_POINT);
        }
        return articulationPoints;
    }

    private static void findPoints(
            List<List<Integer>> adj,
            int vertex,
            int parent,
            SearchState state
    ) {
        state.visited[vertex] = true;
        state.discoveryTime[vertex] = state.lowestReachableTime[vertex] = ++state.time;
        int childCount = 0;

        for (int neighbor : adj.get(vertex)) {
            if (!state.visited[neighbor]) {
                childCount++;
                findPoints(adj, neighbor, vertex, state);

                state.lowestReachableTime[vertex] = Math.min(
                        state.lowestReachableTime[vertex],
                        state.lowestReachableTime[neighbor]
                );

                if (parent != NO_PARENT
                        && state.lowestReachableTime[neighbor] >= state.discoveryTime[vertex]) {
                    state.isArticulationPoint[vertex] = true;
                }
            } else if (neighbor != parent) {
                state.lowestReachableTime[vertex] = Math.min(
                        state.lowestReachableTime[vertex],
                        state.discoveryTime[neighbor]
                );
            }
        }

        if (parent == NO_PARENT && childCount > 1) {
            state.isArticulationPoint[vertex] = true;
        }
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
