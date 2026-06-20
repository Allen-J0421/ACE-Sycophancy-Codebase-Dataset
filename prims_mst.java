import java.util.Arrays;

class MST {
    private static final int START_VERTEX = 0;
    private static final int NO_EDGE = 0;

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

    private static void printMST(int[] parent, int[][] graph)
    {
        System.out.println("Edge \tWeight");
        for (int vertex = 1; vertex < graph.length; vertex++) {
            System.out.println(parent[vertex] + " - " + vertex + "\t"
                               + graph[parent[vertex]][vertex]);
        }
    }

    static void primMST(int[][] graph)
    {
        final int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] key = new int[vertexCount];
        boolean[] mstSet = new boolean[vertexCount];

        initializeState(key, mstSet);
        key[START_VERTEX] = 0;
        parent[START_VERTEX] = -1;

        for (int count = 0; count < vertexCount - 1; count++) {
            int u = minKey(key, mstSet);
            mstSet[u] = true;
            relaxNeighbors(graph, u, key, parent, mstSet);
        }

        printMST(parent, graph);
    }

    public static void main(String[] args)
    {
        int[][] graph = new int[][] { { 0, 2, 0, 6, 0 },
                                      { 2, 0, 3, 8, 5 },
                                      { 0, 3, 0, 0, 7 },
                                      { 6, 8, 0, 0, 9 },
                                      { 0, 5, 7, 9, 0 } };

        primMST(graph);
    }
}
