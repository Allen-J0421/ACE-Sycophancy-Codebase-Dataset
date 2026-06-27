import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

/**
 * An immutable snapshot of the simulation's population statistics for a single
 * step: the step number, the per-species head-count, the current weather and
 * time of day, and whether the simulation is still viable.
 *
 * The {@link Simulator} computes one of these each step from the field and hands
 * it to the view, which renders it without needing to recompute (or have any
 * access to) the population statistics itself.
 *
 * @version 2022.03.02
 */
public final class PopulationSnapshot
{
    // The simulation step this snapshot describes.
    private final int step;
    // Per-species head-count, in the order species were first encountered.
    private final Map<Class, Integer> counts;
    // The weather at this step.
    private final String weather;
    // The time of day ("Day" or "Night") at this step.
    private final String timeOfDay;
    // Whether more than one species is still alive.
    private final boolean viable;

    /**
     * Create a snapshot. The supplied counts are defensively copied so the
     * snapshot is immutable regardless of what the caller does afterwards.
     *
     * @param step The simulation step.
     * @param counts Per-species head-count.
     * @param weather The current weather description.
     * @param timeOfDay The current time of day.
     * @param viable Whether the simulation is still viable.
     */
    public PopulationSnapshot(int step, Map<Class, Integer> counts, String weather, String timeOfDay, boolean viable)
    {
        this.step = step;
        this.counts = Collections.unmodifiableMap(new LinkedHashMap<>(counts));
        this.weather = weather;
        this.timeOfDay = timeOfDay;
        this.viable = viable;
    }

    /**
     * @return The simulation step this snapshot describes.
     */
    public int getStep()
    {
        return step;
    }

    /**
     * @return An unmodifiable per-species head-count.
     */
    public Map<Class, Integer> getCounts()
    {
        return counts;
    }

    /**
     * @return true if more than one species is still alive.
     */
    public boolean isViable()
    {
        return viable;
    }

    /**
     * Build the human-readable population details line shown by the view:
     * each species' count, followed by the weather and time of day.
     *
     * @return A string describing the population at this step.
     */
    public String getPopulationDetails()
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<Class, Integer> entry : counts.entrySet()) {
            buffer.append(entry.getKey().getName());
            buffer.append(": ");
            buffer.append(entry.getValue());
            buffer.append(' ');
        }
        buffer.append("Weather: " + weather);
        buffer.append(" Time: " + timeOfDay);
        return buffer.toString();
    }
}
