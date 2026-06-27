import java.util.List;
import java.util.Random;

/**
 * A class representing all shared characteristics of organisms.
 * Organisms have an age, field and a location in the field.
 * All organisms are able to act and breed.
 *
 * @version 2022.03.02
 */
public abstract class Organism implements FieldOccupant
{
    // Shared lifecycle settings for this organism's species.
    private final OrganismAttributes attributes;
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
    public Organism(boolean randomAge, Field field, Location location,
                    OrganismAttributes attributes)
    {
        this.attributes = attributes;
        age = 0;
        if (randomAge) {
            age = rand.nextInt(attributes.getMaxAge());
        }
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    // Accessor and mutator methods

    /**
     * Return this organism's species.
     *
     * @return The organism species.
     */
    public Species getSpecies()
    {
        return attributes.getSpecies();
    }

    /**
     * Return whether the organism is diurnal or nocturnal.
     *
     * @return True if the organism is diurnal.
     */
    public boolean isDiurnal()
    {
        return attributes.isDiurnal();
    }
    
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
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    // Functional methods

    /**
     * Make this organism act for a single simulation step.
     *
     * @param newOrganisms A list to receive newly born organisms.
     */
    public final void act(List<Organism> newOrganisms)
    {
        incrementAge();
        if(isAlive()) {
            applyStepEffects();
        }
        if(isAlive()) {
            giveBirth(newOrganisms);
            relocate();
        }
    }
    
    /**
     * Determines the amount of newborn a specific organism will produce
     * 
     * @return the number of newborn produced by the organism
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && getRand().nextDouble() <= attributes.getBreedingProbability()) {
            births = getRand().nextInt(attributes.getMaxLitterSize()) + 1;
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
            Organism young = attributes.create(false, field, loc);
            newOrganisms.add(young);
        }
    }
    
    /**
     * Increases the age of the organism by one. If it goes over the max age, the organism dies.
     */
    protected void incrementAge()
    {
        age++;
        if (age > attributes.getMaxAge())
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

    /**
     * Default breeding rule for organisms that reproduce without a mate.
     *
     * @return True when the organism is old enough to breed.
     */
    protected boolean canBreed()
    {
        return getAge() >= attributes.getBreedingAge();
    }

    /**
     * Hook for species-specific per-step work before breeding and movement.
     */
    protected void applyStepEffects()
    {
    }

    /**
     * Hook for organisms that may move during a simulation step.
     *
     * @return The next location, or null to stay in place.
     */
    protected Location findNextLocation()
    {
        return null;
    }

    /**
     * Hook for species that die when they cannot move.
     *
     * @return True if the organism dies when no destination is available.
     */
    protected boolean diesWhenBlocked()
    {
        return false;
    }

    /**
     * Move the organism if its species requires relocation this step.
     */
    private void relocate()
    {
        Location newLocation = findNextLocation();
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else if(diesWhenBlocked()) {
            setDead();
        }
    }
}
