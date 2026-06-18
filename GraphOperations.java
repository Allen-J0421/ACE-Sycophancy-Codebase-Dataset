import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class GraphOperations {
    private final Graph graph;

    public GraphOperations(Graph graph) {
        this.graph = graph;
    }

    public boolean hasCycle() {
        boolean[] visited = new boolean[graph.getVertexCount()];
        boolean[] recStack = new boolean[graph.getVertexCount()];

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                if (hasCycleDFS(i, visited, recStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycleDFS(int vertex, boolean[] visited, boolean[] recStack) {
        visited[vertex] = true;
        recStack[vertex] = true;

        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                if (hasCycleDFS(neighbor, visited, recStack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                return true;
            }
        }

        recStack[vertex] = false;
        return false;
    }

    public List<Integer> findIsolatedVertices() {
        List<Integer> isolated = new ArrayList<>();
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (graph.getAdjacent(i).isEmpty()) {
                isolated.add(i);
            }
        }
        return isolated;
    }

    public boolean isConnected() {
        if (graph.getVertexCount() == 0) return true;

        boolean[] visited = new boolean[graph.getVertexCount()];
        dfs(0, visited);

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private void dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;
        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    public int getMaximumDistance() {
        int maxDistance = 0;

        for (int start = 0; start < graph.getVertexCount(); start++) {
            int[] distances = bfsDistances(start);
            for (int dist : distances) {
                if (dist > maxDistance && dist != Integer.MAX_VALUE) {
                    maxDistance = dist;
                }
            }
        }

        return maxDistance;
    }

    private int[] bfsDistances(int start) {
        int[] distances = new int[graph.getVertexCount()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Integer.MAX_VALUE;
        }

        distances[start] = 0;
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : graph.getAdjacent(current)) {
                if (distances[neighbor] == Integer.MAX_VALUE) {
                    distances[neighbor] = distances[current] + 1;
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    public Set<Integer> getCenterVertices() {
        int eccentricity = getMaximumDistance();
        Set<Integer> centers = new HashSet<>();

        for (int start = 0; start < graph.getVertexCount(); start++) {
            int[] distances = bfsDistances(start);
            int maxDist = 0;
            for (int dist : distances) {
                if (dist != Integer.MAX_VALUE) {
                    maxDist = Math.max(maxDist, dist);
                }
            }

            if (maxDist < eccentricity) {
                centers.add(start);
            }
        }

        return centers;
    }

    public int countTriangles() {
        int triangles = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            List<Integer> neighbors = new ArrayList<>(graph.getAdjacent(i));

            for (int j = 0; j < neighbors.size(); j++) {
                for (int k = j + 1; k < neighbors.size(); k++) {
                    int u = neighbors.get(j);
                    int v = neighbors.get(k);

                    if (graph.getAdjacent(u).contains(v)) {
                        triangles++;
                    }
                }
            }
        }

        return triangles / 3;
    }

    public double getDensity() {
        int v = graph.getVertexCount();
        if (v < 2) return 0;

        int edgeCount = 0;
        for (int i = 0; i < v; i++) {
            edgeCount += graph.getAdjacent(i).size();
        }
        edgeCount /= 2;

        int maxEdges = (v * (v - 1)) / 2;
        return (double) edgeCount / maxEdges;
    }
}
