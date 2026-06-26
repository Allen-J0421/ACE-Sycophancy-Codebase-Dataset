import java.util.List;

/**
 * A simple model of an impala.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Impala extends Consumer
{
    // Constants shared by all impala (class variables):
    //   The likelihood of an impala breeding:
    private static final double BREEDING_PROBABILITY = 0.65;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 3;
    //   The worth of an impala if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which an impala can start to breed:
    private static final int BREEDING_AGE = 5;
    //   The age to which an impala can live:
    private static final int MAX_AGE = 130;
    //   Max sustenance level of the impala:
    private static final int MAX_SUSTENANCE_LEVEL = 55;
    //   The prey this impala hunts:
    private static final List<Class<? extends Actor>> PREY = List.of(Acacia.class);

    /**
     * Create a new impala. A impala may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the impala will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Impala(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE, MAX_SUSTENANCE_LEVEL, false);
        
        setStartingAge(randomAge);
    }
}
