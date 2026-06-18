public class GraphBuilder {
    private final int vertexCount;
    private final Graph graph;
    private final Logger logger;

    public GraphBuilder(int vertexCount) {
        this(vertexCount, new Logger.NoOpLogger());
    }

    public GraphBuilder(int vertexCount, Logger logger) {
        this.vertexCount = vertexCount;
        this.logger = logger;
        this.graph = new Graph(vertexCount, logger);
    }

    public GraphBuilder addEdge(int u, int v) {
        try {
            graph.addEdge(u, v);
        } catch (Exception e) {
            logger.error("Failed to add edge: " + u + " - " + v, e);
        }
        return this;
    }

    public Result<Graph> buildResult() {
        try {
            if (graph.getVertexCount() < 0) {
                return Result.failure("Invalid graph: negative vertex count");
            }
            return Result.success(graph);
        } catch (Exception e) {
            return Result.failure("Failed to build graph", e);
        }
    }

    public Graph build() {
        return graph;
    }
}
