import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class Edge {
    final int to;
    final int weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Graph {
    private final List<List<Edge>> adjacencyList;

    Graph(int numVertices) {
        adjacencyList = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v, int weight) {
        adjacencyList.get(u).add(new Edge(v, weight));
        adjacencyList.get(v).add(new Edge(u, weight));
    }

    List<Edge> neighbors(int vertex) {
        return adjacencyList.get(vertex);
    }

    int size() {
        return adjacencyList.size();
    }
}

class Dijkstra {

    private Dijkstra() {}

    private static class QueueEntry implements Comparable<QueueEntry> {
        final int distance;
        final int vertex;

        QueueEntry(int distance, int vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }

        @Override
        public int compareTo(QueueEntry other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    static int[] shortestPaths(Graph graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<QueueEntry> pq = new PriorityQueue<>();
        pq.offer(new QueueEntry(0, source));

        while (!pq.isEmpty()) {
            QueueEntry entry = pq.poll();
            if (entry.distance > dist[entry.vertex]) continue;

            for (Edge edge : graph.neighbors(entry.vertex)) {
                int newDist = dist[entry.vertex] + edge.weight;
                if (newDist < dist[edge.to]) {
                    dist[edge.to] = newDist;
                    pq.offer(new QueueEntry(newDist, edge.to));
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
