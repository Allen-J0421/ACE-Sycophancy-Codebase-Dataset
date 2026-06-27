/**
 * A factory that constructs a single entity instance at a given field location.
 * Implementations are typically short lambdas, e.g. {@code (f, l) -> new Lion(true, f, l)}.
 */
@FunctionalInterface
public interface EntityFactory
{
    /**
     * Create a new entity at the specified location in the given field.
     *
     * @param field    The field the entity will inhabit.
     * @param location The cell the entity occupies at birth.
     * @return A freshly constructed entity.
     */
    Entity create(Field<Entity> field, Location location);
}
