package graph.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MinimumCut {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private final Set<Integer> sourcePartition;
    private final List<String> cutEdges;
    private double cutValue;

    public MinimumCut(FlowNetwork network, int source, int sink) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.sourcePartition = new HashSet<>();
        this.cutEdges = new ArrayList<>();
        this.cutValue = 0;
        computeMinimumCut();
    }

    private void computeMinimumCut() {
        findReachableVertices();
        findCutEdges();
    }

    private void findReachableVertices() {
        int n = network.getVertexCount();
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;
        sourcePartition.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : network.getAdjacent(u)) {
                if (!visited[v] && network.getResidualCapacity(u, v) > 0) {
                    visited[v] = true;
                    sourcePartition.add(v);
                    queue.offer(v);
                }
            }
        }
    }

    private void findCutEdges() {
        for (int u : sourcePartition) {
            for (int v : network.getAdjacent(u)) {
                if (!sourcePartition.contains(v)) {
                    double flow = network.getFlow(u, v);
                    if (flow > 0) {
                        cutEdges.add(u + " -> " + v + ": " + String.format("%.1f", flow));
                        cutValue += flow;
                    }
                }
            }
        }
    }

    public Set<Integer> getSourcePartition() {
        return new HashSet<>(sourcePartition);
    }

    public Set<Integer> getSinkPartition() {
        Set<Integer> sinkPartition = new HashSet<>();
        for (int i = 0; i < network.getVertexCount(); i++) {
            if (!sourcePartition.contains(i)) {
                sinkPartition.add(i);
            }
        }
        return sinkPartition;
    }

    public List<String> getCutEdges() {
        return new ArrayList<>(cutEdges);
    }

    public double getCutValue() {
        return cutValue;
    }

    public int getCutSize() {
        return cutEdges.size();
    }
}
