package maxflow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;

public final class BreadthFirstAugmentingPathFinder implements AugmentingPathFinder {
    @Override
    public Optional<AugmentingPath> findPath(ResidualNetwork residualNetwork, int source, int sink) {
        boolean[] visited = new boolean[residualNetwork.vertexCount()];
        int[] parent = new int[residualNetwork.vertexCount()];
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
                        return Optional.of(
                            new AugmentingPath(source, sink, parent, bottleneckCapacity(
                                residualNetwork,
                                parent,
                                source,
                                sink)));
                    }

                    visited[nextVertex] = true;
                    queue.addLast(nextVertex);
                }
            }
        }

        return Optional.empty();
    }

    private static int bottleneckCapacity(
        ResidualNetwork residualNetwork,
        int[] parent,
        int source,
        int sink
    ) {
        int pathFlow = Integer.MAX_VALUE;
        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            pathFlow = Math.min(pathFlow, residualNetwork.residualCapacity(predecessor, vertex));
        }

        return pathFlow;
    }
}
