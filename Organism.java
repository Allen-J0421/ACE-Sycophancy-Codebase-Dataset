import java.util.List;
import java.util.Random;

/**
 * A class representing all shared characteristics of organisms.
 * Organisms have an age, field and a location in the field.
 * All organisms are able to act and breed.
 *
 * @version 2022.03.02
 */
public abstract class Organism
{
    // The organism's age
    private int age;
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    // Species-specific lifecycle characteristics.
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final boolean isDiurnal;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for objects of class Organism
     */
    public Organism(boolean randomAge, Field field, Location location,
            int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, boolean isDiurnal)
    {
        age = 0;
        if (randomAge) {
            age = rand.nextInt(maxAge);
        }
        alive = true;
        this.field = field;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.isDiurnal = isDiurnal;
        setLocation(location);
    }

    // Abstract methods
    
    /**
    * Make this organism act - that is: make it do
    * whatever it wants/needs to do.
    * @param newOrganisms A list to receive newly born organisms.
    */
    abstract public void act(List<Organism> newOrganisms);
    
    /**
     * Returns whether the organism can breed.
     *
     * @return True if the organism can breed.
     */
    abstract protected boolean canBreed();
    
    /**
     * Abstract method that returns an organism object of a specific subclass of organism
     * 
     * @return Organism object of a specified subclass
     */
    abstract protected Organism createNewOrganism(boolean randomAge, Field field, Location location);
    
    // Accessor and mutator methods
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Returns the random object of the organism
     * 
     * @return Random object of the organism
     */
    protected Random getRand() {
        return rand;
    }

    /**
     * Returns whether the organism is diurnal.
     *
     * @return True if the organism is diurnal, false if nocturnal.
     */
    protected boolean getIsDiurnal() {
        return isDiurnal;
    }
    
    /**
     * Returns the age of the organism
     * 
     * @return The organism's age
     */
    public int getAge()
    {
        return age;
    }
    
    /**
     * Sets the organism's age
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /**
     * Returns the breeding age for the organism.
     *
     * @return the breeding age.
     */
    protected int getBreedingAge()
    {
        return breedingAge;
    }

    /**
     * Returns the max age for the organism.
     *
     * @return the max age.
     */
    protected int getMaxAge()
    {
        return maxAge;
    }

    /**
     * Returns the breeding probability for the organism.
     *
     * @return the breeding probability.
     */
    protected double getBreedingProbability()
    {
        return breedingProbability;
    }

    /**
     * Returns the maximum litter size for the organism.
     *
     * @return the maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return maxLitterSize;
    }
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    // Functional methods
    
    /**
     * Determines the amount of newborn a specific organism will produce
     * 
     * @return the number of newborn produced by the organism
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && getRand().nextDouble() <= getBreedingProbability()) {
            births = getRand().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
    
    /**
     * Adds newborn organisms to a list
     */
    protected void giveBirth(List<Organism> newOrganisms)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Organism young = createNewOrganism(false, field, loc);
            newOrganisms.add(young);
        }
    }
    
    /**
     * Increases the age of the organism by one. If it goes over the max age, the organism dies.
     */
    protected void incrementAge()
    {
        age++;
        if (age > getMaxAge())
        {
            setDead();
        }
    }
    
    /**
     * Indicate that the organism is no longer alive.
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
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
}
