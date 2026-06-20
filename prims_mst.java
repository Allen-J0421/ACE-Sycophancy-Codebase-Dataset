class MST {
    private static final int NO_EDGE = 0;
    private static final int ROOT_PARENT = -1;

    private static int findMinKeyVertex(int[] key, boolean[] includedInMst) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < includedInMst.length; v++) {
            if (!includedInMst[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    private static void printMST(int[] parent, int[][] graph) {
        System.out.println("Edge \tWeight");
        for (int i = 1; i < graph.length; i++) {
            System.out.println(parent[i] + " - " + i + "\t"
                               + graph[parent[i]][i]);
        }
    }

    private static int[] buildMST(int[][] graph) {
        int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] key = new int[vertexCount];
        boolean[] includedInMst = new boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            key[i] = Integer.MAX_VALUE;
        }

        key[0] = 0;
        parent[0] = ROOT_PARENT;

        for (int count = 0; count < vertexCount - 1; count++) {
            int u = findMinKeyVertex(key, includedInMst);
            includedInMst[u] = true;

            for (int v = 0; v < vertexCount; v++) {
                if (graph[u][v] != NO_EDGE && !includedInMst[v]
                    && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }

        return parent;
    }

    private static void primMST(int[][] graph) {
        int[] parent = buildMST(graph);
        printMST(parent, graph);
    }

    public static void main(String[] args) {
        int[][] graph = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };

        primMST(graph);
    }
}
