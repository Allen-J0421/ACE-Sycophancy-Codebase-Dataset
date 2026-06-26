import java.util.List;
import java.util.Random;

/**
 * This class represents shared characteristics of organisms
 * in the simulation.
 *
 * @version 2022.03.2
 */
public abstract class Organism implements Actor {
    protected static final Random rand = Randomizer.getRandom();
    

    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    
    // Whether the organism has a disease 
    private Disease disease = null;
    private boolean hasDisease = false;


    /**
     * Creates a new organism. 
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
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
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
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
     * Returns true if the organism is infected. 
     */
    public boolean isDiseased()
    {
        return hasDisease;
    }

    /**
     * Returns the organism's disease.
     * @return Disease The organism's disease.
     */
    public Disease getDisease()
    {
        return disease;
    }

    /**
     * Sets the organism to be infected.
     * @param disease The disease that the organism is gonna be infected by. 
     */
    protected void setDisease(Disease disease)
    {
        this.disease = disease;
        hasDisease = true;
    }

    /**
     * Template method for target acquisition and movement.
     */
    protected final void forageAndMove(Environment environment)
    {
        Location targetLocation = locateTargetLocation(environment);
        if(targetLocation == null) {
            targetLocation = getField().freeAdjacentLocation(getLocation());
        }

        if(targetLocation != null) {
            setLocation(targetLocation);
            return;
        }

        List<Location> fallbackLocations = getFallbackLocations();
        if(!fallbackLocations.isEmpty()) {
            getField().clear(getLocation());
            setLocation(fallbackLocations.get(rand.nextInt(fallbackLocations.size())));
            return;
        }

        onMovementBlocked();
    }

    /**
     * Find the resource or prey the organism is seeking.
     */
    protected Location locateTargetLocation(Environment environment)
    {
        return null;
    }

    /**
     * Hook for species-specific fallback movement targets.
     */
    protected List<Location> getFallbackLocations()
    {
        return getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);
    }

    /**
     * Hook for what to do when movement is impossible.
     */
    protected void onMovementBlocked()
    {
        setDead();
    }

    /**
     * Default actor behavior must be provided by subclasses.
     */
    public abstract void act(List<Actor> newActors, Environment environment);
}
