import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {

    private static final int NO_PARENT = -1;
    private static final int NO_ARTICULATION_POINT = -1;

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

        List<Integer> result = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (state.isArticulationPoint[vertex]) {
                result.add(vertex);
            }
        }

        if (result.isEmpty()) {
            result.add(NO_ARTICULATION_POINT);
        }
        return result;
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

    private static class SearchState {
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
        int vertexCount = 5;

        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        List<List<Integer>> adj = constructAdj(vertexCount, edges);
        List<Integer> ans = articulationPoints(vertexCount, adj);

        for (int vertex : ans) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
