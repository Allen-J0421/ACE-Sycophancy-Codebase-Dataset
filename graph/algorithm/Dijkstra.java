package graph.algorithm;

import graph.model.IWeightedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    private final IWeightedGraph graph;
    private final double[] distances;
    private final int[] parent;
    private final boolean[] visited;

    public Dijkstra(IWeightedGraph graph) {
        this.graph = graph;
        this.distances = new double[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
        this.visited = new boolean[graph.getVertexCount()];
    }

    public void computeShortestPaths(int source) {
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        distances[source] = 0;

        PriorityQueue<VertexDistance> pq = new PriorityQueue<>();
        pq.offer(new VertexDistance(source, 0));

        while (!pq.isEmpty()) {
            VertexDistance vd = pq.poll();
            int u = vd.vertex;

            if (visited[u]) {
                continue;
            }

            visited[u] = true;

            for (int v = 0; v < graph.getVertexCount(); v++) {
                if (!visited[v] && graph.hasEdge(u, v)) {
                    double weight = graph.getEdgeWeight(u, v);
                    double newDistance = distances[u] + weight;

                    if (newDistance < distances[v]) {
                        distances[v] = newDistance;
                        parent[v] = u;
                        pq.offer(new VertexDistance(v, newDistance));
                    }
                }
            }
        }
    }

    public double getDistance(int vertex) {
        if (vertex < 0 || vertex >= graph.getVertexCount()) {
            throw new IndexOutOfBoundsException("Invalid vertex");
        }
        return distances[vertex];
    }

    public List<Integer> getPath(int destination) {
        List<Integer> path = new ArrayList<>();
        int current = destination;

        while (current != -1) {
            path.add(0, current);
            current = parent[current];
        }

        if (distances[destination] == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }

        return path;
    }

    public boolean hasPathTo(int vertex) {
        return distances[vertex] != Double.POSITIVE_INFINITY;
    }

    private static class VertexDistance implements Comparable<VertexDistance> {
        int vertex;
        double distance;

        VertexDistance(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(VertexDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
