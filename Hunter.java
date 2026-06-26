import java.util.*;

/**
 * A simple model of a hunter.
 * Hunters hunt all of the species in the simulation.
 *
 * A hunter occupies the field like an organism (position, movement) but is never
 * eaten, never breeds and never dies of natural causes - it simply roams,
 * killing adjacent prey.
 *
 * @version 2022.03.02
 */
public class Hunter extends Organism implements Actor
{
    // A hunter hunts every species.
    private static final Set<Class> DIET = new HashSet<>(Arrays.asList(Deer.class, Mouse.class, Wolf.class, Coyote.class, Eagle.class));

    /**
     * Creates a new hunter.
     * @param field The field currently occupied.
     * @param location  The location within the field.
     * @param environment The environment that the hunter resides in.
     */
    public Hunter(Field field, Location location, Environment environment)
    {
        super(field, location);
    }

    /**
     * A hunter is never eaten, so its food value is unused.
     */
    protected int getFoodValue()
    {
        return 0;
    }

    /**
     * Hunters are not killed by overcrowding.
     */
    @Override
    protected boolean diesFromOvercrowding()
    {
        return false;
    }

    /**
     * Makes the hunter hunt for nearby animals, then move.
     * @param newActors A list to receive new actors.
     * @param environment The environment that the hunter resides in.
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if(isAlive()) {
            moveToTargetOrWander(findPrey());
        }
    }

    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal is killed.
     * @return Location Where an animal was found, or null if it wasn't.
     */
    private Location findPrey()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal != null && DIET.contains(animal.getClass())) {
                Animal food = (Animal) animal;
                if(food.isAlive()) {
                    food.setDead();
                    return where;
                }
                return where;
            }
        }
        return null;
    }
}
