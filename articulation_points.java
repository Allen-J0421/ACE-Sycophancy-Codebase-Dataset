import java.util.ArrayList;

class ArticulationPoints {

    private interface DfsContext {
        boolean isVisited(int vertex);

        void markVisited(int vertex);

        int discover(int vertex);

        int getDiscovery(int vertex);

        int getLow(int vertex);

        void setLow(int vertex, int value);

        void markArticulation(int vertex);

        boolean isArticulation(int vertex);
    }

    private static final class DfsState {
        final int[] discoveryTime;
        final int[] lowLink;
        final boolean[] visited;
        final boolean[] articulationPoint;
        int time;

        DfsState(int vertexCount) {
            this.discoveryTime = new int[vertexCount];
            this.lowLink = new int[vertexCount];
            this.visited = new boolean[vertexCount];
            this.articulationPoint = new boolean[vertexCount];
            this.time = 0;
        }
    }

    private static final class ArrayDfsContext implements DfsContext {
        private final int[] visited;
        private final int[] discoveryTime;
        private final int[] lowLink;
        private final int[] time;
        private final int[] articulationPoint;

        ArrayDfsContext(int[] visited, int[] discoveryTime, int[] lowLink, int[] time, int[] articulationPoint) {
            this.visited = visited;
            this.discoveryTime = discoveryTime;
            this.lowLink = lowLink;
            this.time = time;
            this.articulationPoint = articulationPoint;
        }

        @Override
        public boolean isVisited(int vertex) {
            return visited[vertex] != 0;
        }

        @Override
        public void markVisited(int vertex) {
            visited[vertex] = 1;
        }

        @Override
        public int discover(int vertex) {
            return discoveryTime[vertex] = lowLink[vertex] = ++time[0];
        }

        @Override
        public int getDiscovery(int vertex) {
            return discoveryTime[vertex];
        }

        @Override
        public int getLow(int vertex) {
            return lowLink[vertex];
        }

        @Override
        public void setLow(int vertex, int value) {
            lowLink[vertex] = value;
        }

        @Override
        public void markArticulation(int vertex) {
            articulationPoint[vertex] = 1;
        }

        @Override
        public boolean isArticulation(int vertex) {
            return articulationPoint[vertex] == 1;
        }
    }

    private static final class StateDfsContext implements DfsContext {
        private final DfsState state;

        StateDfsContext(DfsState state) {
            this.state = state;
        }

        @Override
        public boolean isVisited(int vertex) {
            return state.visited[vertex];
        }

        @Override
        public void markVisited(int vertex) {
            state.visited[vertex] = true;
        }

        @Override
        public int discover(int vertex) {
            return state.discoveryTime[vertex] = state.lowLink[vertex] = ++state.time;
        }

        @Override
        public int getDiscovery(int vertex) {
            return state.discoveryTime[vertex];
        }

        @Override
        public int getLow(int vertex) {
            return state.lowLink[vertex];
        }

        @Override
        public void setLow(int vertex, int value) {
            state.lowLink[vertex] = value;
        }

        @Override
        public void markArticulation(int vertex) {
            state.articulationPoint[vertex] = true;
        }

        @Override
        public boolean isArticulation(int vertex) {
            return state.articulationPoint[vertex];
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

    static void findPoints(ArrayList<ArrayList<Integer>> adj, int u, int[] visited,
            int[] disc, int[] low, int[] time, int parent, int[] isAP) {
        dfs(adj, u, parent, new ArrayDfsContext(visited, disc, low, time, isAP));
    }

    static ArrayList<Integer> articulationPoints(int vertexCount, ArrayList<ArrayList<Integer>> adj) {
        DfsContext context = new StateDfsContext(new DfsState(vertexCount));

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!context.isVisited(vertex)) {
                dfs(adj, vertex, -1, context);
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (context.isArticulation(vertex)) {
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
            DfsContext context) {
        context.markVisited(vertex);
        context.discover(vertex);

        int childCount = 0;
        for (int neighbor : adjacencyList.get(vertex)) {
            if (!context.isVisited(neighbor)) {
                childCount++;
                dfs(adjacencyList, neighbor, vertex, context);

                context.setLow(vertex, Math.min(context.getLow(vertex), context.getLow(neighbor)));

                if (parent != -1 && context.getLow(neighbor) >= context.getDiscovery(vertex)) {
                    context.markArticulation(vertex);
                }
            } else if (neighbor != parent) {
                context.setLow(vertex, Math.min(context.getLow(vertex), context.getDiscovery(neighbor)));
            }
        }

        if (parent == -1 && childCount > 1) {
            context.markArticulation(vertex);
        }
    }
}
