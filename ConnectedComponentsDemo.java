import java.util.List;

final class ConnectedComponentsDemo {

    private ConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);

        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(5, 4);

        List<List<Integer>> components = ConnectedComponents.findComponents(graph);
        printComponents(components);
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            System.out.println(joinIntegers(component));
        }
    }

    private static String joinIntegers(List<Integer> values) {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                line.append(' ');
            }
            line.append(values.get(index));
        }
        return line.toString();
    }
}
