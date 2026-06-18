package graph.core;

import graph.config.GraphConfig;
import graph.utility.Logger;
import graph.utility.Result;

public class GraphBuilder {
    private final int vertexCount;
    private final Graph graph;
    private final Logger logger;
    private final GraphConfig config;

    public GraphBuilder(int vertexCount) {
        this(vertexCount, GraphConfig.defaultConfig(), new Logger.NoOpLogger());
    }

    public GraphBuilder(int vertexCount, GraphConfig config) {
        this(vertexCount, config, new Logger.NoOpLogger());
    }

    public GraphBuilder(int vertexCount, GraphConfig config, Logger logger) {
        this.vertexCount = vertexCount;
        this.logger = logger;
        this.config = config;
        this.graph = new Graph(vertexCount, config, logger);
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
