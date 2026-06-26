import java.util.List;
import java.util.Random;

/**
 * This class represents shared characteristics of organisms
 * in the simulation.
 *
 * @version 2022.03.2
 */
public abstract class Organism {

    // A shared random number generator for movement and other behaviour.
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


    protected abstract int getFoodValue();

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
     * Whether this organism dies when it cannot find anywhere to move (i.e. it
     * is overcrowded). Mobile organisms that should survive overcrowding (apex
     * species, hunters) override this.
     * @return true if overcrowding is fatal.
     */
    protected boolean diesFromOvercrowding()
    {
        return true;
    }

    /**
     * Move to the given target location; if there is none, wander to a free
     * adjacent cell, or onto adjacent grass, or (if nothing is available and the
     * organism dies from overcrowding) die in place.
     * @param target The preferred destination, or null if none was found.
     */
    protected void moveToTargetOrWander(Location target)
    {
        if(target == null) {
            // No target found - try to move to a free location.
            target = getField().freeAdjacentLocation(getLocation());
        }

        // adjacent locations that contain an instance of Grass
        List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

        if(target != null) {
            // See if it was possible to move.
            setLocation(target);
        }
        else if(adjacentGrassSpots.size() > 0) {
            // if there is grass adjacent, clear the current location and move
            // onto a random location that contained grass
            getField().clear(getLocation());
            setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
        }
        else if(diesFromOvercrowding()) {
            // Overcrowding
            setDead();
        }
    }
}
