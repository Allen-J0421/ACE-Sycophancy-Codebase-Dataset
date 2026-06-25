import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of star grass.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class StarGrass extends Producer
{
    // Constants shared by all star grass (class variables):
    //   The likelihood of star grass breeding:
    private static final double BREEDING_PROBABILITY = 0.04;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 1;
    //   The worth of star grass if consumed:
    private static final int CONSUMPTION_WORTH = 2;
    //   Max age of the star grass:
    private static final int MAX_AGE = 70;
    
    /**
     * Create star grass.
     * 
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public StarGrass(Field field, Location location)
    {
        super(field, location, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE,MAX_AGE);
    }
    
    /**
     * Make this star grass act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newStarGrass A list to return newly born star grass.
     */
    public void act(List<Actor> newStarGrass)
    {
        super.act(newStarGrass);
    }
}
