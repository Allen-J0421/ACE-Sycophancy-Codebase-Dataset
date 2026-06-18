import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Self-contained test suite for {@link Graph} and {@link BreadthFirstSearch}.
 *
 * <p>Deliberately dependency-free: this repository compiles each algorithm
 * standalone with plain {@code javac}, so the suite uses a small built-in
 * assertion harness instead of an external framework. Run it with:
 *
 * <pre>{@code
 *   javac breadth_first_search.java breadth_first_search_test.java
 *   java BreadthFirstSearchTest
 * }</pre>
 *
 * <p>Each test is isolated; a failing assertion or unexpected exception fails
 * only its own case. The process exits non-zero if any test fails, so the suite
 * is usable as a CI gate.
 */
class BreadthFirstSearchTest {

    private static int run = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        test("sample graph yields known BFS order", BreadthFirstSearchTest::sampleGraphOrder);
        test("empty graph yields empty order", BreadthFirstSearchTest::emptyGraph);
        test("single isolated vertex is visited", BreadthFirstSearchTest::singleVertex);
        test("every disconnected component is visited", BreadthFirstSearchTest::disconnectedComponents);
        test("neighbors are explored in insertion order", BreadthFirstSearchTest::insertionOrder);
        test("a cycle visits each vertex exactly once", BreadthFirstSearchTest::cycleVisitsEachOnce);
        test("self loop does not revisit or hang", BreadthFirstSearchTest::selfLoop);
        test("addEdge creates an undirected edge", BreadthFirstSearchTest::undirectedEdges);
        test("neighbors() returns a read-only view", BreadthFirstSearchTest::neighborsUnmodifiable);
        test("vertexCount reflects construction", BreadthFirstSearchTest::vertexCount);
        // Generic-vertex behavior.
        test("BFS works over String vertices", BreadthFirstSearchTest::stringVertices);
        test("addEdge auto-creates missing vertices", BreadthFirstSearchTest::addEdgeCreatesVertices);
        test("vertices() preserves insertion order", BreadthFirstSearchTest::verticesInInsertionOrder);
        test("neighbors() of an absent vertex throws VertexNotFoundException", BreadthFirstSearchTest::neighborsAbsentVertexThrows);
        test("VertexNotFoundException is a GraphException", BreadthFirstSearchTest::vertexNotFoundIsAGraphException);
        test("null vertices are rejected", BreadthFirstSearchTest::nullVerticesRejected);
        test("graph is iterable over vertices in insertion order", BreadthFirstSearchTest::graphIsIterable);
        test("graph iterator is read-only", BreadthFirstSearchTest::graphIteratorReadOnly);
        test("forEachNeighbor visits neighbors in insertion order", BreadthFirstSearchTest::forEachNeighborOrder);
        test("forEachNeighbor returns true when not interrupted", BreadthFirstSearchTest::forEachNeighborCompletes);
        test("forEachNeighbor stops early and returns false", BreadthFirstSearchTest::forEachNeighborInterrupts);
        test("forEachNeighbor on an absent vertex throws", BreadthFirstSearchTest::forEachNeighborAbsentVertexThrows);
        test("forEachNeighbor rejects a null visitor", BreadthFirstSearchTest::forEachNeighborNullVisitor);

