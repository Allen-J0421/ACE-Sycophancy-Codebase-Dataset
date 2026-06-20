import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class DetectCycle {

    static class DirectedGraph {
        private final int vertices;
        private final List<List<Integer>> adjacency;

        private DirectedGraph(int vertices) {
            this.vertices = vertices;
            this.adjacency = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        void addEdge(int from, int to) {
            adjacency.get(from).add(to);
        }

        int vertexCount() {
            return vertices;
        }

        List<Integer> neighbors(int vertex) {
            return adjacency.get(vertex);
        }

        static class Builder {
            private final DirectedGraph graph;

            Builder(int vertices) {
                this.graph = new DirectedGraph(vertices);
            }

            Builder edge(int from, int to) {
                graph.addEdge(from, to);
                return this;
            }

            DirectedGraph build() {
                return graph;
            }
        }
    }

    static class CycleDetector {
        boolean hasCycle(DirectedGraph graph) {
            int V = graph.vertexCount();
            int[] inDegree = new int[V];

            for (int u = 0; u < V; u++) {
                for (int v : graph.neighbors(u)) {
                    inDegree[v]++;
                }
            }

            Queue<Integer> queue = new LinkedList<>();
            for (int u = 0; u < V; u++) {
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

            return visited != V;
        }
    }

    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph.Builder(4)
            .edge(0, 1)
            .edge(1, 2)
            .edge(2, 0)
            .edge(2, 3)
            .build();

        boolean cyclic = new CycleDetector().hasCycle(graph);
        System.out.println(cyclic ? "true" : "false");
    }
}
