/**
 * Strategy interface for an animal's biological health simulation.
 * Encapsulates infection state, immunity state, and the per-step
 * transitions between them, decoupling health logic from movement
 * and dietary concerns in {@link Animal#act}.
 */
public interface HealthBehaviour
{
    /**
     * Advance the disease/immunity state machine by one step.
     * Handles death-from-infection and immunity acquisition or loss.
     *
     * @param rand The shared random generator.
     * @return true if the animal has died from infection this step
     *         (caller must invoke {@link Animal#setDead()}).
     */
    boolean progressDisease(SimRandom rand);

    /**
     * Attempt to infect the animal through environmental exposure.
     * Only acts when the animal is susceptible (not immune, not already
     * infected). Calls {@code self.surroundingsInfected()} lazily so
     * the random generator is consumed only when needed.
     *
     * @param self  The animal being updated.
     * @param field The field used for neighbour scanning.
     * @param rand  The shared random generator.
     */
    void tryAcquireDisease(Animal self, Field field, SimRandom rand);

    /**
     * Attempt to infect this animal from a specific source (e.g. eating
     * infected prey). Uses the disease-spread probability.
     *
     * @param sourceIsInfected true if the infection source is infected.
     * @param rand             The shared random generator.
     */
    void tryInfectFrom(boolean sourceIsInfected, SimRandom rand);

    /** @return true if the animal is currently infected. */
    boolean isInfected();

    /** @return true if the animal is currently immune. */
    boolean isImmune();
}
