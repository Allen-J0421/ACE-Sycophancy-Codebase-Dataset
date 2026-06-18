import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class TopologicalSort {

    static List<Integer> topoSort(List<List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] indegree = buildIndegreeTable(adjacencyList);
        Deque<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        enqueueZeroIndegreeVertices(indegree, queue);

        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            order.add(vertex);
            for (int next : adjacencyList.get(vertex)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.add(next);
                }
            }
        }

        if (order.size() != vertexCount) {
            throw new IllegalStateException("Topological sort requires a directed acyclic graph.");
        }

        return order;
    }

    private static int[] buildIndegreeTable(List<List<Integer>> adjacencyList) {
        int[] indegree = new int[adjacencyList.size()];
        for (List<Integer> neighbors : adjacencyList) {
            for (int next : neighbors) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private static void enqueueZeroIndegreeVertices(int[] indegree, Deque<Integer> queue) {
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
    }

    static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    static void addEdge(List<List<Integer>> adjacencyList, int source, int destination) {
        adjacencyList.get(source).add(destination);
    }

    private static void printOrder(List<Integer> order) {
        for (int vertex : order) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        List<List<Integer>> adjacencyList = createGraph(vertexCount);

        addEdge(adjacencyList, 0, 1);
        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 2, 3);
        addEdge(adjacencyList, 4, 5);
        addEdge(adjacencyList, 5, 1);
        addEdge(adjacencyList, 5, 2);

        List<Integer> order = topoSort(adjacencyList);
        printOrder(order);
    }
}
