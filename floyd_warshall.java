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

class GraphFactory {
    private GraphFactory() {}

    static Graph createSampleGraph() {
        int INF = Graph.INF;
        return new AdjacencyMatrixGraph(new int[][] {
            {0,   4,   INF, 5,   INF},
            {INF, 0,   1,   INF, 6  },
            {2,   INF, 0,   3,   INF},
            {INF, INF, 1,   0,   2  },
            {1,   INF, INF, 4,   0  }
        });
    }

    static Graph fromMatrix(int[][] matrix) {
        return new AdjacencyMatrixGraph(matrix);
    }
}

@FunctionalInterface
interface PathfindingStrategy {
    void compute(Graph g);
}

class FloydWarshallStrategy implements PathfindingStrategy {
    public void compute(Graph g) {
        int V = g.size();
        for (int k = 0; k < V; k++)
            for (int i = 0; i < V; i++)
                for (int j = 0; j < V; j++)
                    if (g.getDistance(i, k) != Graph.INF && g.getDistance(k, j) != Graph.INF)
                        g.setDistance(i, j, Math.min(g.getDistance(i, j),
                                                     g.getDistance(i, k) + g.getDistance(k, j)));
    }
}

class PathfindingResult {
    private final int[][] distances;

    PathfindingResult(Graph g) {
        int V = g.size();
        this.distances = new int[V][V];
        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++)
                this.distances[i][j] = g.getDistance(i, j);
    }

    int size() { return distances.length; }

    int getDistance(int i, int j) { return distances[i][j]; }
}

class PathfindingService {
    private final Graph graph;
    private final PathfindingStrategy strategy;

    PathfindingService(Graph graph, PathfindingStrategy strategy) {
        this.graph = graph;
        this.strategy = strategy;
    }

    PathfindingResult computeShortestPaths() {
        strategy.compute(graph);
        return new PathfindingResult(graph);
    }
}

class GraphPrinter {
    void print(PathfindingResult result) {
        int V = result.size();
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                int d = result.getDistance(i, j);
                System.out.print((d == Graph.INF ? "INF" : d) + " ");
            }
            System.out.println();
        }
    }
}

class PathfindingPipeline {
    private final Graph graph;
    private final PathfindingStrategy strategy;
    private final GraphPrinter printer;

    private PathfindingPipeline(Builder builder) {
        this.graph = builder.graph;
        this.strategy = builder.strategy;
        this.printer = builder.printer;
    }

    PathfindingResult run() {
        PathfindingResult result = new PathfindingService(graph, strategy).computeShortestPaths();
        printer.print(result);
        return result;
    }

    static class Builder {
        private Graph graph;
        private PathfindingStrategy strategy;
        private GraphPrinter printer = new GraphPrinter();

        Builder graph(Graph g)                  { this.graph = g;    return this; }
        Builder strategy(PathfindingStrategy s)  { this.strategy = s; return this; }
        Builder printer(GraphPrinter p)          { this.printer = p;  return this; }

        PathfindingPipeline build() {
            if (graph == null)    throw new IllegalStateException("graph is required");
            if (strategy == null) throw new IllegalStateException("strategy is required");
            return new PathfindingPipeline(this);
        }
    }
}

class FloydWarshall {
    public static void main(String[] args) {
        PathfindingResult result = new PathfindingPipeline.Builder()
            .graph(GraphFactory.createSampleGraph())
            .strategy(new FloydWarshallStrategy())
            .build()
            .run();
    }
}
