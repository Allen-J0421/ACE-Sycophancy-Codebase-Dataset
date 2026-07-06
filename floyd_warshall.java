interface Graph {
    int INF = (int) 1e8;
    int size();
    int getDistance(int i, int j);
    void setDistance(int i, int j, int d);
}

class AdjacencyMatrixGraph implements Graph {
    private final int[][] dist;

    AdjacencyMatrixGraph(int[][] matrix) {
        this.dist = matrix;
    }

    public int size() { return dist.length; }

    public int getDistance(int i, int j) { return dist[i][j]; }

    public void setDistance(int i, int j, int d) { dist[i][j] = d; }
}

class FloydWarshallService {
    private final Graph graph;

    FloydWarshallService(Graph graph) {
        this.graph = graph;
    }

    void computeShortestPaths() {
        int V = graph.size();
        for (int k = 0; k < V; k++)
            for (int i = 0; i < V; i++)
                for (int j = 0; j < V; j++)
                    if (graph.getDistance(i, k) != Graph.INF && graph.getDistance(k, j) != Graph.INF)
                        graph.setDistance(i, j, Math.min(graph.getDistance(i, j),
                                                         graph.getDistance(i, k) + graph.getDistance(k, j)));
    }
}

class GraphPrinter {
    void print(Graph g) {
        int V = g.size();
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                int d = g.getDistance(i, j);
                System.out.print((d == Graph.INF ? "INF" : d) + " ");
            }
            System.out.println();
        }
    }
}

class FloydWarshall {
    public static void main(String[] args) {
        int INF = Graph.INF;
        Graph g = new AdjacencyMatrixGraph(new int[][] {
            {0,   4,   INF, 5,   INF},
            {INF, 0,   1,   INF, 6  },
            {2,   INF, 0,   3,   INF},
            {INF, INF, 1,   0,   2  },
            {1,   INF, INF, 4,   0  }
        });

        new FloydWarshallService(g).computeShortestPaths();
        new GraphPrinter().print(g);
    }
}
