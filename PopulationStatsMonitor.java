/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Observes a Field each step and maintains population counts via FieldStats.
 * Provides viability checks and population summaries without any GUI involvement.
 *
 * @version 2022.03.02
 */
public class PopulationStatsMonitor
{
    private final FieldStats stats;

    /**
     * Construct a monitor backed by a fresh FieldStats instance.
     */
    public PopulationStatsMonitor()
    {
        stats = new FieldStats();
    }

    /**
     * Scan the entire field and update the population counts.
     * Must be called once per step before querying population data.
     *
     * @param field The field to observe.
     */
    public void observe(Field field)
    {
        stats.reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object obj = field.getObjectAt(row, col);
                if (obj != null) {
                    stats.incrementCount(obj.getClass());
                }
            }
        }
        stats.countFinished();
    }

    /**
     * Return a human-readable summary of current population counts.
     * Call {@link #observe(Field)} first to ensure counts are fresh.
     *
     * @param field The field the counts were collected from.
     * @return A string listing each species and its count.
     */
    public String getPopulationSummary(Field field)
    {
        return stats.getPopulationDetails(field);
    }

    /**
     * Determine whether the simulation is still viable (more than one
     * species has a non-zero population).
     * Call {@link #observe(Field)} first to ensure counts are fresh.
     *
     * @param field The field the counts were collected from.
     * @return true if more than one species remains alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
}
