class MST {

    int minKey(int[] key, boolean[] mstSet)
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

    void printMST(int[] parent, int[][] graph)
    {
        System.out.println("Edge \tWeight");
        for (int vertex = 1; vertex < graph.length; vertex++) {
            System.out.println(parent[vertex] + " - " + vertex + "\t"
                               + graph[parent[vertex]][vertex]);
        }
    }

    void primMST(int[][] graph)
    {
        final int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] key = new int[vertexCount];
        boolean[] mstSet = new boolean[vertexCount];

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            key[vertex] = Integer.MAX_VALUE;
        }

        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < vertexCount - 1; count++) {
            int u = minKey(key, mstSet);

            mstSet[u] = true;

            for (int vertex = 0; vertex < vertexCount; vertex++) {
                if (graph[u][vertex] != 0 && !mstSet[vertex]
                    && graph[u][vertex] < key[vertex]) {
                    parent[vertex] = u;
                    key[vertex] = graph[u][vertex];
                }
            }
        }

        printMST(parent, graph);
    }

    public static void main(String[] args)
    {
        MST t = new MST();
        int[][] graph = new int[][] { { 0, 2, 0, 6, 0 },
                                      { 2, 0, 3, 8, 5 },
                                      { 0, 3, 0, 0, 7 },
                                      { 6, 8, 0, 0, 9 },
                                      { 0, 5, 7, 9, 0 } };

        t.primMST(graph);
    }
}
