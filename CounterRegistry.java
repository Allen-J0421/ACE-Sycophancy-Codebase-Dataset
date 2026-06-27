import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages mutable population tallies keyed by class and produces immutable
 * {@link Counter} snapshots on demand.
 *
 * This is the only class in the codebase that increments or resets raw
 * counts.  {@link Counter} instances vended by {@link #snapshot()} are
 * read-only value objects; subsequent calls to {@link #clear()} or
 * {@link #increment(Class)} leave previously returned snapshots unchanged.
 *
 * Typical usage within one simulation step:
 * <pre>
 *   registry.clear();
 *   for each occupant → registry.increment(occupant.getClass());
 *   Map&lt;Class, Counter&gt; counts = registry.snapshot();
 * </pre>
 */
class CounterRegistry
{
    private final HashMap<Class, Integer> counts = new HashMap<>();

    /** Remove all recorded tallies. */
    void clear()
    {
        counts.clear();
    }

    /**
     * Increment the tally for {@code clazz} by one.
     * Creates an entry with count 1 if none exists yet.
     * @param clazz the class whose tally should be incremented
     */
    void increment(Class clazz)
    {
        counts.merge(clazz, 1, Integer::sum);
    }

    /**
     * Return an unmodifiable snapshot of all current tallies as
     * {@link Counter} instances.
     * @return immutable map from class to its {@link Counter}
     */
    Map<Class, Counter> snapshot()
    {
        Map<Class, Counter> result = new HashMap<>();
        for (Map.Entry<Class, Integer> entry : counts.entrySet()) {
            result.put(entry.getKey(),
                       new Counter(entry.getKey().getName(), entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }
}
