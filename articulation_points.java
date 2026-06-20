import java.util.ArrayList;

class ArticulationPoints {

    private static final class TarjanArticulationFinder {
        private final ArrayList<ArrayList<Integer>> adjacencyList;
        private final boolean[] visited;
        private final int[] discoveryTime;
        private final int[] lowLink;
        private final boolean[] isArticulationPoint;
        private int time;

        TarjanArticulationFinder(ArrayList<ArrayList<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
            int vertexCount = adjacencyList.size();
            this.visited = new boolean[vertexCount];
            this.discoveryTime = new int[vertexCount];
            this.lowLink = new int[vertexCount];
            this.isArticulationPoint = new boolean[vertexCount];
            this.time = 0;
        }

        ArrayList<Integer> find() {
            for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
                if (!visited[vertex]) {
                    dfs(vertex, -1);
                }
            }

            ArrayList<Integer> result = new ArrayList<>();
            for (int vertex = 0; vertex < isArticulationPoint.length; vertex++) {
                if (isArticulationPoint[vertex]) {
                    result.add(vertex);
                }
            }

            if (result.isEmpty()) {
                result.add(-1);
            }
            return result;
        }

        private void dfs(int vertex, int parent) {
            visited[vertex] = true;
            discoveryTime[vertex] = lowLink[vertex] = ++time;

            int childCount = 0;
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited[neighbor]) {
                    childCount++;
                    dfs(neighbor, vertex);

                    lowLink[vertex] = Math.min(lowLink[vertex], lowLink[neighbor]);
                    if (parent != -1 && lowLink[neighbor] >= discoveryTime[vertex]) {
                        isArticulationPoint[vertex] = true;
                    }
                } else if (neighbor != parent) {
                    lowLink[vertex] = Math.min(lowLink[vertex], discoveryTime[neighbor]);
                }
            }

            if (parent == -1 && childCount > 1) {
                isArticulationPoint[vertex] = true;
            }
        }
    }

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

    static ArrayList<Integer> articulationPoints(int vertexCount, ArrayList<ArrayList<Integer>> adjacencyList) {
        if (adjacencyList.size() != vertexCount) {
            throw new IllegalArgumentException("vertexCount must match adjacencyList size");
        }
        return new TarjanArticulationFinder(adjacencyList).find();
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
        ArrayList<Integer> points = articulationPoints(vertexCount, adjacencyList);

        StringBuilder output = new StringBuilder();
        for (int point : points) {
            output.append(point).append(' ');
        }
        System.out.println(output);
    }

    private static void addUndirectedEdge(ArrayList<ArrayList<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }
}
