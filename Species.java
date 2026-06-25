import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of species.
 *
 * 
 * @version 2022.02.28
 */
public abstract class Species 
{
    // Characteristics shared by all species (class variables).

    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // Whether the species is alive or not.
    private boolean alive;
    // The species's field.
    private Field field;
    // The species's position in the field.
    private Location location;

    // The species's age.
    protected int age;
    // The species's max age.
    protected int maxAge;
    // The species's gender. 0 is female 1 is male
    protected int gender;
    // The species's move and search range
    protected int speed;
    // How much time species failed to breed
    protected int failBreed = 0;

    /**
     * Create a new species at location in field.
     * 
     * @param randomAge If true, the species will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param maxAge The age to which an species can live.
     */
    public Species(boolean randomAge, Field field, Location location, int maxAge)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge) {
            age = rand.nextInt(maxAge);
        }
        else {
            age = 0;
        }

        gender = rand.nextInt(2);
        speed = 0;
    }
    
    /**
     * Make this species act - that is: make it do
     * whatever it wants/needs to do.
     * @param newSpecies A list to receive newly born species.
     */
    abstract public void act(List<Species> newSpecies, boolean isDay);
    
    /**
     * Check whether the species is alive or not.
     * @return true if the species is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the species is no longer alive.
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
     * Return the species's location.
     * @return The species's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the species at the new location in the given field.
     * @param newLocation The species's new location.
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
     * Return the species's field.
     * @return The species's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Return the species's gender.
     * @return The species's gender.
     */
    protected int getGender()
    {
        return gender;
    }
    
    /**
     * Increase the age.
     * This could result in the species's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * @param breadingAge The age at which an animal can start to breed.
     * @param probability The likelihood of an animal breeding.
     * @param maxLitterSize The maximum number of births.
     */
    protected void giveBirth(List<Species> newAnimals, int breadingAge, 
        double probability, int maxLitterSize)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> free = getBirthLocations(getLocation());
        int births = breed(breadingAge, probability, maxLitterSize);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Species young = spawn(false, field, loc);
            newAnimals.add(young);
        }
    }

    /**
     * Get locations where can birth
     */
    abstract protected List<Location> getBirthLocations(Location location);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @param breadingAge The age at which an species can start to breed.
     * @param probability The likelihood of an species breeding.
     * @param maxLitterSize The maximum number of births.
     * @return The number of births (may be zero).
     */
    protected int breed(int breadingAge, double probability, int maxLitterSize)
    {
        int births = 0;
        if(canBreed(breadingAge) && rand.nextDouble() <= probability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }
    
    /**
     * An species can breed if it has reached the breeding age.
     * @return true if the species can breed, false otherwise.
     */
    protected boolean canBreed(int breedingAge)
    {
        return age >= breedingAge && findCouple();
    }
    
    /**
     * Check if an species has couple in adjacent locations.
     * @return true if the species has.
     */
    protected boolean findCouple()
    {
        if(field == null){
            return false;
        }
        // find couple in larger range if failed breed
        List<Location> adjacent = field.adjacentLocations(getLocation(), failBreed * speed + 1);
        for(Location where : adjacent) {
            Species species = (Species)field.getObjectAt(where);
            if(species != null && species.getClass() == this.getClass() && species.gender == gender){
                failBreed = 0;
                return true;
            }
        }
        failBreed++;
        return false;
    }

    
    /**
     * Create a new species at location in field.
     * 
     * @param randomAge If true, the species will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public abstract Species spawn(boolean randomAge, Field field, Location location);
}