        System.out.printf("%n%d run, %d passed, %d failed%n", run, run - failed, failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    // ---------------------------------------------------------------- tests

    private static void sampleGraphOrder() {
        Graph<Integer> g = intGraph(6);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        g.addEdge(0, 3);
        g.addEdge(4, 5);
        assertEquals(Arrays.asList(0, 2, 3, 1, 4, 5), BreadthFirstSearch.bfs(g));
    }

    private static void emptyGraph() {
        assertEquals(Arrays.asList(), BreadthFirstSearch.bfs(new Graph<Integer>()));
    }

    private static void singleVertex() {
        assertEquals(Arrays.asList(0), BreadthFirstSearch.bfs(intGraph(1)));
    }

    private static void disconnectedComponents() {
        Graph<Integer> g = intGraph(5);
        g.addEdge(0, 1); // component {0,1}
        g.addEdge(2, 3); // component {2,3}
        // vertex 4 is isolated
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), BreadthFirstSearch.bfs(g));
    }

    private static void insertionOrder() {
        // Neighbors of 0 are added as 3, then 1, then 2; BFS must preserve that.
        Graph<Integer> g = intGraph(4);
        g.addEdge(0, 3);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        assertEquals(Arrays.asList(0, 3, 1, 2), BreadthFirstSearch.bfs(g));
    }

    private static void cycleVisitsEachOnce() {
        Graph<Integer> g = intGraph(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        List<Integer> order = BreadthFirstSearch.bfs(g);
        assertEquals(3, order.size());
        assertTrue("no duplicate visits", new HashSet<>(order).size() == order.size());
        assertEquals(new HashSet<>(Arrays.asList(0, 1, 2)), new HashSet<>(order));
    }

    private static void selfLoop() {
        Graph<Integer> g = intGraph(1);
        g.addEdge(0, 0);
        assertEquals(Arrays.asList(0), BreadthFirstSearch.bfs(g));
    }

    private static void undirectedEdges() {
        Graph<Integer> g = intGraph(2);
        g.addEdge(0, 1);
        assertTrue("0 -> 1 present", g.neighbors(0).contains(1));
        assertTrue("1 -> 0 present (undirected)", g.neighbors(1).contains(0));
    }

    private static void neighborsUnmodifiable() {
        Graph<Integer> g = intGraph(2);
        g.addEdge(0, 1);
        assertThrows(UnsupportedOperationException.class, () -> g.neighbors(0).add(99));
    }

    private static void vertexCount() {
        assertEquals(0, new Graph<Integer>().vertexCount());
        assertEquals(7, intGraph(7).vertexCount());
    }

    private static void stringVertices() {
        Graph<String> g = new Graph<>();
        g.addEdge("a", "b");
        g.addEdge("b", "c");
        g.addEdge("d", "e");
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), BreadthFirstSearch.bfs(g));
    }

    private static void addEdgeCreatesVertices() {
        Graph<String> g = new Graph<>();
        g.addEdge("x", "y");
        assertEquals(2, g.vertexCount());
        assertEquals(new HashSet<>(Arrays.asList("x", "y")), new HashSet<>(g.vertices()));
    }

    private static void verticesInInsertionOrder() {
        Graph<String> g = new Graph<>();
        g.addEdge("a", "b");
        g.addEdge("c", "a");
        assertEquals(Arrays.asList("a", "b", "c"), new ArrayList<>(g.vertices()));
    }

    private static void neighborsAbsentVertexThrows() {
        Graph<Integer> g = new Graph<>();
        assertThrows(Graph.VertexNotFoundException.class, () -> g.neighbors(42));
    }

    private static void vertexNotFoundIsAGraphException() {
        Graph<Integer> g = new Graph<>();
        // The specific exception must be catchable via the hierarchy's base type.
        assertThrows(Graph.GraphException.class, () -> g.neighbors(42));
    }

    private static void nullVerticesRejected() {
        assertThrows(NullPointerException.class, () -> new Graph<String>().addVertex(null));
        assertThrows(NullPointerException.class, () -> new Graph<String>().addEdge("a", null));
    }

    private static void graphIsIterable() {
        Graph<String> g = new Graph<>();
        g.addEdge("a", "b");
        g.addEdge("c", "a");
        List<String> iterated = new ArrayList<>();
        for (String vertex : g) { // exercises Iterable<V> directly
            iterated.add(vertex);
        }
        assertEquals(Arrays.asList("a", "b", "c"), iterated);
        assertEquals(new ArrayList<>(g.vertices()), iterated);
    }

    private static void graphIteratorReadOnly() {
        Graph<Integer> g = intGraph(1);
        Iterator<Integer> it = g.iterator();
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    private static void forEachNeighborOrder() {
        Graph<Integer> g = intGraph(4);
        g.addEdge(0, 3);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        List<Integer> collected = new ArrayList<>();
        g.forEachNeighbor(0, neighbor -> {
            collected.add(neighbor);
            return true;
        });
        assertEquals(Arrays.asList(3, 1, 2), collected);
        assertEquals(g.neighbors(0), collected); // same contents as the list view
    }

    private static void forEachNeighborCompletes() {
        Graph<Integer> g = intGraph(3);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        boolean completed = g.forEachNeighbor(0, neighbor -> true);
        assertTrue("traversal reported completion", completed);
    }

    private static void forEachNeighborInterrupts() {
        Graph<Integer> g = intGraph(4);
        g.addEdge(0, 3);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        List<Integer> collected = new ArrayList<>();
        // Stop right after the first neighbor.
        boolean completed = g.forEachNeighbor(0, neighbor -> {
            collected.add(neighbor);
            return false;
        });
        assertTrue("traversal reported interruption", !completed);
        assertEquals(Arrays.asList(3), collected); // remaining neighbors were skipped
    }

    private static void forEachNeighborAbsentVertexThrows() {
        Graph<Integer> g = new Graph<>();
        assertThrows(Graph.VertexNotFoundException.class, () -> g.forEachNeighbor(42, v -> true));
    }

    private static void forEachNeighborNullVisitor() {
        Graph<Integer> g = intGraph(1);
        assertThrows(NullPointerException.class, () -> g.forEachNeighbor(0, null));
    }

    // ----------------------------------------------------------- harness

    /** Builds an integer graph with vertices {@code 0..vertexCount-1} added in order. */
    private static Graph<Integer> intGraph(int vertexCount) {
        Graph<Integer> g = new Graph<>();
        for (int i = 0; i < vertexCount; i++) {
            g.addVertex(i);
        }
        return g;
    }

    /** A test body that may throw; thrown errors fail the individual test. */
    private interface TestBody {
        void run() throws Exception;
    }

    private static void test(String name, TestBody body) {
        run++;
        try {
            body.run();
            System.out.println("[PASS] " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[ERROR] " + name + " -> " + e);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertTrue(String what, boolean condition) {
        if (!condition) {
            throw new AssertionError("expected true: " + what);
        }
    }

    private static void assertThrows(Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("expected " + expected.getSimpleName() + " but threw " + actual);
        }
        throw new AssertionError("expected " + expected.getSimpleName() + " but nothing was thrown");
    }
}
