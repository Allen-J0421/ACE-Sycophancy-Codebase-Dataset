import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;
    private final int[] indegree;

    private DirectedGraph(List<List<Integer>> adjacencyList, int[] indegree) {
        this.adjacencyList = adjacencyList;
        this.indegree = indegree;
    }

    public static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    public static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
        Builder graph = builder(adjacencyList.size());
        for (int source = 0; source < adjacencyList.size(); source++) {
            for (int destination : adjacencyList.get(source)) {
                graph.addEdge(source, destination);
            }
        }
        return graph.build();
    }

    public boolean hasCycle() {
        return DetectCycleDirectedGraph.hasCycle(this);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    public int[] indegreeSnapshot() {
        return indegree.clone();
    }

    private void validateVertex(int vertex) {
        validateVertex(vertex, adjacencyList.size());
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    "Invalid vertex: " + vertex + " for graph size " + vertexCount);
        }
    }

    public static final class Builder {
        private final List<List<Integer>> adjacencyList;
        private final int[] indegree;

        private Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            adjacencyList = new ArrayList<>(vertexCount);
            indegree = new int[vertexCount];
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        public Builder addEdge(int source, int destination) {
            validateVertex(source, adjacencyList.size());
            validateVertex(destination, adjacencyList.size());
            adjacencyList.get(source).add(destination);
            indegree[destination]++;
            return this;
        }

        public DirectedGraph build() {
            List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
            }

            return new DirectedGraph(
                    Collections.unmodifiableList(immutableAdjacencyList),
                    indegree.clone());
        }
    }
}
