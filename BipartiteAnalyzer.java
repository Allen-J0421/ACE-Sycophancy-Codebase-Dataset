import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

final class BipartiteAnalyzer {

    private BipartiteAnalyzer() {
    }

    static boolean isBipartite(UndirectedGraph graph) {
        Objects.requireNonNull(graph, "graph");

        Coloring coloring = new Coloring(graph.vertexCount());

        for (int startVertex = 0; startVertex < coloring.size(); startVertex++) {
            if (coloring.isUncolored(startVertex) && !colorComponent(startVertex, graph, coloring)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(int startVertex, UndirectedGraph graph, Coloring coloring) {
        Deque<Integer> queue = new ArrayDeque<>();
        coloring.assign(startVertex, Partition.LEFT);
        queue.addLast(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            Partition currentPartition = coloring.partitionOf(currentVertex);
            Partition oppositePartition = currentPartition.opposite();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (coloring.isUncolored(neighbor)) {
                    coloring.assign(neighbor, oppositePartition);
                    queue.addLast(neighbor);
                    continue;
                }

                if (coloring.partitionOf(neighbor) == currentPartition) {
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

    private static final class Coloring {
        private final Partition[] partitions;

        private Coloring(int vertexCount) {
            this.partitions = new Partition[vertexCount];
        }

        int size() {
            return partitions.length;
        }

        boolean isUncolored(int vertex) {
            return partitions[vertex] == null;
        }

        Partition partitionOf(int vertex) {
            return partitions[vertex];
        }

        void assign(int vertex, Partition partition) {
            partitions[vertex] = partition;
        }
    }
}
