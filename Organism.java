/**
 * This class represents shared characteristics of organisms
 * in the simulation. 
 *
 * @version 2022.03.2
 */
public abstract class Organism {
    

    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    
    // Whether the organism has a disease 
    private Disease disease = null;
    private boolean hasDisease = false;


    protected abstract int FOOD_VALUE();

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
}
