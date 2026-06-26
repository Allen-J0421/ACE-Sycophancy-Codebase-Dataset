import java.util.*;

/**
 * A simple model of a hunter. 
 * Hunters hunt all of the species in the simulation.
 * @version 2022.03.02
 */
public class Hunter extends Organism implements Actor
{
    // A hunter hunts every species. 
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(
            Deer.class,
            Mouse.class,
            Wolf.class,
            Coyote.class,
            Eagle.class
    ));


    /**
     * Creates a new hunter.
     * @param field The field currently occupied.
     * @param location  The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field.getRandomProvider(), field, location);
    }

    @Override
    protected int getFoodValue()
    {
        return 0;
    }

    /**
     * Makes the hunter hunt for nearby animals. 
     * @param environment The environment that the hunter resides in. 
     * @param newActors A list to receive new actors. 
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        Random rand = getRandomProvider().getRandom();
        if(isAlive()) {
            Location newLocation = findPrey();
            if(newLocation == null) {
                // No animals found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else if (adjacentGrassSpots.size() > 0) {
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }

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
        Location currentLocation = getLocation();
        List<Location> adjacent = field.adjacentLocations(currentLocation);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal != null && DIET.contains(animal.getClass())) {
                Organism food = (Organism) animal;
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
