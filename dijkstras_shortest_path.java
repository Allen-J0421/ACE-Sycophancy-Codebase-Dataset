import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class Edge {
    int destination;
    int weight;

    Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

class Graph {
    private List<List<Edge>> adjacencyList;

    Graph(int vertexCount) {
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(int source, int destination, int weight) {
        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
    }

    List<List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    int getVertexCount() {
        return adjacencyList.size();
    }
}

class QueueEntry implements Comparable<QueueEntry> {
    int distance;
    int node;

    QueueEntry(int distance, int node) {
        this.distance = distance;
        this.node = node;
    }

    @Override
    public int compareTo(QueueEntry other) {
        return Integer.compare(this.distance, other.distance);
    }
}

class ShortestPathSolver {
    static List<Integer> solve(Graph graph, int sourceNode) {
        int vertexCount = graph.getVertexCount();
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, Integer.MAX_VALUE);

        PriorityQueue<QueueEntry> priorityQueue = new PriorityQueue<>();
        distances[sourceNode] = 0;
        priorityQueue.offer(new QueueEntry(0, sourceNode));

        while (!priorityQueue.isEmpty()) {
            QueueEntry current = priorityQueue.poll();
            int currentDistance = current.distance;
            int currentNode = current.node;

            if (currentDistance > distances[currentNode])
                continue;

            for (Edge edge : graph.getAdjacencyList().get(currentNode)) {
                int neighbor = edge.destination;
                int weight = edge.weight;
                int newDistance = distances[currentNode] + weight;

                if (newDistance < distances[neighbor]) {
                    distances[neighbor] = newDistance;
                    priorityQueue.offer(new QueueEntry(newDistance, neighbor));
                }
            }
        }

        return Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        );
    }
}

class Main {
    public static void main(String[] args) {
        int vertexCount = 5;
        int sourceNode = 0;

        Graph graph = new Graph(vertexCount);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);

        List<Integer> result = ShortestPathSolver.solve(graph, sourceNode);
        printDistances(result);
    }

    private static void printDistances(List<Integer> distances) {
        for (int distance : distances) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }
}
