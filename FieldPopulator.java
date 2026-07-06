import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Strategy interface for seeding a Field with an initial population of organisms.
 * Implementations decide which species to place and how many.
 *
 * @version 2022.03.02
 */
public interface FieldPopulator {

    /**
     * Clear the field and populate it with organisms.
     *
     * @param organisms The list to which all newly created organisms are added.
     * @param field     The field to populate.
     */
    void populate(List<Entity> organisms, Field field);
}
