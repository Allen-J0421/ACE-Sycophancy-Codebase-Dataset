import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

final class BipartiteChecker {

    private BipartiteChecker() {
    }

    static boolean isBipartite(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        Partition[] partitions = new Partition[vertexCount];

        for (int startVertex = 0; startVertex < vertexCount; startVertex++) {
            if (partitions[startVertex] == null && !colorComponent(startVertex, graph, partitions)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(int startVertex, Graph graph, Partition[] partitions) {
        Deque<Integer> queue = new ArrayDeque<>();
        partitions[startVertex] = Partition.LEFT;
        queue.addLast(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            Partition currentPartition = partitions[currentVertex];
            Partition oppositePartition = currentPartition.opposite();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                Partition neighborPartition = partitions[neighbor];
                if (neighborPartition == null) {
                    partitions[neighbor] = oppositePartition;
                    queue.addLast(neighbor);
                    continue;
                }

                if (neighborPartition == currentPartition) {
                    return false;
                }
            }
        }

        return true;
    }

    private enum Partition {
        LEFT,
        RIGHT;

        Partition opposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }
}
