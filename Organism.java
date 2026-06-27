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
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for objects of class Organism
     */
    public Organism(boolean randomAge, Field field, Location location)
    {
        age = 0;
        if (randomAge) {
            age = rand.nextInt(getMaxAge());
        }
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    // Abstract methods
    
    /**
     * Abstract method that returns whether the animal is diurnal or nocturnal
     * 
     * @return boolean True if the animal is diurnal, false if nocturnal
     */
    abstract protected boolean getIsDiurnal();
    
    /**
     * Make this organism act for one step of the simulation. This defines the
     * lifecycle skeleton shared by every organism: it ages, updates its health,
     * possibly suffers from and spreads any infection, breeds, and finally moves.
     * The individual steps after aging are overridable hooks that default to doing
     * nothing, so a plain organism (e.g. a plant) only ages and breeds, while
     * animals layer in health, infection and movement.
     *
     * @param nursery Collects any organisms born during this step.
     */
    public void act(Nursery nursery)
    {
        incrementAge();
        updateHealth();
        if (isAlive()) {
            applyIllness();
        }
        if (isAlive()) {
            attemptSpreadDisease();
            giveBirth(nursery);
            move();
        }
    }

    /**
     * Lifecycle hook: update the organism's health for this step. Does nothing by
     * default; animals override this to apply hunger.
     */
    protected void updateHealth() { }

    /**
     * Lifecycle hook applied while the organism is alive, before it acts. Does
     * nothing by default; infectable animals override this to suffer illness.
     */
    protected void applyIllness() { }

    /**
     * Lifecycle hook applied while the organism is alive, before it breeds. Does
     * nothing by default; infectious animals override this to spread disease.
     */
    protected void attemptSpreadDisease() { }

    /**
     * Lifecycle hook: move the organism after it has bred. Does nothing by default
     * (e.g. plants are stationary); animals override this to hunt and relocate.
     */
    protected void move() { }

    /**
     * Abstract method that returns the max age of the organism
     * 
     * @return the organism's max age
     */
    abstract protected int getMaxAge();
    
    /**
     * Abstract method that returns whether the organism can breed
     * 
     * @return boolean True if the organism can breed
     */
    abstract protected boolean canBreed();
    
    /**
     * Abstract method that returns the breeding age for the animal
     * 
     * @return the animal's breeding age
     */
    abstract protected int getBreedingAge();
    
    /**
     * Abstract methohd that returns the litter size of the animal
     * 
     * @return the animal's max litter size
     */
    abstract protected int getMaxLitterSize();
    
    /**
     * Abstract method that returns the breeding probability of the animal
     * 
     * @return the animal's breeding probability
     */
    abstract protected double getBreedingProbability();
    
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
     * Breeds this organism and registers any newborns with the shared nursery.
     * @param nursery Collects the organisms born during this step.
     */
    protected void giveBirth(Nursery nursery)
    {
        // New organisms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Organism young = createNewOrganism(false, field, loc);
            nursery.register(young);
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