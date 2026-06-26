/**
 * A simple model of red oat grass grass.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class RedOatGrass extends Producer
{
    // Constants shared by all red oat grass (class variables):
    //   The likelihood of red oat grass breeding:
    private static final double BREEDING_PROBABILITY = 0.04;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 1;
    //   The worth of red oat grass if consumed:
    private static final int CONSUMPTION_WORTH = 3;
    //   Max Age of the red oat grass:
    private static final int MAX_AGE = 70;
    
    /**
     * Create red oat grass.
     * 
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public RedOatGrass(Field field, Location location)
    {
        super(field, location, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, RedOatGrass::new);
    }
}
