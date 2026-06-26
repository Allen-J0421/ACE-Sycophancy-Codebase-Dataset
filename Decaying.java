/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A contractual interface for things that decay once dead: their remains
 * linger for a while and are eventually removed from the simulation.
 *
 * @version 2022.03.02
 */
public interface Decaying {

    /**
     * Advance decay by a single step. Once the remains have fully decayed they
     * are removed from the simulation.
     */
    void decay();
}
