/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Contract for disease-related probability parameters on an animal.
 *
 * @version 2022.03.02
 */
public interface DiseaseProfile {

    /**
     * @return The probability that this animal attempts to spread disease each step.
     */
    double getDiseaseSpreadProbability();

    /**
     * @return The probability that this animal dies from disease each step.
     */
    double getDeathByDiseaseProbability();
}
