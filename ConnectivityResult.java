import java.util.List;

/**
 * The result of analyzing an undirected graph's connectivity: its articulation
 * points (cut vertices) and its bridges (cut edges).
 *
 * @param articulationPoints the cut vertices, in ascending order
 * @param bridges            the cut edges, ordered by endpoints then edge id
 */
public record ConnectivityResult(List<Integer> articulationPoints, List<Edge> bridges) {
}
