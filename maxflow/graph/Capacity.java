package maxflow.graph;

/**
 * A non-negative edge weight in a flow network — a capacity or an amount of flow.
 *
 * <p>Wrapping the raw {@code int} gives the quantity a name, a single home for its
 * domain rule (non-negativity), and arithmetic that reads like the problem
 * ({@link #min}, {@link #plus}), while keeping it from being confused with a vertex
 * index. {@link #INFINITE} is the identity for {@link #min} — used when reducing a
 * path to its bottleneck — and is not meant to take part in arithmetic.
 */
public record Capacity(int units) implements Comparable<Capacity> {

    /** No capacity. */
    public static final Capacity ZERO = new Capacity(0);

    /** An unbounded capacity, usable only as the identity of {@link #min}. */
    public static final Capacity INFINITE = new Capacity(Integer.MAX_VALUE);

    public Capacity {
        if (units < 0) {
            throw new IllegalArgumentException("capacity must be non-negative, was " + units);
        }
    }

    /** Returns a capacity of {@code units}. */
    public static Capacity of(int units) {
        return new Capacity(units);
    }

    /** Returns true if this capacity can carry flow. */
    public boolean isPositive() {
        return units > 0;
    }

    /** Returns the smaller of this and {@code other}. */
    public Capacity min(Capacity other) {
        return units <= other.units ? this : other;
    }

    /** Returns the sum of this and {@code other}. */
    public Capacity plus(Capacity other) {
        return new Capacity(Math.addExact(units, other.units));
    }

    @Override
    public int compareTo(Capacity other) {
        return Integer.compare(units, other.units);
    }

    @Override
    public String toString() {
        return this == INFINITE ? "∞" : Integer.toString(units);
    }
}
