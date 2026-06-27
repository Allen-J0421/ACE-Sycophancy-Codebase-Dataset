/**
 * Enforces all functionality required to add a player to the simulation.
 *
 * @version 2.9.2
 */
interface Simulatable {

    /**
     * Defines a player's actions for one simulation step.
     * Environmental conditions and spawn registration are accessed through
     * {@code context} rather than passed as separate parameters.
     * @param context the context for this step
     */
    void act(StepContext context);

    /** @return true if the player is still alive */
    boolean isAlive();

    /** Remove the player from the simulation. */
    void setDead();
}
