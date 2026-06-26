import java.util.Iterator;
import java.util.List;

/**
 * A simple model of weather.
 *
 * An underwater storm occurs intermittently, killing all creatures within its scope.
 *
 * @version 2022/03/02
 */
public class Weather
{
    // The radius of a storm: the storm area extends stormScope cells in each direction.
    public static final int STORM_SCOPE = 3;

    private Field field;
    private Location stormCenter;
    private boolean stormActive;

    /**
     * Create a weather object for the given field.
     */
    public Weather(Field field)
    {
        this.field = field;
    }

    /**
     * Generate an underwater storm at a random location, killing all creatures within STORM_SCOPE.
     */
    public void underwaterStorm()
    {
        stormCenter = field.generateRandomLocation();
        List<Object> adjacentObjectList = field.getAllObjectAt(stormCenter, STORM_SCOPE);
        for(Object obj : adjacentObjectList) {
            if(obj instanceof Creature) {
                ((Creature) obj).setDead();
            }
        }
    }

    /**
     * Return the location of the most recent storm centre, or null if no storm occurred yet.
     */
    public Location getStormCenter()
    {
        return stormCenter;
    }

    /**
     * Return true if a storm is active this step.
     */
    public boolean isStormActive()
    {
        return stormActive;
    }

    /**
     * Set whether a storm is active this step.
     */
    public void setStormActive(boolean active)
    {
        this.stormActive = active;
    }
}
