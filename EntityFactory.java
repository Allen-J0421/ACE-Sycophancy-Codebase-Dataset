/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Functional interface for creating a single Entity at a specific location in
 * the field. Used as the per-species factory in a {@link SpeciesEntry}.
 *
 * @version 2022.03.02
 */
@FunctionalInterface
public interface EntityFactory {

    /**
     * Create a new entity and place it at the given location.
     *
     * @param field    The field that the entity will occupy.
     * @param location The cell within the field to place the entity.
     * @return The newly created entity.
     */
    Entity create(Field field, Location location);
}
