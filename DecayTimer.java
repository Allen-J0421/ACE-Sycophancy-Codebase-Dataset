/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Tracks how long something has been decaying and reports when it has decayed
 * for longer than its allowed lifetime. Keeping this countdown in its own small
 * component separates "how long until these remains disappear" from the
 * responsibilities of the organism that is decaying.
 *
 * @version 2022.03.02
 */
public class DecayTimer {

    // The number of steps the remains may linger before being removed.
    private final int lifetimeAfterDeath;
    // How many steps the remains have been decaying for so far.
    private int stepsDecaying;

    /**
     * Create a decay timer.
     *
     * @param lifetimeAfterDeath The number of steps the remains may linger
     *                           after death before they should be removed.
     */
    public DecayTimer(int lifetimeAfterDeath) {
        this.lifetimeAfterDeath = lifetimeAfterDeath;
        this.stepsDecaying = 0;
    }

    /**
     * Record that another step of decay has passed.
     *
     * @return true once the remains have decayed beyond their lifetime and
     *         should be removed.
     */
    public boolean tick() {
        stepsDecaying++;
        return stepsDecaying > lifetimeAfterDeath;
    }
}
