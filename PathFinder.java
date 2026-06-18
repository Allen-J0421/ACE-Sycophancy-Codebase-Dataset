import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PathFinder {
    private final IGraph graph;
    private final boolean[] visited;
    private final int[] parent;

    public PathFinder(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }
    }

    public Optional<List<Integer>> findPath(int source, int destination) {
        if (source < 0 || source >= graph.getVertexCount() ||
            destination < 0 || destination >= graph.getVertexCount()) {
            throw new IndexOutOfBoundsException("Invalid vertex indices");
        }

        if (source == destination) {
            List<Integer> path = new ArrayList<>();
            path.add(source);
            return Optional.of(path);
        }

        resetVisited();
        if (dfsPath(source, destination)) {
            return Optional.of(reconstructPath(source, destination));
        }
        return Optional.empty();
    }

    public int getDistance(int source, int destination) {
        return findPath(source, destination)
                .map(path -> path.size() - 1)
                .orElse(-1);
    }

    private boolean dfsPath(int vertex, int destination) {
        visited[vertex] = true;

        if (vertex == destination) {
            return true;
        }

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                parent[neighbor] = vertex;
                if (dfsPath(neighbor, destination)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<Integer> reconstructPath(int source, int destination) {
        List<Integer> path = new ArrayList<>();
        int current = destination;

        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        Collections.reverse(path);
        return path;
    }

    private void resetVisited() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
            parent[i] = -1;
        }
    }
}
