import java.util.ArrayList;

class ArticulationPoints {

    private ArticulationPoints() {
        // Utility class.
    }

    static ArrayList<ArrayList<Integer>> constructAdj(int vertexCount, int[][] edges) {
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            addUndirectedEdge(adjacencyList, edge[0], edge[1]);
        }
        return adjacencyList;
    }

    static void findPoints(ArrayList<ArrayList<Integer>> adj, int u, int[] visited,
            int[] disc, int[] low, int[] time, int parent, int[] isAP) {
        dfs(adj, u, parent, visited, disc, low, time, isAP);
    }

    static ArrayList<Integer> articulationPoints(int vertexCount, ArrayList<ArrayList<Integer>> adj) {
        int[] discoveryTime = new int[vertexCount];
        int[] lowLink = new int[vertexCount];
        int[] visited = new int[vertexCount];
        int[] articulationPoint = new int[vertexCount];
        int[] time = {0};

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (visited[vertex] == 0) {
                dfs(adj, vertex, -1, visited, discoveryTime, lowLink, time, articulationPoint);
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (articulationPoint[vertex] == 1) {
                result.add(vertex);
            }
        }

        if (result.isEmpty()) {
            result.add(-1);
        }
        return result;
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {
            {0, 1},
            {1, 4},
            {2, 3},
            {2, 4},
            {3, 4}
        };

        ArrayList<ArrayList<Integer>> adjacencyList = constructAdj(vertexCount, edges);
        ArrayList<Integer> articulationPoints = articulationPoints(vertexCount, adjacencyList);

        for (int vertex : articulationPoints) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    private static void addUndirectedEdge(ArrayList<ArrayList<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    private static void dfs(ArrayList<ArrayList<Integer>> adjacencyList, int vertex, int parent,
            int[] visited, int[] discoveryTime, int[] lowLink, int[] time, int[] articulationPoint) {
        visited[vertex] = 1;
        discoveryTime[vertex] = lowLink[vertex] = ++time[0];

        int childCount = 0;
        for (int neighbor : adjacencyList.get(vertex)) {
            if (visited[neighbor] == 0) {
                childCount++;
                dfs(adjacencyList, neighbor, vertex, visited, discoveryTime, lowLink, time, articulationPoint);

                lowLink[vertex] = Math.min(lowLink[vertex], lowLink[neighbor]);

                if (parent != -1 && lowLink[neighbor] >= discoveryTime[vertex]) {
                    articulationPoint[vertex] = 1;
                }
            } else if (neighbor != parent) {
                lowLink[vertex] = Math.min(lowLink[vertex], discoveryTime[neighbor]);
            }
        }

        if (parent == -1 && childCount > 1) {
            articulationPoint[vertex] = 1;
        }
    }
}
