import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class DetectCycle {

    static class DirectedGraph {
        private final int vertexCount;
        private final List<List<Integer>> adjacency;

        private DirectedGraph(int vertexCount) {
            this.vertexCount = vertexCount;
            this.adjacency = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        private void addEdge(int from, int to) {
            adjacency.get(from).add(to);
        }

        int vertexCount() {
            return vertexCount;
        }

        List<Integer> neighbors(int vertex) {
            return Collections.unmodifiableList(adjacency.get(vertex));
        }

        static class Builder {
            private final DirectedGraph graph;

            Builder(int vertexCount) {
                this.graph = new DirectedGraph(vertexCount);
            }

            Builder edge(int from, int to) {
                if (from < 0 || from >= graph.vertexCount || to < 0 || to >= graph.vertexCount) {
                    throw new IllegalArgumentException(
                        "Vertex index out of range [0, " + graph.vertexCount + "): from=" + from + ", to=" + to);
                }
                graph.addEdge(from, to);
                return this;
            }

            DirectedGraph build() {
                return graph;
            }
        }
    }

    static class CycleDetector {
        static boolean hasCycle(DirectedGraph graph) {
            int n = graph.vertexCount();
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

            int processedCount = 0;
            while (!queue.isEmpty()) {
                int u = queue.poll();
                processedCount++;
                for (int v : graph.neighbors(u)) {
                    if (--inDegree[v] == 0) {
                        queue.add(v);
                    }
                }
            }

            return processedCount != n;
        }
    }

    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph.Builder(4)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 0)
            .edge(2, 3)
            .build();

        System.out.println(CycleDetector.hasCycle(graph));
    }
}
