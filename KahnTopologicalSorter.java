import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class KahnTopologicalSorter implements TopologicalSorter {
    @Override
    public List<Integer> sort(DirectedGraph graph) {
        int[] indegree = buildIndegreeTable(graph);
        Deque<Integer> readyVertices = collectZeroIndegreeVertices(indegree);
        List<Integer> order = new ArrayList<>(graph.vertexCount());

        while (!readyVertices.isEmpty()) {
            int vertex = readyVertices.removeFirst();
            order.add(vertex);
            for (int next : graph.neighborsOf(vertex)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    readyVertices.addLast(next);
                }
            }
        }

        validateAcyclic(graph, order);
        return order;
    }

    private int[] buildIndegreeTable(DirectedGraph graph) {
        int[] indegree = new int[graph.vertexCount()];
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            for (int neighbor : graph.neighborsOf(vertex)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private Deque<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Deque<Integer> readyVertices = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                readyVertices.addLast(vertex);
            }
        }
        return readyVertices;
    }

    private void validateAcyclic(DirectedGraph graph, List<Integer> order) {
        if (order.size() != graph.vertexCount()) {
            throw new IllegalStateException("Topological sort requires a directed acyclic graph.");
        }
    }
}
