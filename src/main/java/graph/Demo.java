package graph;

import java.util.List;
import java.util.stream.Collectors;

/** Demonstrates {@link ConnectedComponentsFinder} on a small sample graph. */
public final class Demo {

    private Demo() {
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        Components components = new ConnectedComponentsFinder().find(graph);

        for (List<Integer> component : components.asList()) {
            System.out.println(component.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
        }

        System.out.println("components: " + components.count());
        System.out.println("0 and 1 connected? " + components.connected(0, 1));
        System.out.println("0 and 4 connected? " + components.connected(0, 4));
    }
}
