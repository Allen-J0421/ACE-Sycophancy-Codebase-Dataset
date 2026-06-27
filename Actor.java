/**
 * Represents all simulation entities that participate in a lifecycle step.
 *
 * @version 2022.06.27
 */
interface Actor
{
    /**
     * Advance this actor by one simulation tick.
     * @param context Shared lifecycle state for the current step.
     */
    void tick(SimulationContext context);

    /**
     * @return true if this actor should be removed from the simulation.
     */
    default boolean isExpired()
    {
        return false;
    }
}
