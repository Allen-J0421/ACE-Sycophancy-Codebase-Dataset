import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    static class Graph {
        private final int numVertices;
        private final List<List<int[]>> adjacencyList;

        Graph(int numVertices) {
            this.numVertices = numVertices;
            adjacencyList = new ArrayList<>(numVertices);
            for (int i = 0; i < numVertices; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        void addEdge(int u, int v, int weight) {
            adjacencyList.get(u).add(new int[]{v, weight});
            adjacencyList.get(v).add(new int[]{u, weight});
        }

        List<int[]> neighbors(int vertex) {
            return adjacencyList.get(vertex);
        }

        int size() {
            return numVertices;
        }
    }

    static int[] shortestPaths(Graph graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Priority queue entries: [distance, vertex]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] entry = pq.poll();
            int currentDist = entry[0];
            int u = entry[1];

            if (currentDist > dist[u]) continue;

            for (int[] edge : graph.neighbors(u)) {
                int v = edge[0];
                int weight = edge[1];
                int newDist = dist[u] + weight;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);

        int[] distances = shortestPaths(graph, 0);
        for (int d : distances) {
            System.out.print(d + " ");
        }
        System.out.println();
    }
}
