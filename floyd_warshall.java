import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

interface InputParser {
    Graph parse() throws IOException;
}

class MatrixTextParser implements InputParser {
    private final String filePath;

    MatrixTextParser(String filePath) {
        this.filePath = filePath;
    }

    public Graph parse() throws IOException {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+");
                int[] row = new int[tokens.length];
                for (int j = 0; j < tokens.length; j++)
                    row[j] = tokens[j].equalsIgnoreCase("INF") ? Graph.INF : Integer.parseInt(tokens[j]);
                rows.add(row);
            }
        }
        return new AdjacencyMatrixGraph(rows.toArray(new int[0][]));
    }
}

class HardcodedGraphParser implements InputParser {
    public Graph parse() {
        int INF = Graph.INF;
        return new AdjacencyMatrixGraph(new int[][] {
            {0,   4,   INF, 5,   INF},
            {INF, 0,   1,   INF, 6  },
            {2,   INF, 0,   3,   INF},
            {INF, INF, 1,   0,   2  },
            {1,   INF, INF, 4,   0  }
        });
    }
}

class GraphFactory {
    private GraphFactory() {}

    static Graph fromParser(InputParser parser) throws IOException {
        return parser.parse();
    }

    static Graph fromFile(String path) throws IOException {
        return fromParser(new MatrixTextParser(path));
    }

    static Graph fromMatrix(int[][] matrix) {
        return new AdjacencyMatrixGraph(matrix);
    }

    static Graph createSampleGraph() {
        try {
            return fromParser(new HardcodedGraphParser());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

@FunctionalInterface
interface PathfindingStrategy {
    void compute(Graph g);
}

class PathfindingStrategies {
    private PathfindingStrategies() {}

    static class FloydWarshall implements PathfindingStrategy {
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
}

class StrategyRegistry {
    private static final Map<String, PathfindingStrategy> registry = new HashMap<>();

    static {
        register("floyd-warshall", new PathfindingStrategies.FloydWarshall());
    }

    private StrategyRegistry() {}

    static void register(String name, PathfindingStrategy strategy) {
        registry.put(name, strategy);
    }

    static PathfindingStrategy get(String name) {
        PathfindingStrategy strategy = registry.get(name);
        if (strategy == null)
            throw new IllegalArgumentException("No strategy registered under: " + name);
        return strategy;
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
    public static void main(String[] args) throws IOException {
        Graph g = args.length > 0
            ? GraphFactory.fromFile(args[0])
            : GraphFactory.createSampleGraph();

        PathfindingResult result = new PathfindingPipeline.Builder()
            .graph(g)
            .strategy(StrategyRegistry.get("floyd-warshall"))
            .build()
            .run();
    }
}
