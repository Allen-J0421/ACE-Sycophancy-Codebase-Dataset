public final class FloydWarshall {

    private final WeightedGraph graph;

    private FloydWarshall(WeightedGraph graph) {
        this.graph = graph;
    }

    public static FloydWarshall from(int[][] graph) {
        return new FloydWarshall(WeightedGraph.from(graph));
    }

    public static FloydWarshall from(int[][] graph, int unreachableDistance) {
        return new FloydWarshall(WeightedGraph.from(graph, unreachableDistance));
    }

    public static FloydWarshall from(WeightedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }
        return new FloydWarshall(graph);
    }

    public int vertexCount() {
        return graph.vertexCount();
    }

    public AllPairsShortestPaths solve() {
        IntSquareMatrix adjacencyMatrix = graph.adjacencyMatrix();
        int[][] distances = adjacencyMatrix.copyValues();
        int[][] nextHop = new int[graph.vertexCount()][graph.vertexCount()];
        initializeNextHop(adjacencyMatrix, nextHop, graph.unreachableDistance());

        int vertices = distances.length;
        int unreachableDistance = graph.unreachableDistance();
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                if (distances[i][k] == unreachableDistance) {
                    continue;
                }

                for (int j = 0; j < vertices; j++) {
                    if (distances[k][j] == unreachableDistance) {
                        continue;
                    }

                    long throughK = (long) distances[i][k] + distances[k][j];
                    if (throughK < distances[i][j]) {
                        distances[i][j] = (int) throughK;
                        nextHop[i][j] = nextHop[i][k];
                    }
                }
            }
        }

        return new AllPairsShortestPaths(distances, nextHop);
    }

    private void initializeNextHop(IntSquareMatrix adjacencyMatrix, int[][] nextHop, int unreachableDistance) {
        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            for (int j = 0; j < adjacencyMatrix.size(); j++) {
                nextHop[i][j] = adjacencyMatrix.get(i, j) == unreachableDistance ? -1 : j;
            }
        }
    }
}
