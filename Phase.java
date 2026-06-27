/**
 * The time of day a simulation step represents. Replaces the previous integer
 * timeOfDay flag (0 = day, 1 = night) with a type-safe alternative, keeping the
 * step-parity rule and the user-facing label in one place.
 *
 * @version (27/06/2026)
 */
public enum Phase
{
    DAY("Day"),
    NIGHT("Night");

    // Human-readable label used by the user interface.
    private final String label;

    Phase(String label)
    {
        this.label = label;
    }

    /**
     * The phase a given simulation step falls in. Even steps are day, odd steps
     * are night, matching the simulation's step parity.
     *
     * @param step The simulation step.
     * @return The phase for that step.
     */
    public static Phase forStep(int step)
    {
        return step % 2 == 0 ? DAY : NIGHT;
    }

    /**
     * @return The human-readable label for this phase.
     */
    public String getLabel()
    {
        return label;
    }
}
