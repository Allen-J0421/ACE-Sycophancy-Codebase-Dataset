/**
 * A small, self-contained library for deciding whether an undirected graph is
 * bipartite, built around a generic adjacency-list representation.
 *
 * <p>Graph representation is layered so new variants can be added without
 * touching the algorithms:
 * <ul>
 *   <li>{@link bipartite.Graph} is the read-only interface algorithms depend
 *       on;</li>
 *   <li>{@link bipartite.AbstractAdjacencyListGraph} is a generic adjacency-list
 *       base parameterised by an incidence type, leaving "what an edge carries"
 *       and "directed vs. undirected" to subclasses;</li>
 *   <li>{@link bipartite.UndirectedGraph}, {@link bipartite.DirectedGraph}, and
 *       {@link bipartite.WeightedUndirectedGraph} are the concrete
 *       implementations;</li>
 *   <li>{@link bipartite.GraphEdge} (with {@link bipartite.Edge} and
 *       {@link bipartite.WeightedEdge}) is the edge contract, and
 *       {@link bipartite.GraphInputValidator} validates construction inputs.</li>
 * </ul>
 *
 * <p>Algorithms operate against the {@link bipartite.Graph} interface, so they
 * run unchanged on every implementation. {@link bipartite.BipartiteChecker} runs
 * a breadth-first 2-coloring and returns the sealed
 * {@link bipartite.BipartiteResult} — either the two vertex partitions or an
 * odd-cycle witness — {@link bipartite.DepthFirstSearch} provides a standard
 * depth-first traversal, {@link bipartite.TopologicalSort} orders a
 * {@link bipartite.DirectedGraph} so dependencies precede dependents (returning
 * the sealed {@link bipartite.TopologicalSortResult} — either the order or a
 * directed-cycle witness), and {@link bipartite.ShortestPath} runs Dijkstra's
 * algorithm over a {@link bipartite.WeightedUndirectedGraph} (returning the
 * sealed {@link bipartite.ShortestPathResult} — either the distance and path or
 * an unreachable notification), and {@link bipartite.StronglyConnectedComponents}
 * runs Tarjan's algorithm (returning the sealed
 * {@link bipartite.StronglyConnectedComponentsResult} — a single spanning
 * component or the fragmented components).
 *
 * <p>{@link bipartite.Main} provides a runnable demonstration, and
 * {@link bipartite.BipartiteCheckerTests},
 * {@link bipartite.DepthFirstSearchTests},
 * {@link bipartite.TopologicalSortTests},
 * {@link bipartite.ShortestPathTests}, and
 * {@link bipartite.StronglyConnectedComponentsTests} are dependency-free test
 * harnesses.
 */
package bipartite;
