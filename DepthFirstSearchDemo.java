import java.util.List;
import java.util.StringJoiner;

public final class DepthFirstSearchDemo {

    private DepthFirstSearchDemo() {
    }

    public static void main(String[] args) {
        Graph graph = Graph.withVertices(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(5, 4);

        System.out.println(formatTraversal(DepthFirstSearch.dfs(graph)));
    }

    private static String formatTraversal(List<Integer> traversal) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : traversal) {
            joiner.add(Integer.toString(vertex));
        }
        return joiner.toString();
    }
}
