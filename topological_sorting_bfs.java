import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

class Graph {
    private final int vertices;
    private final List<List<Integer>> adjacencyList;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination) {
        this.adjacencyList.get(source).add(destination);
    }

    public List<Integer> topologicalSort() {
        int[] indegree = calculateIndegrees();
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            for (int neighbor : adjacencyList.get(node)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    private int[] calculateIndegrees() {
        int[] indegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            for (int neighbor : adjacencyList.get(i)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 1);
        graph.addEdge(5, 2);

        List<Integer> result = graph.topologicalSort();
        for (int vertex : result) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
