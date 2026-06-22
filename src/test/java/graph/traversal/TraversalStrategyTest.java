package graph.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.Graph;
import graph.GraphBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

class TraversalStrategyTest {

    static Stream<TraversalStrategy> strategies() {
        return Stream.of(new BreadthFirstTraversal(), new DepthFirstTraversal());
    }

    /** A path 0-1-2 plus an isolated vertex 3. */
    private static Graph pathWithIsolatedVertex() {
        return new GraphBuilder(4).addEdge(0, 1).addEdge(1, 2).build();
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void traversalReachesEveryVertexInTheComponent(TraversalStrategy strategy) {
        boolean[] visited = new boolean[4];
        List<Integer> reached = strategy.traverseFrom(pathWithIsolatedVertex(), 0, visited);
        assertEquals(Set.of(0, 1, 2), new HashSet<>(reached));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void traversalMarksOnlyTheReachedVerticesVisited(TraversalStrategy strategy) {
        boolean[] visited = new boolean[4];
        strategy.traverseFrom(pathWithIsolatedVertex(), 0, visited);
        assertTrue(visited[0] && visited[1] && visited[2]);
        assertTrue(!visited[3], "isolated vertex must remain unvisited");
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void traversalStartsAtTheSource(TraversalStrategy strategy) {
        boolean[] visited = new boolean[4];
        List<Integer> reached = strategy.traverseFrom(pathWithIsolatedVertex(), 0, visited);
        assertEquals(0, reached.get(0));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void traversalRespectsAlreadyVisitedVertices(TraversalStrategy strategy) {
        boolean[] visited = new boolean[4];
        visited[1] = true; // pretend 1 was reached by an earlier traversal
        List<Integer> reached = strategy.traverseFrom(pathWithIsolatedVertex(), 0, visited);
        // With 1 blocked, only the source 0 is reachable.
        assertEquals(List.of(0), reached);
    }

    @Test
    void breadthFirstVisitsInLevelOrder() {
        // 0 connected to 1 and 2; BFS yields the source then its neighbours.
        Graph star = new GraphBuilder(3).addEdge(0, 1).addEdge(0, 2).build();
        List<Integer> reached = new BreadthFirstTraversal().traverseFrom(star, 0, new boolean[3]);
        assertEquals(List.of(0, 1, 2), reached);
    }

    @Test
    void depthFirstFollowsAPathToItsEndBeforeBacktracking() {
        // Path 0-1-2-3: DFS descends fully before returning.
        Graph path = new GraphBuilder(4).addEdge(0, 1).addEdge(1, 2).addEdge(2, 3).build();
        List<Integer> reached = new DepthFirstTraversal().traverseFrom(path, 0, new boolean[4]);
        assertEquals(List.of(0, 1, 2, 3), reached);
    }
}
