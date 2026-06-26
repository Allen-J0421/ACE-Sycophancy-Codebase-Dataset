/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A contractual interface representing an ability to grow.
 *
 * @version 2022.03.02
 */
public interface Growable {

    /**
     * Grow in size.
     */
    void grow();

    /**
     * Get maximum size of the object.
     *
     * @return A double representing maximum size.
     */
    double getMaxSize();

    /**
     * Get the current size of the object.
     *
     * @return A double representing current size.
     */
    double getSize();
}
