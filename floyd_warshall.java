class FloydWarshall {

    static void floydWarshall(int[][] dist) {
        int V = dist.length;
        int INF = (int)1e8;

        for (int k = 0; k < V; k++) {

            for (int i = 0; i < V; i++) {

                for (int j = 0; j < V; j++) {

                    if(dist[i][k] != INF && dist[k][j]!= INF)
                        dist[i][j] = Math.min(dist[i][j],
                                              dist[i][k] + dist[k][j]);
                }
            }
        }
    }

    public static void main(String[] args) {
        int INF = (int)1e8;

        int[][] dist = {{0, 4, INF, 5, INF},
                        {INF, 0, 1, INF, 6},
                        {2, INF, 0, 3, INF },
                        {INF, INF, 1, 0, 2},
                        {1, INF, INF, 4, 0}};

        floydWarshall(dist);

        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                System.out.print(dist[i][j] + " ");
            }
            System.out.println();
        }
    }
}
