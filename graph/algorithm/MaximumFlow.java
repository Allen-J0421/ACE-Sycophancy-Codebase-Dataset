package graph.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MaximumFlow {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private double maxFlow;

    public MaximumFlow(FlowNetwork network, int source, int sink) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.maxFlow = 0;
        computeMaxFlow();
    }

    private void computeMaxFlow() {
        while (hasAugmentingPath()) {
            double pathFlow = findMinCapacityOnPath();
            augmentPath(pathFlow);
            maxFlow += pathFlow;
        }
    }

    private boolean hasAugmentingPath() {
        int n = network.getVertexCount();
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : network.getAdjacent(u)) {
                if (!visited[v] && network.getResidualCapacity(u, v) > 0) {
                    visited[v] = true;
                    queue.offer(v);
                    if (v == sink) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private double findMinCapacityOnPath() {
        int n = network.getVertexCount();
        int[] parent = new int[n];
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : network.getAdjacent(u)) {
                if (!visited[v] && network.getResidualCapacity(u, v) > 0) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.offer(v);
                }
            }
        }

        double minCapacity = Double.MAX_VALUE;
        int current = sink;
        while (parent[current] != -1) {
            int prev = parent[current];
            minCapacity = Math.min(minCapacity, network.getResidualCapacity(prev, current));
            current = prev;
        }
        return minCapacity;
    }

    private void augmentPath(double flow) {
        int n = network.getVertexCount();
        int[] parent = new int[n];
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : network.getAdjacent(u)) {
                if (!visited[v] && network.getResidualCapacity(u, v) > 0) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.offer(v);
                }
            }
        }

        int current = sink;
        while (parent[current] != -1) {
            int prev = parent[current];
            network.setFlow(prev, current, network.getFlow(prev, current) + flow);
            network.setFlow(current, prev, network.getFlow(current, prev) - flow);
            current = prev;
        }
    }

    public double getMaxFlow() {
        return maxFlow;
    }

    public List<String> getFlowEdges() {
        List<String> edges = new ArrayList<>();
        for (var entry : network.getFlowMap().entrySet()) {
            if (entry.getValue() > 0) {
                edges.add(entry.getKey() + ": " + String.format("%.1f", entry.getValue()));
            }
        }
        return edges;
    }
}
