/**
 * A small, self-contained library for deciding whether an undirected graph is
 * bipartite.
 *
 * <p>The package separates four concerns:
 * <ul>
 *   <li>{@link bipartite.Edge} and {@link bipartite.UndirectedGraph} model an
 *       immutable graph;</li>
 *   <li>{@link bipartite.GraphInputValidator} validates construction inputs;</li>
 *   <li>{@link bipartite.BipartiteChecker} runs the breadth-first 2-coloring
 *       algorithm;</li>
 *   <li>{@link bipartite.BipartiteResult} is the sealed outcome — either the two
 *       vertex partitions or an odd-cycle witness.</li>
 * </ul>
 *
 * <p>{@link bipartite.Main} provides a runnable demonstration and
 * {@link bipartite.BipartiteCheckerTests} a dependency-free test harness.
 */
package bipartite;
