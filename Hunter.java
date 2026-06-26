import java.util.*;

/**
 * A simple model of a hunter.
 * Hunters hunt all animal species in the simulation.
 *
 * @version 2022.03.02
 */
public class Hunter extends Organism implements Actor
{
    private static final Random rand = Randomizer.getRandom();

    // Hunters cannot be eaten, so they contribute no food value.
    @Override
    protected int FOOD_VALUE() { return 0; }

    // A hunter preys on every animal species.
    private static final Set<Class> DIET = Set.of(
        Deer.class, Mouse.class, Wolf.class, Coyote.class, Eagle.class
    );

    /**
     * Creates a new hunter at the given location.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Makes the hunter hunt for nearby animals.
     * @param newActors A list to receive new actors (unused — hunters do not reproduce).
     * @param environment The environment that the hunter resides in.
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if (isAlive()) {
            Location newLocation = findPrey();
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            if (newLocation != null) {
                setLocation(newLocation);
            } else if (adjacentGrassSpots.size() > 0) {
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }
        }
    }

    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal found is killed.
     * @return The location where an animal was found, or null if none.
     */
    private Location findPrey()
    {
        for (Location where : getField().adjacentLocations(getLocation())) {
            Object obj = getField().getObjectAt(where);
            if (obj != null && DIET.contains(obj.getClass())) {
                Animal prey = (Animal) obj;
                if (prey.isAlive()) {
                    prey.setDead();
                }
                return where;
            }
        }
        return null;
    }
}
