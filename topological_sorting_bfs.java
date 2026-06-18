import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

class TopologicalSort {

    static List<Integer> topoSort(List<? extends List<Integer>> adjacencyList) {
        int vertexCount = adjacencyList.size();
        int[] indegree = calculateIndegrees(adjacencyList, vertexCount);
        Queue<Integer> readyVertices = collectZeroIndegreeVertices(indegree);
        List<Integer> sortedVertices = new ArrayList<>(vertexCount);

        while (!readyVertices.isEmpty()) {
            int vertex = readyVertices.poll();
            sortedVertices.add(vertex);
            for (int next : adjacencyList.get(vertex)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    readyVertices.add(next);
                }
            }
        }

        return sortedVertices;
    }

    static void addEdge(List<? extends List<Integer>> adjacencyList, int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private static int[] calculateIndegrees(List<? extends List<Integer>> adjacencyList, int vertexCount) {
        int[] indegree = new int[vertexCount];
        for (List<Integer> neighbors : adjacencyList) {
            for (int next : neighbors) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private static Queue<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Queue<Integer> readyVertices = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                readyVertices.add(vertex);
            }
        }
        return readyVertices;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
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

        List<Integer> sortedVertices = topoSort(adjacencyList);
        for (int vertex : sortedVertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
