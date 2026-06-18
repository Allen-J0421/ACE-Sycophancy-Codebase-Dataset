package depthfirstsearch.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Graph {

    private final List<List<Integer>> adjacencyList;

    Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Graph fromUndirectedEdges(int vertices, int[][] edges) {
        Objects.requireNonNull(edges, "edges");

        GraphBuilder builder = new GraphBuilder(vertices);
        for (int[] edge : edges) {
            if (edge == null || edge.length != 2) {
                throw new IllegalArgumentException(
                        "Each edge must contain exactly two vertices: " + Arrays.toString(edge));
            }
            builder.addUndirectedEdge(edge[0], edge[1]);
        }

        return builder.build();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighbors(int vertex) {
        checkVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void checkVertex(int vertex) {
        Objects.checkIndex(vertex, adjacencyList.size());
    }
}
