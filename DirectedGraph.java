import java.util.ArrayList;
import java.util.List;

public class DirectedGraph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    DirectedGraph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        List<List<Integer>> sealed = new ArrayList<>(vertexCount + 1);
        for (List<Integer> neighbors : adjacencyList) {
            sealed.add(List.copyOf(neighbors));
        }
        this.adjacencyList = List.copyOf(sealed);
    }

    static DirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        List<List<Integer>> adjList = emptyAdjacencyList(vertexCount);
        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(edge.getDestination());
        }
        return new DirectedGraph(vertexCount, adjList);
    }

    public List<Integer> neighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public DirectedGraph reverse() {
        List<List<Integer>> reversed = emptyAdjacencyList(vertexCount);
        for (int u = 1; u <= vertexCount; u++) {
            for (int v : adjacencyList.get(u)) {
                reversed.get(v).add(u);
            }
        }
        return new DirectedGraph(vertexCount, reversed);
    }

    private static List<List<Integer>> emptyAdjacencyList(int vertexCount) {
        List<List<Integer>> list = new ArrayList<>(vertexCount + 1);
        for (int i = 0; i <= vertexCount; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }
}
