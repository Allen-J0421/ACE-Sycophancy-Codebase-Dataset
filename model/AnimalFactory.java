package model;


/**
 * Creates a freshly spawned {@link Animal} of some species at a location.
 * Lets callers build animals without naming a concrete class.
 */
@FunctionalInterface
public interface AnimalFactory {
	Animal create(Field field, Location location);
}
