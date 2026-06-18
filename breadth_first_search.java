import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

class UndirectedGraph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;

    public UndirectedGraph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        if (isValidVertex(u) && isValidVertex(v)) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }
    }

    public List<Integer> getAdjacent(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < vertexCount;
    }
}

class BreadthFirstSearch {
    private final UndirectedGraph graph;
    private final boolean[] visited;
    private final List<Integer> result;

    public BreadthFirstSearch(UndirectedGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.result = new ArrayList<>();
    }

    public List<Integer> traverse() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                bfsFromVertex(i);
            }
        }
        return result;
    }

    private void bfsFromVertex(int startVertex) {
        Queue<Integer> queue = new LinkedList<>();
        visited[startVertex] = true;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : graph.getAdjacent(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }
}

public class BreadthFirstSearchDemo {
    public static void main(String[] args) {
        UndirectedGraph graph = new UndirectedGraph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        BreadthFirstSearch bfs = new BreadthFirstSearch(graph);
        List<Integer> traversalOrder = bfs.traverse();

        System.out.print("BFS Traversal: ");
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
