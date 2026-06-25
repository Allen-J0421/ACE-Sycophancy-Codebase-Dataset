import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

/**
 * A class representing shared characteristics of all living beings, animals and plants
 *
 * @version 2022.02.24
 */
public abstract class LivingBeing
{
    // Whether the being is alive or not.
    private boolean alive;
    // The being's field.
    private Field field;
    // The being's position in the field.
    private Location location;
    //Checks to see if it is night time or not
    protected static boolean nightTime;

    /**
     * Constructor for objects of class LivingBeing
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public LivingBeing(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);

        nightTime = false;

    }

    /**
     * Returns if it is night time or not
     * @return true if it is night
     */
    protected boolean isNight() {
        return nightTime;
    }

    /**
     * Switches between night time and day time
     */
    protected void switchNight() {
        nightTime = !nightTime;
    }

    /**
     * Return the being's location.
     * @return The being's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the being at the new location in the given field.
     * @param newLocation The being's new location.
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
     * Return the being's field.
     * @return The being's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Check whether the being is alive or not.
     * @return true if the being is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Make this being act - that is: make it do
     * whatever it wants/needs to do.
     * @param newBeings A list to receive newly born beings.
     */
    abstract public void act(List<LivingBeing> newBeings);

    /**
     * Indicate that the being is no longer alive.
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

}
