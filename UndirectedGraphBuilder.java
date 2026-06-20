import java.util.ArrayList;
import java.util.List;

final class UndirectedGraphBuilder {

    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    private UndirectedGraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    static UndirectedGraph fromEdgeList(int vertexCount, int[][] edges) {
        UndirectedGraphBuilder builder = new UndirectedGraphBuilder(vertexCount);
        builder.addEdges(edges);
        return builder.build();
    }

    UndirectedGraphBuilder addEdge(int source, int target) {
        validateVertex(source);
        validateVertex(target);

        adjacencyList.get(source).add(target);
        adjacencyList.get(target).add(source);
        return this;
    }

    UndirectedGraph build() {
        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(vertexCount);
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(List.copyOf(neighbors));
        }

        return new UndirectedGraph(vertexCount, frozenAdjacencyList);
    }

    private UndirectedGraphBuilder addEdges(int[][] edges) {
        if (edges == null) {
            throw new NullPointerException("edges");
        }

        for (int[] edge : edges) {
            validateEdge(edge);
            addEdge(edge[0], edge[1]);
        }

        return this;
    }

    private void validateEdge(int[] edge) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("Each edge must contain exactly two vertices");
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Vertex " + vertex + " is out of bounds for graph size " + vertexCount);
        }
    }
}
