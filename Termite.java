import java.util.List;
import java.util.ArrayList;

/**
 * A simple model of a termite.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Termite extends Consumer
{
    // Constants shared by all termites (class variables):
    //   The likelihood of a termite breeding:
    private static final double BREEDING_PROBABILITY = 0.65;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 2;
    //   The worth of a termite if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which a termite can start to breed:
    private static final int BREEDING_AGE = 3;
    //   The age to which a termite can live:
    private static final int MAX_AGE = 200;
    //   Max sustenance level of the termite:
    private static final int MAX_SUSTENANCE_LEVEL = 50;
    //   The prey this termite hunts:
    private static final List<Class<?>> PREY
    = new ArrayList<>(List.of(RedOatGrass.class,StarGrass.class));

    /**
     * Create a new termite. A termite may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the termite will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Termite(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE,MAX_SUSTENANCE_LEVEL,false,true);
        
        setStartingAge(randomAge);
    }

    @Override
    protected Consumer createOffspring(Field field, Location location)
    {
        return new Termite(false, field, location);
    }
}
