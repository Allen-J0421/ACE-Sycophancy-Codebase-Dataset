package maxflow.graph;

import java.util.Objects;

/**
 * A directed, weighted edge {@code from -> to}. This single type is reused across
 * the library wherever a (tail, head, weight) triple is needed: in a
 * {@link FlowNetwork} the {@link #value()} is the edge's <em>capacity</em>, and in
 * a max-flow result it is the <em>flow</em> routed along that edge. The meaning is
 * fixed by context; the structure and validation are shared.
 *
 * <p>Endpoints must be distinct, non-negative vertex identifiers. The weight is a
 * {@link Capacity}, which carries the non-negativity rule. An {@code Edge} does not
 * know how many vertices exist, so callers that have a vertex count (such as
 * {@link FlowNetwork.Builder}) additionally check the upper bound.
 */
public record Edge(int from, int to, Capacity value) {

    public Edge {
        if (from < 0) {
            throw new IllegalArgumentException("from vertex must be non-negative, was " + from);
        }
        if (to < 0) {
            throw new IllegalArgumentException("to vertex must be non-negative, was " + to);
        }
        if (from == to) {
            throw new IllegalArgumentException("Self-loops are not allowed at vertex " + from);
        }
        Objects.requireNonNull(value, "value");
    }

    /** Convenience constructor that wraps a raw weight in a {@link Capacity}. */
    public Edge(int from, int to, int value) {
        this(from, to, Capacity.of(value));
    }

    /** Returns a copy of this edge with a different weight but the same endpoints. */
    public Edge withValue(Capacity newValue) {
        return new Edge(from, to, newValue);
    }

    /** Returns a copy of this edge with a different raw weight but the same endpoints. */
    public Edge withValue(int newValue) {
        return new Edge(from, to, Capacity.of(newValue));
    }

    @Override
    public String toString() {
        return from + " -> " + to + ": " + value;
    }
}
