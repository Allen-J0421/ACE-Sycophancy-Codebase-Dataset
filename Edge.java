/**
 * An undirected, weighted edge in a minimum spanning tree.
 *
 * @param source      one endpoint of the edge
 * @param destination the other endpoint of the edge
 * @param weight      the weight connecting the two endpoints
 */
public record Edge(int source, int destination, int weight) {
}
