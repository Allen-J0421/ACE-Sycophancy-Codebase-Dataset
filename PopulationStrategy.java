import java.util.*;

/**
 * Strategy interface for seeding a Field with an initial actor population.
 * Implementations decide which species to create, where, and in what quantities.
 *
 * @version 2022.03.02
 */
public interface PopulationStrategy
{
    /**
     * Clear the field and fill the actors list with the initial population.
     * @param field    The field to populate.
     * @param environment The current environment (needed for actors such as Hunter).
     * @param actors   The list to receive all newly created actors.
     */
    void populate(Field field, Environment environment, List<Actor> actors);

    /**
     * Return the per-species creation probabilities used by this strategy.
     * Used by the engine for ongoing grass regrowth and by the simulator for
     * population-stability telemetry.
     */
    Map<Class<?>, Double> getCreationProbabilities();
}
