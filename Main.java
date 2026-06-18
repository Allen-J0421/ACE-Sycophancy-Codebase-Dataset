import java.util.List;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        Graph graph = new Graph.Builder(6)
            .addEdge(1, 2)
            .addEdge(2, 0)
            .addEdge(0, 3)
            .addEdge(4, 5)
            .build();

        List<Integer> result = BreadthFirstSearch.traverse(graph);
        System.out.println(result.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(" ")));
    }
}
