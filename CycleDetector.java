import java.util.ArrayDeque;
import java.util.Queue;

final class CycleDetector {
    private final DirectedGraph graph;
    private final int[] indegree;
    private final Queue<Integer> zeroIndegreeVertices;
    private int visitedCount;

    CycleDetector(DirectedGraph graph) {
        this.graph = graph;
        this.indegree = graph.indegreeSnapshot();
        this.zeroIndegreeVertices = collectZeroIndegreeVertices(indegree);
    }

    boolean hasCycle() {
        while (!zeroIndegreeVertices.isEmpty()) {
            processNextVertex();
        }
        return visitedCount != graph.vertexCount();
    }

    private void processNextVertex() {
        int vertex = zeroIndegreeVertices.poll();
        visitedCount++;

        for (int neighbor : graph.neighborsOf(vertex)) {
            decrementIndegree(neighbor);
        }
    }

    private void decrementIndegree(int vertex) {
        indegree[vertex]--;
        if (indegree[vertex] == 0) {
            zeroIndegreeVertices.add(vertex);
        }
    }

    private static Queue<Integer> collectZeroIndegreeVertices(int[] indegree) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int vertex = 0; vertex < indegree.length; vertex++) {
            if (indegree[vertex] == 0) {
                queue.add(vertex);
            }
        }
        return queue;
    }
}
