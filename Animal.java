import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // A shared random number generator used by legacy animal classes:
    protected static final Random rand = Randomizer.getRandom();
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Move the animal to a new location, or die if there is nowhere to go.
     *
     * @param newLocation The target location, or null if none is available.
     * @return True if the animal moved successfully.
     */
    protected boolean moveTo(Location newLocation)
    {
        if (newLocation == null)
        {
            setDead();
            return false;
        }

        setLocation(newLocation);
        return true;
    }

    /**
     * Generate a birth count using the standard legacy animal breeding rules.
     *
     * @param age The animal's current age.
     * @param breedingAge The minimum age required to breed.
     * @param breedingProbability The probability of breeding.
     * @param maxLitterSize The maximum number of births.
     * @return The number of births to create.
     */
    protected int breed(int age, int breedingAge, double breedingProbability, int maxLitterSize)
    {
        if (age >= breedingAge && rand.nextDouble() <= breedingProbability)
        {
            return rand.nextInt(maxLitterSize) + 1;
        }
        return 0;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
