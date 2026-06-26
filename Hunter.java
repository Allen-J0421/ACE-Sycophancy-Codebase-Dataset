import java.util.*;

/**
 * A simple model of a hunter. 
 * Hunters hunt all of the species in the simulation.
 * @version 2022.03.02
 */
public class Hunter implements Actor
{
    private boolean alive;
    private Field field;
    private Location location;

    private Random rand = Randomizer.getRandom();
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
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Returns true if the hunter is alive. 
     */
    public boolean isAlive() 
    {
        return alive;
    }

    /**
     * Makes the hunter hunt for nearby animals. 
     * @param environment The environment that the hunter resides in. 
     * @param newActors A list to receive new actors. 
     */
    public void act(List<Actor> newActors, Environment environment)
    {
        if(isAlive()) {
            Location newLocation = findPrey();
            if(newLocation == null) {
                // No animals found - try to move to a free location.
                newLocation = field.freeAdjacentLocation(location);
            }
            List<Location> adjacentGrassSpots = field.adjacentLocationsWithSpecies(location, Grass.class);

            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else if (adjacentGrassSpots.size() > 0) {
                field.clear(location);
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }

        }
    }

    /**
     * Place the hunter at the new location in the given field.
     * @param newLocation The hunter's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }


    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal is killed.
     * @return Location Where an animal was found, or null if it wasn't.
     */
    private Location findPrey()
    {
        Field field = this.field;
        List<Location> adjacent = field.adjacentLocations(location);
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
