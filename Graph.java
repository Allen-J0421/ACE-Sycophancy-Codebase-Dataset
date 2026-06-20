import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Graph implements Iterable<Integer> {
    private final List<List<Integer>> adjacencyList;
    private final List<Integer> vertices;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
        this.vertices = new VertexView(adjacencyList.size());
    }

    public static Graph fromEdges(int vertexCount, int[][] edges) {
        validateVertexCount(vertexCount);
        requireEdges(edges);

        Builder builder = new Builder(vertexCount);
        for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
            builder.addEdge(edges[edgeIndex], edgeIndex);
        }

        return builder.build();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> vertices() {
        return vertices;
    }

    @Override
    public Iterator<Integer> iterator() {
        return vertices.iterator();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertexIndex(vertex);
        return adjacencyList.get(vertex);
    }

    private static List<List<Integer>> toUnmodifiableAdjacencyList(
            List<List<Integer>> adjacencyList) {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(neighbors));
        }

        return Collections.unmodifiableList(immutableAdjacencyList);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private static void requireEdges(int[][] edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }
    }

    private static void validateEdgeShape(int[] edge, int edgeIndex) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException(
                    "Each edge must contain exactly two vertices. Invalid edge at index "
                            + edgeIndex
                            + ".");
        }
    }

    private static int validateVertex(int vertex, int vertexCount, int edgeIndex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Edge at index "
                            + edgeIndex
                            + " references invalid vertex "
                            + vertex
                            + " for graph size "
                            + vertexCount
                            + ".");
        }

        return vertex;
    }

    private void validateVertexIndex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IndexOutOfBoundsException(
                    "Vertex "
                            + vertex
                            + " is out of bounds for graph size "
                            + adjacencyList.size()
                            + ".");
        }
    }

    private static final class Builder {
        private final List<List<Integer>> adjacencyList;
        private final int vertexCount;

        private Builder(int vertexCount) {
            this.vertexCount = vertexCount;
            this.adjacencyList = initializeAdjacencyList(vertexCount);
        }

        private void addEdge(int[] edge, int edgeIndex) {
            validateEdgeShape(edge, edgeIndex);

            int source = validateVertex(edge[0], vertexCount, edgeIndex);
            int destination = validateVertex(edge[1], vertexCount, edgeIndex);
            adjacencyList.get(source).add(destination);
            adjacencyList.get(destination).add(source);
        }

        private Graph build() {
            return new Graph(toUnmodifiableAdjacencyList(adjacencyList));
        }

        private static List<List<Integer>> initializeAdjacencyList(int vertexCount) {
            List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }

            return adjacencyList;
        }
    }

    private static final class VertexView extends AbstractList<Integer> {
        private final int vertexCount;

        private VertexView(int vertexCount) {
            this.vertexCount = vertexCount;
        }

        @Override
        public Integer get(int index) {
            if (index < 0 || index >= vertexCount) {
                throw new IndexOutOfBoundsException(
                        "Vertex index "
                                + index
                                + " is out of bounds for graph size "
                                + vertexCount
                                + ".");
            }

            return index;
        }

        @Override
        public int size() {
            return vertexCount;
        }
    }
}
