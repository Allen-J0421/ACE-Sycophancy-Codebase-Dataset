package model;


/**
 * Creates a {@link Plant} of some species at a location.
 * Lets callers build plants without naming a concrete class.
 */
@FunctionalInterface
public interface PlantFactory {
	Plant create(Field field, Location location);
}
