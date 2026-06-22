import java.util.List;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<List<Integer>> components = ConnectedComponents.find(graph);

        for (List<Integer> component : components) {
            System.out.println(component.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
        }
    }
}
