import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class PrimMST {
    private static final int START_VERTEX = 0;
    private static final int NO_EDGE = 0;

    private PrimMST() {
        // Utility class.
    }

    private static final class Edge {
        final int from;
        final int to;
        final int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return from + " - " + to + "\t" + weight;
        }
    }

    private static int minKey(int[] key, boolean[] mstSet)
    {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int vertex = 0; vertex < mstSet.length; vertex++) {
            if (!mstSet[vertex] && key[vertex] < min) {
                min = key[vertex];
                minIndex = vertex;
            }
        }

        return minIndex;
    }

    private static void validateGraph(int[][] graph) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex");
        }

        final int vertexCount = graph.length;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (graph[vertex] == null || graph[vertex].length != vertexCount) {
                throw new IllegalArgumentException("Graph must be a non-null square adjacency matrix");
            }
        }

        for (int row = 0; row < vertexCount; row++) {
            for (int col = row + 1; col < vertexCount; col++) {
                if (graph[row][col] != graph[col][row]) {
                    throw new IllegalArgumentException("Graph must be undirected and symmetric");
                }
            }
        }
    }

    private static void initializeState(int[] key, boolean[] mstSet) {
        Arrays.fill(key, Integer.MAX_VALUE);
        Arrays.fill(mstSet, false);
    }

    private static void relaxNeighbors(int[][] graph, int u, int[] key, int[] parent, boolean[] mstSet) {
        for (int vertex = 0; vertex < graph.length; vertex++) {
            if (graph[u][vertex] != NO_EDGE && !mstSet[vertex]
                && graph[u][vertex] < key[vertex]) {
                parent[vertex] = u;
                key[vertex] = graph[u][vertex];
            }
        }
    }

    private static List<Edge> toEdgeList(int[] parent, int[][] graph) {
        List<Edge> edges = new ArrayList<>();
        for (int vertex = 1; vertex < graph.length; vertex++) {
            edges.add(new Edge(parent[vertex], vertex, graph[parent[vertex]][vertex]));
        }

        return edges;
    }

    private static List<Edge> buildMST(int[][] graph) {
        final int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] key = new int[vertexCount];
        boolean[] mstSet = new boolean[vertexCount];

        initializeState(key, mstSet);
        Arrays.fill(parent, -1);
        key[START_VERTEX] = 0;

        for (int count = 0; count < vertexCount - 1; count++) {
            int u = minKey(key, mstSet);
            if (u == -1 || key[u] == Integer.MAX_VALUE) {
                throw new IllegalStateException("Graph must be connected to compute an MST");
            }

            mstSet[u] = true;
            relaxNeighbors(graph, u, key, parent, mstSet);
        }

        return toEdgeList(parent, graph);
    }

    private static void printMST(List<Edge> edges) {
        System.out.println("Edge \tWeight");
        for (Edge edge : edges) {
            System.out.println(edge);
        }
    }

    private static int[][] sampleGraph() {
        return new int[][] { { 0, 2, 0, 6, 0 },
                             { 2, 0, 3, 8, 5 },
                             { 0, 3, 0, 0, 7 },
                             { 6, 8, 0, 0, 9 },
                             { 0, 5, 7, 9, 0 } };
    }

    private static void runDemo() {
        printMST(primMST(sampleGraph()));
    }

    static List<Edge> primMST(int[][] graph)
    {
        validateGraph(graph);
        return buildMST(graph);
    }

    public static void main(String[] args)
    {
        runDemo();
    }
}
