/**
 * Generates population and viability information for the simulation.
 */
public class SimulationDemographics
{
    private final FieldStats stats;

    /**
     * Create a demographics manager backed by the provided counters.
     */
    public SimulationDemographics(FieldStats stats)
    {
        this.stats = stats;
    }

    /**
     * Get details of what is in the field.
     * @param field The field to get details for.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        ensureCounts(field);

        int totalCount = 0;
        for(Counter info : stats.getCounters()) {
            totalCount += info.getCount();
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        buffer.append(" Total: " + totalCount);
        return buffer.toString();
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @param field The field to generate the stats for.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        int nonZero = 0;
        ensureCounts(field);

        for(Counter info : stats.getCounters()) {
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Regenerate counts when the cached values are stale.
     */
    private void ensureCounts(Field field)
    {
        if(!stats.isValid()) {
            generateCounts(field);
        }
    }

    /**
     * Generate counts of the number of populated field locations.
     */
    private void generateCounts(Field field)
    {
        stats.reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object occupant = field.getObjectAt(row, col);
                if(occupant != null) {
                    stats.incrementCount(occupant.getClass());
                }
            }
        }
        stats.countFinished();
    }
}
