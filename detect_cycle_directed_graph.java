import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

class DirectedGraph {
    private final int vertices;
    private final List<List<Integer>> adj;

    DirectedGraph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    int size() { return vertices; }

    Iterable<Integer> neighbors(int v) { return adj.get(v); }
}

interface CycleDetector {
    boolean hasCycle(DirectedGraph graph);
}

class KahnCycleDetector implements CycleDetector {
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

enum Algorithm { KAHN }

class CycleDetectorFactory {
    private CycleDetectorFactory() {}

    static CycleDetector create(Algorithm algorithm) {
        switch (algorithm) {
            case KAHN: return new KahnCycleDetector();
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}

class DetectCycle {
    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);

        CycleDetector detector = CycleDetectorFactory.create(Algorithm.KAHN);
        System.out.println(detector.hasCycle(graph));
    }
}
