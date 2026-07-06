import java.util.List;
import java.util.function.BiFunction;

/**
 * Centralizes offspring instantiation and field placement for all actor types.
 * Consumers and Producers call the appropriate spawn method after computing
 * their own birth count; this class owns the reflection, location lookup, and
 * list insertion that were previously duplicated between the two hierarchies.
 */
public class BirthManager
{
    /**
     * Instantiate and place Consumer offspring.
     * Expects the concrete class to expose a {@code (boolean randomAge, Field, Location)}
     * constructor; each child is created with {@code randomAge = true}.
     *
     * @param parent The parent actor producing offspring.
     * @param births Number of offspring to attempt to place.
     * @param into   The list to receive newly created actors.
     */
    public static void spawnConsumerOffspring(Actor parent, int births, List<Actor> into)
    {
        place(parent, births, into, (field, loc) -> {
            try {
                return (Actor) parent.getClass()
                        .getDeclaredConstructor(boolean.class, Field.class, Location.class)
                        .newInstance(true, field, loc);
            } catch (Exception e) {
                System.out.println("Error creating consumer offspring: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Instantiate and place Producer offspring.
     * Expects the concrete class to expose a {@code (Field, Location)} constructor.
     *
     * @param parent The parent actor producing offspring.
     * @param births Number of offspring to attempt to place.
     * @param into   The list to receive newly created actors.
     */
    public static void spawnProducerOffspring(Actor parent, int births, List<Actor> into)
    {
        place(parent, births, into, (field, loc) -> {
            try {
                return (Actor) parent.getClass()
                        .getDeclaredConstructor(Field.class, Location.class)
                        .newInstance(field, loc);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Shared placement loop: find free adjacent cells, call the factory for
     * each birth slot, and add non-null results to {@code into}.
     */
    private static void place(Actor parent, int births, List<Actor> into,
                               BiFunction<Field, Location, Actor> factory)
    {
        if (births == 0) return;
        Field field = parent.getField();
        List<Location> free = field.getFreeAdjacentLocations(parent.getLocation());
        for (int b = 0; b < births && !free.isEmpty(); b++)
        {
            Location loc = free.remove(0);
            Actor child = factory.apply(field, loc);
            if (child != null) into.add(child);
        }
    }

    private BirthManager() {}
}
