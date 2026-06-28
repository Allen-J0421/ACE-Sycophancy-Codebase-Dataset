import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a pangolin.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Pangolin extends Consumer
{
    // Constants shared by all pangolins (class variables):
    //   The likelihood of a pangolin breeding:
    private static final double BREEDING_PROBABILITY = 0.72;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 3;
    //   The worth of a pangolin if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which a pangolin can start to breed:
    private static final int BREEDING_AGE = 15;
    //   The age to which a pangolin can live:
    private static final int MAX_AGE = 130;
    //   Max sustenance level of the pangolin:
    private static final int MAX_SUSTENANCE_LEVEL = 50;
    //   The prey this pangolin hunts:
    private static final List<Class<?>> PREY
    = new ArrayList<>(List.of(Termite.class,Impala.class));

    /**
     * Create a new pangolin. A pangolin may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the pangolin will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Pangolin(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE,MAX_SUSTENANCE_LEVEL,true,false);
        
        setStartingAge(randomAge);
    }

    @Override
    protected Consumer createOffspring(Field field, Location location)
    {
        return new Pangolin(false, field, location);
    }
}