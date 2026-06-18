import java.util.List;
import java.util.StringJoiner;

public final class DepthFirstSearchDemo {

    private DepthFirstSearchDemo() {
    }

    public static void main(String[] args) {
        Graph graph = Graph.builder(6)
                .addUndirectedEdge(1, 2)
                .addUndirectedEdge(0, 3)
                .addUndirectedEdge(2, 0)
                .addUndirectedEdge(5, 4)
                .build();

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
