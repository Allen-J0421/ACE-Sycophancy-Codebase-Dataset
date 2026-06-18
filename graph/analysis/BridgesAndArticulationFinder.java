package graph.analysis;

import graph.core.IGraph;
import graph.model.Edge;

import java.util.ArrayList;
import java.util.List;

public class BridgesAndArticulationFinder {
    private final IGraph graph;
    private final boolean[] visited;
    private final int[] disc;
    private final int[] low;
    private final int[] parent;
    private int time;
    private final List<Edge> bridges;
    private final List<Integer> articulationPoints;

    public BridgesAndArticulationFinder(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.disc = new int[graph.getVertexCount()];
        this.low = new int[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
        this.time = 0;
        this.bridges = new ArrayList<>();
        this.articulationPoints = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            parent[i] = -1;
        }

        findCriticalEdges();
    }

    private void findCriticalEdges() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfsFindBridges(i);
            }
        }
    }

    private void dfsFindBridges(int u) {
        visited[u] = true;
        disc[u] = low[u] = time++;
        int children = 0;

        for (int v : graph.getNeighbors(u)) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                dfsFindBridges(v);

                low[u] = Math.min(low[u], low[v]);

                if (low[v] > disc[u]) {
                    bridges.add(new Edge(u, v, 1.0));
                }

                if ((parent[u] == -1 && children > 1) || (parent[u] != -1 && low[v] >= disc[u])) {
                    if (!articulationPoints.contains(u)) {
                        articulationPoints.add(u);
                    }
                }
            } else if (v != parent[u]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    public List<Edge> getBridges() {
        return bridges;
    }

    public List<Integer> getArticulationPoints() {
        return articulationPoints;
    }

    public int getBridgeCount() {
        return bridges.size();
    }

    public int getArticulationPointCount() {
        return articulationPoints.size();
    }

    public boolean isBridge(int u, int v) {
        for (Edge bridge : bridges) {
            if ((bridge.getSource() == u && bridge.getDestination() == v) ||
                (bridge.getSource() == v && bridge.getDestination() == u)) {
                return true;
            }
        }
        return false;
    }

    public boolean isArticulationPoint(int vertex) {
        return articulationPoints.contains(vertex);
    }
}
