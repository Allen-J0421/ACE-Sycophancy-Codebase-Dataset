import java.util.List;

/**
 * A simple model of an aardvark.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Aardvark extends Consumer
{
    // Constants shared by all aardvarks (class variables):
    //   The likelihood of an aardvark breeding:
    private static final double BREEDING_PROBABILITY = 0.73;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 3;
    //   The worth of an aardvark if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which an aardvark can start to breed:
    private static final int BREEDING_AGE = 15;
    //   The age to which an aardvark can live:
    private static final int MAX_AGE = 120;
    //    Max sustenance level of the aadvark:
    private static final int MAX_SUSTENANCE_LEVEL = 50;
    //   The prey this aardvark hunts:
    private static final List<Class<? extends Actor>> PREY = List.of(Grasshopper.class, HarvesterAnt.class);

    /**
     * Create a new aardvark. A aardvark may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the aardvark will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Aardvark(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE, MAX_SUSTENANCE_LEVEL, true);
        
        setStartingAge(randomAge);
    }

    protected Actor createChild(Field field, Location location)
    {
        return new Aardvark(false, field, location);
    }
}
