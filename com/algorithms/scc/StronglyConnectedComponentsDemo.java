package com.algorithms.scc;

/**
 * Demonstrates the strongly-connected-components pipeline end to end:
 * build a graph, find its components with Kosaraju's algorithm, and print them.
 *
 * <p>This is the refactored entry point that replaces the old monolithic
 * {@code StronglyConnectedComponents.main}. The graph below is the same example
 * the original used (edges 1->3, 1->4, 2->1, 3->2, 4->5), so the output is
 * directly comparable: {@code 1 2 3}, {@code 4}, {@code 5}.
 */
public final class StronglyConnectedComponentsDemo {

    private StronglyConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        DirectedGraph<Integer> graph = new DirectedGraphBuilder<Integer>()
                .addEdge(1, 3)
                .addEdge(1, 4)
                .addEdge(2, 1)
                .addEdge(3, 2)
                .addEdge(4, 5)
                .build();

        StronglyConnectedComponentsFinder<Integer> finder =
                new KosarajuStronglyConnectedComponentsFinder<>();
        StronglyConnectedComponentsResult<Integer> result = finder.find(graph);

        StronglyConnectedComponentsFormatter<Integer> formatter =
                new StronglyConnectedComponentsFormatter<>();
        System.out.println(formatter.format(result));
    }
}
