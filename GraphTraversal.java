import java.util.List;

public interface GraphTraversal {
    List<Integer> traverse(IGraph graph);

    default TraversalStats traverseWithStats(IGraph graph) {
        long startTime = System.currentTimeMillis();
        List<Integer> result = traverse(graph);
        long endTime = System.currentTimeMillis();
        return new TraversalStats(getAlgorithmName(), result, endTime - startTime);
    }

    String getAlgorithmName();
}
