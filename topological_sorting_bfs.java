import java.util.List;

class CycleDetectedException extends RuntimeException {
    CycleDetectedException() {
        super("Graph contains a cycle and cannot be topologically sorted");
    }
}

class TopologicalSortService {
    private final TopologicalSortStrategy strategy;

    private TopologicalSortService(TopologicalSortStrategy strategy) {
        this.strategy = strategy;
    }

    static TopologicalSortService of() {
        return new TopologicalSortService(new KahnTopologicalSortStrategy());
    }

    static TopologicalSortService of(TopologicalSortStrategy strategy) {
        return new TopologicalSortService(strategy);
    }

    List<Integer> sort(GraphView graph) {
        return strategy.sort(graph);
    }
}

class TopologicalSort {

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 1);
        graph.addEdge(5, 2);

        List<Integer> res = TopologicalSortService.of().sort(graph);
        for (int vertex : res) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
