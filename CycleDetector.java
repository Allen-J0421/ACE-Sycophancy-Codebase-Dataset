import java.util.ArrayList;
import java.util.List;

public class CycleDetector {
    private final Graph graph;
    private boolean[] visited;
    private boolean[] recursionStack;
    private List<Integer> cycleVertices;

    public CycleDetector(Graph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.recursionStack = new boolean[graph.getVertexCount()];
        this.cycleVertices = new ArrayList<>();
    }

    public boolean hasCycle() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                if (dfsHasCycle(i, -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Integer> getCycleVertices() {
        cycleVertices.clear();
        visited = new boolean[graph.getVertexCount()];
        recursionStack = new boolean[graph.getVertexCount()];

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsHasCycle(i, -1);
            }
        }

        return cycleVertices;
    }

    private boolean dfsHasCycle(int vertex, int parent) {
        visited[vertex] = true;
        recursionStack[vertex] = true;

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                if (dfsHasCycle(neighbor, vertex)) {
                    cycleVertices.add(vertex);
                    return true;
                }
            } else if (neighbor != parent && recursionStack[neighbor]) {
                cycleVertices.add(vertex);
                cycleVertices.add(neighbor);
                return true;
            }
        }

        recursionStack[vertex] = false;
        return false;
    }
}
