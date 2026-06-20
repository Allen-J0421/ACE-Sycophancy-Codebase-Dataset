import java.util.List;

final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;
    private final int[] indegree;

    DirectedGraph(List<List<Integer>> adjacencyList, int[] indegree) {
        this.adjacencyList = adjacencyList;
        this.indegree = indegree;
    }

    public static DirectedGraphBuilder builder(int vertexCount) {
        return new DirectedGraphBuilder(vertexCount);
    }

    public static DirectedGraph fromAdjacencyList(List<List<Integer>> adjacencyList) {
        DirectedGraphBuilder graph = builder(adjacencyList.size());
        for (int source = 0; source < adjacencyList.size(); source++) {
            for (int destination : adjacencyList.get(source)) {
                graph.addEdge(source, destination);
            }
        }
        return graph.build();
    }

    public static DirectedGraph fromEdges(int vertexCount, List<DirectedEdge> edges) {
        DirectedGraphBuilder graph = builder(vertexCount);
        for (DirectedEdge edge : edges) {
            graph.addEdge(edge);
        }
        return graph.build();
    }

    public boolean hasCycle() {
        return CycleDetector.hasCycle(this);
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
        validateVertexIndex(vertex, adjacencyList.size());
    }

    static void validateVertexIndex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    "Invalid vertex: " + vertex + " for graph size " + vertexCount);
        }
    }
}
