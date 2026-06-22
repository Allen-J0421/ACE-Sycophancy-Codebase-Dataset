import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by the integers {@code 0 .. vertexCount() - 1}.
 * The adjacency list is not exposed directly; callers read neighbours through
 * {@link #neighbours(int)}, which returns an unmodifiable view.
 */
final class Graph {

    private final List<List<Integer>> adjacency;

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /** Returns the number of vertices in the graph. */
    int vertexCount() {
        return adjacency.size();
    }

    /**
     * Adds an undirected edge between {@code u} and {@code v}.
     *
     * @throws IndexOutOfBoundsException if either endpoint is not a valid vertex
     */
    void addEdge(int u, int v) {
        adjacency.get(checkVertex(u)).add(v);
        adjacency.get(checkVertex(v)).add(u);
    }

    /** Returns an unmodifiable view of the neighbours of {@code vertex}. */
    List<Integer> neighbours(int vertex) {
        return Collections.unmodifiableList(adjacency.get(checkVertex(vertex)));
    }

    private int checkVertex(int vertex) {
        return Objects.checkIndex(vertex, adjacency.size());
    }
}

/**
 * The connected components of an undirected {@link Graph}: an immutable
 * partition of every vertex into groups that are mutually reachable.
 *
 * <p>Beyond listing the components, this answers the questions callers
 * usually have — how many components there are, which one a vertex belongs
 * to, and whether two vertices are connected — in constant time.
 */
final class Components {

    private final List<List<Integer>> components;
    private final int[] componentOfVertex;

    /**
     * @param components the components, each a list of the vertices it contains
     * @param vertexCount the number of vertices the components partition
     */
    Components(List<List<Integer>> components, int vertexCount) {
        List<List<Integer>> copy = new ArrayList<>(components.size());
        int[] index = new int[vertexCount];
        for (int id = 0; id < components.size(); id++) {
            copy.add(List.copyOf(components.get(id)));
            for (int vertex : components.get(id)) {
                index[vertex] = id;
            }
        }
        this.components = List.copyOf(copy);
        this.componentOfVertex = index;
    }

    /** Returns the number of connected components. */
    int count() {
        return components.size();
    }

    /**
     * Returns the components as an unmodifiable list of unmodifiable vertex
     * lists. Component order, and vertex order within a component, follow the
     * traversal that produced them.
     */
    List<List<Integer>> asList() {
        return components;
    }

    /**
     * Returns the identifier of the component containing {@code vertex}, in the
     * range {@code 0 .. count() - 1}.
     *
     * @throws IndexOutOfBoundsException if {@code vertex} is not a valid vertex
     */
    int componentOf(int vertex) {
        return componentOfVertex[Objects.checkIndex(vertex, componentOfVertex.length)];
    }

    /** Returns whether {@code u} and {@code v} lie in the same component. */
    boolean connected(int u, int v) {
        return componentOf(u) == componentOf(v);
    }
}

/**
 * Finds the {@link Components} of an undirected {@link Graph} using
 * breadth-first search.
 */
final class ConnectedComponentsFinder {

    /** Returns the connected components of {@code graph}. */
    Components find(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(exploreFrom(graph, vertex, visited));
            }
        }
        return new Components(components, graph.vertexCount());
    }

    /** Collects every vertex reachable from {@code source}, marking it visited. */
    private List<Integer> exploreFrom(Graph graph, int source, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbour : graph.neighbours(current)) {
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }
        return component;
    }
}

/** Demonstrates {@link ConnectedComponentsFinder} on a small sample graph. */
class ConnectedComponents {

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
