import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    void addDirectedEdge(int from, int to, int weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
    }

    void addEdge(int u, int v, int weight) {
        addDirectedEdge(u, v, weight);
        addDirectedEdge(v, u, weight);
    }

    List<Edge> neighbors(int vertex) {
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    int size() {
        return adjacencyList.size();
    }
}

class ShortestPathResult {
    private final int[] dist;
    private final int[] prev;
    private final int source;

    ShortestPathResult(int[] dist, int[] prev, int source) {
        this.dist = dist;
        this.prev = prev;
        this.source = source;
    }

    int distanceTo(int vertex) {
        return dist[vertex];
    }

    boolean isReachable(int vertex) {
        return dist[vertex] != Integer.MAX_VALUE;
    }

    List<Integer> pathTo(int vertex) {
        if (!isReachable(vertex)) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        for (int v = vertex; v != source; v = prev[v]) {
            path.add(v);
        }
        path.add(source);
        Collections.reverse(path);
        return path;
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

    static ShortestPathResult shortestPaths(Graph graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
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
                    prev[edge.to] = entry.vertex;
                    pq.offer(new QueueEntry(newDist, edge.to));
                }
            }
        }

        return new ShortestPathResult(dist, prev, source);
    }

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);

        ShortestPathResult result = shortestPaths(graph, 0);
        for (int v = 0; v < graph.size(); v++) {
            System.out.println("vertex " + v + ": distance=" + result.distanceTo(v) + "  path=" + result.pathTo(v));
        }
    }
}
