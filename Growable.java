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
     * Getter method for rate of growth.
     *
     * @return A double representing growth rate.
     */
    double getGrowthRate();

    /**
     * Setter method for rate of growth.
     *
     * @param rate A given growth rate.
     */
    void setGrowthRate(double rate);

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
