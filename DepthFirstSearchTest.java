import java.util.ArrayList;
import java.util.List;

/**
 * A small, dependency-free test runner for {@link DepthFirstSearch} and the
 * {@link DfsVisitor} hooks. Run with {@code java DepthFirstSearchTest}; it prints a
 * summary and exits with a non-zero status if any test fails.
 */
public final class DepthFirstSearchTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        DepthFirstSearchTest tests = new DepthFirstSearchTest();
        tests.run();
        System.out.printf("%n%d passed, %d failed%n", tests.passed, tests.failed);
        if (tests.failed > 0) {
            System.exit(1);
        }
    }

    private void run() {
        directedTraversalReportsHooksInOrder();
        undirectedTreeEdgesEqualVertexCountMinusComponents();
        customVisitorCanDetectDirectedCycles();
    }

    /** A visitor that records the sequence and counts of every hook. */
    private static final class RecordingVisitor implements DfsVisitor {
        final List<Integer> discovered = new ArrayList<>();
        final List<Integer> finished = new ArrayList<>();
        int treeEdges;
        int nonTreeEdges;

        @Override public void discoverVertex(int v) {
            discovered.add(v);
        }

        @Override public void treeEdge(int from, int to, Edge edge) {
            treeEdges++;
        }

        @Override public void nonTreeEdge(int from, int to, Edge edge) {
            nonTreeEdges++;
        }

        @Override public void finishVertex(int v) {
            finished.add(v);
        }
    }

    private void directedTraversalReportsHooksInOrder() {
        // 0 -> 1 -> 2 and 0 -> 2. DFS from 0 follows 0->1->2, then sees 0->2 as a
        // non-tree edge.
        Graph graph = GraphBuilder.directed().edge(0, 1).edge(1, 2).edge(0, 2).build();
        RecordingVisitor visitor = new RecordingVisitor();
        new DepthFirstSearch().traverse(graph, visitor);

        check("directed discovery order", visitor.discovered, List.of(0, 1, 2));
        check("directed finish order", visitor.finished, List.of(2, 1, 0));
        check("directed tree edges", visitor.treeEdges, 2);
        check("directed non-tree edges", visitor.nonTreeEdges, 1);
    }

    private void undirectedTreeEdgesEqualVertexCountMinusComponents() {
        // Two components: a triangle {0,1,2} and an edge {3,4}. A spanning forest
        // has (vertices - components) tree edges = 5 - 2 = 3.
        Graph graph = GraphBuilder.undirected()
                .edge(0, 1).edge(1, 2).edge(2, 0).edge(3, 4)
                .build();
        RecordingVisitor visitor = new RecordingVisitor();
        new DepthFirstSearch().traverse(graph, visitor);

        check("undirected discovers every vertex", visitor.discovered.size(), 5);
        check("undirected spanning-forest tree edges", visitor.treeEdges, 3);
    }

    private void customVisitorCanDetectDirectedCycles() {
        // Demonstrates a brand-new algorithm built only from the hooks, with no
        // change to the engine or the existing analyses: a directed graph has a
        // cycle iff some edge leads back to a vertex still on the DFS stack.
        check("cycle present", hasDirectedCycle(
                GraphBuilder.directed().edge(0, 1).edge(1, 2).edge(2, 0).build()), true);
        check("self-loop is a cycle", hasDirectedCycle(
                GraphBuilder.directed().edge(0, 0).build()), true);
        check("DAG has no cycle", hasDirectedCycle(
                GraphBuilder.directed().edge(0, 1).edge(1, 2).edge(0, 2).build()), false);
    }

    /** Detects a cycle in a directed graph using only the DFS visitor hooks. */
    private static boolean hasDirectedCycle(Graph graph) {
        boolean[] onStack = new boolean[graph.vertexCount()];
        boolean[] found = {false};
        DfsVisitor cycleDetector = new DfsVisitor() {
            @Override public void discoverVertex(int v) {
                onStack[v] = true;
            }

            @Override public void nonTreeEdge(int from, int to, Edge edge) {
                if (onStack[to]) {
                    found[0] = true;
                }
            }

            @Override public void finishVertex(int v) {
                onStack[v] = false;
            }
        };
        new DepthFirstSearch().traverse(graph, cycleDetector);
        return found[0];
    }

    private void check(String name, Object actual, Object expected) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected + " but was " + actual);
        }
    }
}
