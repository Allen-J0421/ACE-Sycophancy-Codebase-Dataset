package graph.metrics;

import graph.core.IGraph;

public class AdvancedMetrics {
    private final IGraph graph;
    private double clusteringCoefficient;
    private double averagePathLength;
    private double assortativity;

    public AdvancedMetrics(IGraph graph) {
        this.graph = graph;
        calculateMetrics();
    }

    private void calculateMetrics() {
        calculateClusteringCoefficient();
        calculateAveragePathLength();
        calculateAssortativity();
    }

    private void calculateClusteringCoefficient() {
        double sum = 0;
        int count = 0;

        for (int v = 0; v < graph.getVertexCount(); v++) {
            int degree = graph.getDegree(v);
            if (degree < 2) continue;

            int edges = 0;
            java.util.List<Integer> neighbors = graph.getNeighbors(v);
            for (int i = 0; i < neighbors.size(); i++) {
                for (int j = i + 1; j < neighbors.size(); j++) {
                    if (graph.hasEdge(neighbors.get(i), neighbors.get(j))) {
                        edges++;
                    }
                }
            }

            double maxEdges = (double) degree * (degree - 1) / 2;
            sum += edges / maxEdges;
            count++;
        }

        clusteringCoefficient = count > 0 ? sum / count : 0;
    }

    private void calculateAveragePathLength() {
        double sum = 0;
        int count = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            int[] distances = bfsDistances(i);
            for (int j = i + 1; j < graph.getVertexCount(); j++) {
                if (distances[j] != Integer.MAX_VALUE) {
                    sum += distances[j];
                    count++;
                }
            }
        }

        averagePathLength = count > 0 ? sum / count : 0;
    }

    private void calculateAssortativity() {
        double sumProd = 0;
        double sumDeg2 = 0;
        int edges = 0;

        for (int u = 0; u < graph.getVertexCount(); u++) {
            for (int v : graph.getNeighbors(u)) {
                if (u < v) {
                    sumProd += graph.getDegree(u) * graph.getDegree(v);
                    sumDeg2 += graph.getDegree(u) + graph.getDegree(v);
                    edges++;
                }
            }
        }

        assortativity = edges > 0 ? sumProd / edges : 0;
    }

    private int[] bfsDistances(int start) {
        int[] distances = new int[graph.getVertexCount()];
        java.util.Arrays.fill(distances, Integer.MAX_VALUE);

        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        distances[start] = 0;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : graph.getNeighbors(v)) {
                if (distances[neighbor] == Integer.MAX_VALUE) {
                    distances[neighbor] = distances[v] + 1;
                    queue.offer(neighbor);
                }
            }
        }

        return distances;
    }

    public double getClusteringCoefficient() {
        return clusteringCoefficient;
    }

    public double getAveragePathLength() {
        return averagePathLength;
    }

    public double getAssortativity() {
        return assortativity;
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════╗\n");
        sb.append("║    ADVANCED GRAPH METRICS REPORT           ║\n");
        sb.append("╚════════════════════════════════════════════╝\n");
        sb.append("Clustering Coefficient: ").append(String.format("%.4f", clusteringCoefficient)).append("\n");
        sb.append("Average Path Length:    ").append(String.format("%.4f", averagePathLength)).append("\n");
        sb.append("Assortativity:          ").append(String.format("%.4f", assortativity)).append("\n");
        return sb.toString();
    }
}
