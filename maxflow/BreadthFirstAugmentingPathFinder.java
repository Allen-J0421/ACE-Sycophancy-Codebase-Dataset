package maxflow;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class BreadthFirstAugmentingPathFinder implements AugmentingPathFinder {
    @Override
    public boolean findPath(ResidualNetwork residualNetwork, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[residualNetwork.vertexCount()];
        Arrays.fill(parent, -1);

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();

            for (int nextVertex = 0; nextVertex < residualNetwork.vertexCount(); nextVertex++) {
                if (!visited[nextVertex] && residualNetwork.hasResidualCapacity(currentVertex, nextVertex)) {
                    parent[nextVertex] = currentVertex;
                    if (nextVertex == sink) {
                        return true;
                    }

                    visited[nextVertex] = true;
                    queue.addLast(nextVertex);
                }
            }
        }

        return false;
    }
}
