package depthfirstsearch.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GraphBuilder {

    private final List<List<Integer>> adjacencyList;
    private boolean built;

    public GraphBuilder(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices must be non-negative");
        }

        adjacencyList = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public GraphBuilder addUndirectedEdge(int u, int v) {
        ensureNotBuilt();
        checkVertex(u);
        checkVertex(v);

        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
        return this;
    }

    public Graph build() {
        ensureNotBuilt();
        built = true;

        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(List.copyOf(neighbors));
        }
        return new Graph(List.copyOf(frozenAdjacencyList));
    }

    private void ensureNotBuilt() {
        if (built) {
            throw new IllegalStateException("Builder has already built a graph");
        }
    }

    private void checkVertex(int vertex) {
        Objects.checkIndex(vertex, adjacencyList.size());
    }
}
