/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Contract for reproductive parameters on an organism.
 * Separates the breeding parameter specification from organism lifecycle management.
 *
 * @version 2022.03.02
 */
public interface BreedingProfile {

    /**
     * @return The probability that this organism breeds each step when eligible.
     */
    double getBreedingProbability();

    /**
     * @return The maximum number of offspring produced in a single birth event.
     */
    int getMaxLitterSize();

    /**
     * @return The minimum age at which this organism becomes capable of breeding.
     */
    int getBreedingAge();
}
