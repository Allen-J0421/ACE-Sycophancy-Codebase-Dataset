/**
 * Evaluates whether a simulation has enough surviving species to remain viable.
 * Encapsulates the viability business rule so it can be changed independently
 * of FieldStats, which is responsible only for counting.
 *
 * @version 2022.03.02
 */
public class SimulationViabilityEvaluator
{
    private static final int MIN_ALIVE_SPECIES = 2;

    /**
     * Returns true if more than MIN_ALIVE_SPECIES species have a non-zero
     * population count in the given stats snapshot.
     *
     * @param stats A FieldStats instance whose counts are already up to date.
     */
    public boolean isViable(FieldStats stats)
    {
        int nonZero = 0;
        for (Class cls : SimulationInfo.ALL_ACTORS) {
            if (stats.getCount(cls) > 0) {
                nonZero++;
            }
        }
        return nonZero > MIN_ALIVE_SPECIES;
    }
}
