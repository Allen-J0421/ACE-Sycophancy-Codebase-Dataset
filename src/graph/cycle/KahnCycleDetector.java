package graph.cycle;

import java.util.LinkedList;
import java.util.Queue;

public class KahnCycleDetector implements CycleDetector {
    @Override
    public boolean hasCycle(DirectedGraph graph) {
        int n = graph.size();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) {
                inDegree[v]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int u = 0; u < n; u++) {
            if (inDegree[u] == 0) {
                queue.add(u);
            }
        }

        int visited = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            visited++;
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        return visited != n;
    }
}
