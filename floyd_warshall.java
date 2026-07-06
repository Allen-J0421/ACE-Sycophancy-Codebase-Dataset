class Graph {
    static final int INF = (int) 1e8;

    private final int[][] dist;

    Graph(int[][] matrix) {
        this.dist = matrix;
    }

    int size() { return dist.length; }

    int getDistance(int i, int j) { return dist[i][j]; }

    void setDistance(int i, int j, int d) { dist[i][j] = d; }

    void print() {
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                System.out.print((dist[i][j] == INF ? "INF" : dist[i][j]) + " ");
            }
            System.out.println();
        }
    }
}

class FloydWarshallService {
    void computeShortestPaths(Graph g) {
        int V = g.size();
        for (int k = 0; k < V; k++)
            for (int i = 0; i < V; i++)
                for (int j = 0; j < V; j++)
                    if (g.getDistance(i, k) != Graph.INF && g.getDistance(k, j) != Graph.INF)
                        g.setDistance(i, j, Math.min(g.getDistance(i, j),
                                                     g.getDistance(i, k) + g.getDistance(k, j)));
    }
}

class FloydWarshall {
    public static void main(String[] args) {
        int INF = Graph.INF;
        Graph g = new Graph(new int[][] {
            {0,   4,   INF, 5,   INF},
            {INF, 0,   1,   INF, 6  },
            {2,   INF, 0,   3,   INF},
            {INF, INF, 1,   0,   2  },
            {1,   INF, INF, 4,   0  }
        });

        new FloydWarshallService().computeShortestPaths(g);
        g.print();
    }
}
