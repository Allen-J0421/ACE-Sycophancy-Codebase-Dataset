package graph.metrics;

import graph.core.IGraph;

import java.util.HashMap;
import java.util.Map;

public class GraphMetrics {
    private final IGraph graph;
    private final int[] degreeSequence;
    private final Map<Integer, Integer> degreeFrequency;
    private double density;
    private double avgDegree;
    private int diameter;
    private int radius;

    public GraphMetrics(IGraph graph) {
        this.graph = graph;
        this.degreeSequence = new int[graph.getVertexCount()];
        this.degreeFrequency = new HashMap<>();
        calculateMetrics();
    }

    private void calculateMetrics() {
        int maxDegree = 0;
        int minDegree = Integer.MAX_VALUE;
        int totalDegree = 0;

        for (int v = 0; v < graph.getVertexCount(); v++) {
            int degree = graph.getDegree(v);
            degreeSequence[v] = degree;
            totalDegree += degree;
            maxDegree = Math.max(maxDegree, degree);
            minDegree = Math.min(minDegree, degree);
            degreeFrequency.put(degree, degreeFrequency.getOrDefault(degree, 0) + 1);
        }

        if (graph.getVertexCount() > 0) {
            avgDegree = (double) totalDegree / graph.getVertexCount();
        }

        int maxEdges = graph.getVertexCount() * (graph.getVertexCount() - 1) / 2;
        density = maxEdges > 0 ? (double) graph.getEdgeCount() / maxEdges : 0;

        calculateDiameterAndRadius();
    }

    private void calculateDiameterAndRadius() {
        diameter = 0;
        radius = Integer.MAX_VALUE;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            int eccentricity = calculateEccentricity(i);
            diameter = Math.max(diameter, eccentricity);
            if (eccentricity < radius) {
                radius = eccentricity;
            }
        }
    }

    private int calculateEccentricity(int vertex) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        int maxDist = 0;
        maxDist = bfsDistance(vertex, visited);
        return maxDist;
    }

    private int bfsDistance(int start, boolean[] visited) {
        boolean[] localVisited = new boolean[graph.getVertexCount()];
        int[] distances = new int[graph.getVertexCount()];
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();

        queue.offer(start);
        localVisited[start] = true;

        int maxDistance = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : graph.getNeighbors(v)) {
                if (!localVisited[neighbor]) {
                    localVisited[neighbor] = true;
                    distances[neighbor] = distances[v] + 1;
                    maxDistance = Math.max(maxDistance, distances[neighbor]);
                    queue.offer(neighbor);
                }
            }
        }
        return maxDistance;
    }

    public double getDensity() {
        return density;
    }

    public double getAverageDegree() {
        return avgDegree;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getRadius() {
        return radius;
    }

    public int[] getDegreeSequence() {
        return degreeSequence.clone();
    }

    public Map<Integer, Integer> getDegreeFrequency() {
        return new HashMap<>(degreeFrequency);
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════╗\n");
        sb.append("║         GRAPH METRICS REPORT               ║\n");
        sb.append("╚════════════════════════════════════════════╝\n");
        sb.append("\nBasic Properties:\n");
        sb.append("  Vertices: ").append(graph.getVertexCount()).append("\n");
        sb.append("  Edges: ").append(graph.getEdgeCount()).append("\n");
        sb.append("\nDensity & Degree:\n");
        sb.append("  Density: ").append(String.format("%.4f", density)).append("\n");
        sb.append("  Avg Degree: ").append(String.format("%.2f", avgDegree)).append("\n");
        sb.append("\nStructural Properties:\n");
        sb.append("  Diameter: ").append(diameter).append("\n");
        sb.append("  Radius: ").append(radius).append("\n");
        sb.append("  Degree Frequency: ").append(degreeFrequency).append("\n");
        return sb.toString();
    }
}
