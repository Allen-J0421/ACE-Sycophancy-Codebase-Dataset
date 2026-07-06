/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Strategy interface for plants that adjust their breeding probability
 * based on recent weather conditions.
 *
 * @version 2022.03.02
 */
public interface WeatherEffect {

    /**
     * Returns the breeding probability appropriate for the given weather state.
     *
     * @param weather The current weather in the simulation.
     * @return The breeding probability the plant should use this step.
     */
    double getBreedingProbability(Weather weather);
}
