import java.util.Arrays;

class MST {
    private static final int NO_EDGE = 0;
    private static final int ROOT_PARENT = -1;

    private static int findNearestUnvisitedVertex(int[] minimumEdgeWeight, boolean[] includedInMst) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < includedInMst.length; v++) {
            if (!includedInMst[v] && minimumEdgeWeight[v] < min) {
                min = minimumEdgeWeight[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    private static void printMinimumSpanningTree(int[] parent, int[][] graph) {
        System.out.println("Edge \tWeight");
        for (int i = 1; i < graph.length; i++) {
            System.out.println(parent[i] + " - " + i + "\t"
                               + graph[parent[i]][i]);
        }
    }

    private static int[] buildMinimumSpanningTree(int[][] graph) {
        int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] minimumEdgeWeight = new int[vertexCount];
        boolean[] includedInMst = new boolean[vertexCount];

        Arrays.fill(minimumEdgeWeight, Integer.MAX_VALUE);
        minimumEdgeWeight[0] = 0;
        parent[0] = ROOT_PARENT;

        for (int count = 0; count < vertexCount - 1; count++) {
            int u = findNearestUnvisitedVertex(minimumEdgeWeight, includedInMst);
            includedInMst[u] = true;

            for (int v = 0; v < vertexCount; v++) {
                if (graph[u][v] != NO_EDGE && !includedInMst[v]
                    && graph[u][v] < minimumEdgeWeight[v]) {
                    parent[v] = u;
                    minimumEdgeWeight[v] = graph[u][v];
                }
            }
        }

        return parent;
    }

    private static void printPrimMinimumSpanningTree(int[][] graph) {
        int[] parent = buildMinimumSpanningTree(graph);
        printMinimumSpanningTree(parent, graph);
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };
    }

    public static void main(String[] args) {
        printPrimMinimumSpanningTree(sampleGraph());
    }
}
