/**
 * The result when a negative-weight cycle is reachable from the source.
 *
 * <p>In this case no shortest path is well defined, so the type deliberately
 * carries no distances at all.
 */
record NegativeCycle() implements ShortestPathResult {

    @Override
    public String toString() {
        return "NegativeCycle";
    }
}
