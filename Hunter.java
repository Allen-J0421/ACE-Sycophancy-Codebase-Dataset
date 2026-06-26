import java.util.*;

/**
 * A simple model of a hunter. 
 * Hunters hunt all of the species in the simulation.
 * @version 2022.03.02
 */
public class Hunter extends MobileForager
{
    // A hunter hunts every species. 
    private static final Set<Class<?>> DIET = Set.of(Deer.class, Mouse.class, Wolf.class, Coyote.class, Eagle.class);


    /**
     * Creates a new hunter.
     * @param field The field currently occupied.
     * @param location  The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Makes the hunter hunt for nearby animals. 
     * @param environment The environment that the hunter resides in. 
     * @param newActors A list to receive new actors. 
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if(isAlive()) {
            forageAndMove();
        }
    }

    @Override
    protected Location locateTargetLocation()
    {
        return findPrey();
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

    @Override
    protected void onMovementBlocked()
    {
        // Hunters stay put if they cannot move.
    }

}
