/**
 * The life-cycle status of an organism, modelled as a small state machine.
 *
 * An organism begins {@link #ALIVE} and transitions exactly once, irreversibly,
 * to {@link #DEAD}. Centralising the status here replaces the bare {@code alive}
 * boolean that was previously managed by hand, and gives the rest of the codebase
 * a single place that defines what "alive" means.
 *
 * @version 2022.03.02
 */
public enum OrganismStatus
{
    /** The organism is living and participates in the simulation. */
    ALIVE,
    /** The organism has died and has been removed from the field. */
    DEAD;

    /**
     * @return true if this status represents a living organism.
     */
    public boolean isAlive()
    {
        return this == ALIVE;
    }
}
