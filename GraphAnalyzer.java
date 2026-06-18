import java.util.HashMap;
import java.util.Map;

public class GraphAnalyzer {
    private final Graph graph;

    public GraphAnalyzer(Graph graph) {
        this.graph = graph;
    }

    public GraphMetrics analyze() {
        int vertexCount = graph.getVertexCount();
        int edgeCount = countEdges();
        int componentCount = countComponents();
        Map<Integer, Integer> degreeMap = calculateDegrees();

        return new GraphMetrics(vertexCount, edgeCount, componentCount, degreeMap);
    }

    private int countEdges() {
        int count = 0;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            count += graph.getAdjacent(i).size();
        }
        return count / 2;
    }

    private int countComponents() {
        boolean[] visited = new boolean[graph.getVertexCount()];
        int components = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfs(i, visited);
                components++;
            }
        }
        return components;
    }

    private void dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;
        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    private Map<Integer, Integer> calculateDegrees() {
        Map<Integer, Integer> degrees = new HashMap<>();
        for (int i = 0; i < graph.getVertexCount(); i++) {
            degrees.put(i, graph.getAdjacent(i).size());
        }
        return degrees;
    }
}
