import java.util.List;

/**
 * A simple model of a mongoose.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Mongoose extends Consumer
{
    // Constants shared by all mongoose (class variables):
    //   The likelihood of a mongoose breeding:
    private static final double BREEDING_PROBABILITY = 0.61;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 3;
    //   The worth of a mongoose if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which a mongoose can start to breed:
    private static final int BREEDING_AGE = 3;
    //   The age to which a mongoose can live:
    private static final int MAX_AGE = 100;
    //   Max sustenance level of the mongoose:
    private static final int MAX_SUSTENANCE_LEVEL = 40;
    //   The prey this mongoose hunts:
    private static final List<Class<?>> PREY = List.of(Grasshopper.class, HarvesterAnt.class);

    /**
     * Create a new mongoose. A mongoose may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the mongoose will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Mongoose(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE, MAX_SUSTENANCE_LEVEL, true);
        
        setStartingAge(randomAge);
    }

    @Override
    protected Consumer createOffspring(Field field, Location location)
    {
        return new Mongoose(false, field, location);
    }
}