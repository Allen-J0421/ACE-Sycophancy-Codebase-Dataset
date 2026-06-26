package safari;

import java.util.function.BiFunction;

/**
 * A single registry entry for population seeding.
 */
record SpawnRule(
    String name,
    double probability,
    BiFunction<Field, Location, Actor> constructor
)
{
    Actor create(Field field, Location location)
    {
        return constructor.apply(field, location);
    }
}
