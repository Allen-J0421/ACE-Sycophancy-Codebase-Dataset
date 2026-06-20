import java.util.ArrayDeque;
import java.util.Queue;

class CycleDetector {
    private CycleDetector() {}

    static boolean hasCycle(DirectedGraph graph) {
        int n = graph.vertexCount();
        int[] inDegrees = computeInDegrees(graph);

        Queue<Integer> queue = new ArrayDeque<>();
        for (int u = 0; u < n; u++) {
            if (inDegrees[u] == 0) {
                queue.add(u);
            }
        }

        int processedCount = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            processedCount++;
            for (int v : graph.neighbors(u)) {
                if (--inDegrees[v] == 0) {
                    queue.add(v);
                }
            }
        }

        return processedCount != n;
    }

    private static int[] computeInDegrees(DirectedGraph graph) {
        int n = graph.vertexCount();
        int[] inDegrees = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) {
                inDegrees[v]++;
            }
        }
        return inDegrees;
    }
}
