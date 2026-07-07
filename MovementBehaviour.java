/**
 * Strategy interface for an animal's movement behaviour.
 * Controls whether the animal acts at all this step and how likely
 * it is to wander when no food is found.
 */
public interface MovementBehaviour
{
    /**
     * Return true if the animal should proceed to act this simulation step.
     * Implementations may consume the random generator (e.g. probabilistic
     * night/fog activity gates).
     */
    boolean canActThisStep(SimRandom rand);

    /** Probability that the animal moves to a random free adjacent cell. */
    double getMovementProbability();
}
