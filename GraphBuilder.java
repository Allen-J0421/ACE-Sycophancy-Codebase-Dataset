import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class GraphBuilder {
    private final int vertexCount;
    private final GraphType type;
    private final List<Edge> edges;

    GraphBuilder(int vertexCount, GraphType type) {
        GraphValidation.requireVertexCount(vertexCount);
        this.vertexCount = vertexCount;
        this.type = Objects.requireNonNull(type, "type");
        this.edges = new ArrayList<>();
    }

    static GraphBuilder directed(int vertexCount) {
        return new GraphBuilder(vertexCount, GraphType.DIRECTED);
    }

    static GraphBuilder undirected(int vertexCount) {
        return new GraphBuilder(vertexCount, GraphType.UNDIRECTED);
    }

    static GraphBuilder fromGraph(Graph graph) {
        Graph nonNullGraph = Objects.requireNonNull(graph, "graph");
        return new GraphBuilder(nonNullGraph.vertexCount(), nonNullGraph.type()).addEdges(nonNullGraph.edges());
    }

    GraphBuilder addEdge(int from, int to) {
        return addEdge(Edge.between(from, to));
    }

    GraphBuilder addEdge(Edge edge) {
        Edge nonNullEdge = Objects.requireNonNull(edge, "edge");
        GraphValidation.requireVertex(nonNullEdge.from(), vertexCount, "edge.from");
        GraphValidation.requireVertex(nonNullEdge.to(), vertexCount, "edge.to");
        edges.add(nonNullEdge);
        return this;
    }

    GraphBuilder addEdges(int[][] edges) {
        Objects.requireNonNull(edges, "edges");

        for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
            int[] edge = GraphValidation.requireEdgeVertices(edges[edgeIndex], vertexCount, edgeIndex);
            addEdge(edge[0], edge[1]);
        }

        return this;
    }

    GraphBuilder addEdges(Iterable<Edge> edges) {
        Objects.requireNonNull(edges, "edges");
        for (Edge edge : edges) {
            addEdge(edge);
        }

        return this;
    }

    GraphBuilder addGraph(Graph graph) {
        Graph nonNullGraph = Objects.requireNonNull(graph, "graph");
        if (nonNullGraph.type() != type) {
            throw new IllegalArgumentException("graph type " + nonNullGraph.type() + " does not match builder type " + type);
        }
        if (nonNullGraph.vertexCount() != vertexCount) {
            throw new IllegalArgumentException(
                "graph vertex count " + nonNullGraph.vertexCount() + " does not match builder vertex count " + vertexCount
            );
        }

        return addEdges(nonNullGraph.edges());
    }

    Graph build() {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjacencyList.get(edge.from()).add(edge.to());
            if (type == GraphType.UNDIRECTED) {
                adjacencyList.get(edge.to()).add(edge.from());
            }
        }

        return Graph.create(type, edges, adjacencyList);
    }
}
