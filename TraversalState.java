import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class TraversalState {
    private final int[] indegree;
    private final Deque<Integer> readyVertices;
    private final List<Integer> order;

    private TraversalState(int[] indegree, Deque<Integer> readyVertices, List<Integer> order) {
        this.indegree = indegree;
        this.readyVertices = readyVertices;
        this.order = order;
    }

    static TraversalState from(DirectedGraph graph) {
        int[] indegree = buildIndegreeTable(graph);
        Deque<Integer> readyVertices = collectZeroIndegreeVertices(indegree);
        List<Integer> order = new ArrayList<>(graph.vertexCount());
        return new TraversalState(indegree, readyVertices, order);
    }

    boolean hasReadyVertices() {
        return !readyVertices.isEmpty();
    }

    int removeNextReadyVertex() {
        return readyVertices.removeFirst();
    }

    void record(int vertex) {
        order.add(vertex);
    }

    void consumeIncomingEdge(int vertex) {
        indegree[vertex]--;
        if (indegree[vertex] == 0) {
            readyVertices.addLast(vertex);
        }
    }

    boolean isCompleteFor(DirectedGraph graph) {
        return order.size() == graph.vertexCount();
    }

    List<Integer> order() {
        return order;
    }

    private static int[] buildIndegreeTable(DirectedGraph graph) {
        int[] indegree = new int[graph.vertexCount()];
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            for (int neighbor : graph.neighborsOf(vertex)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private static Deque<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Deque<Integer> readyVertices = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                readyVertices.addLast(vertex);
            }
        }
        return readyVertices;
    }
}
