/**
 * The outcome of a shortest-path computation, modelled as a closed sum type.
 *
 * <p>A run is exactly one of two cases:
 * <ul>
 *   <li>{@link Distances} &mdash; the source reaches a well-defined set of distances, or
 *   <li>{@link NegativeCycle} &mdash; a negative-weight cycle is reachable, so no
 *       shortest path is defined.
 * </ul>
 *
 * <p>Sealing the interface lets callers handle the result with an exhaustive
 * {@code switch} and lets the compiler reject any unhandled case. This replaces
 * the earlier single class that carried a {@code boolean} flag plus a nullable
 * distance array guarded by runtime checks: distances simply do not exist on the
 * {@link NegativeCycle} branch, so they can no longer be queried by mistake.
 */
sealed interface ShortestPathResult permits Distances, NegativeCycle {
}
