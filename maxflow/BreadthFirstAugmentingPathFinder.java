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
                        int[] vertices = buildPath(parent, source, sink);
                        return Optional.of(new AugmentingPath(
                            source,
                            sink,
                            vertices,
                            bottleneckCapacity(residualNetwork, vertices)));
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
        int[] vertices
    ) {
        int pathFlow = Integer.MAX_VALUE;

        for (int index = 0; index < vertices.length - 1; index++) {
            int from = vertices[index];
            int to = vertices[index + 1];
            pathFlow = Math.min(pathFlow, residualNetwork.residualCapacity(from, to));
        }

        return pathFlow;
    }

    private static int[] buildPath(int[] parent, int source, int sink) {
        int length = 1;
        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            length++;
        }

        int[] vertices = new int[length];
        int index = length - 1;
        for (int vertex = sink; ; vertex = parent[vertex]) {
            vertices[index--] = vertex;
            if (vertex == source) {
                break;
            }
        }

        return vertices;
    }
}
