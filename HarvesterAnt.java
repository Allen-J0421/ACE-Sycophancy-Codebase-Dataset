import java.util.List;

/**
 * A simple model of a harvester ant.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class HarvesterAnt extends Consumer
{
    // Constants shared by all termites (class variables):
    //   The likelihood of a termite breeding:
    private static final double BREEDING_PROBABILITY = 0.5;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 3;
    //   The worth of a termite if consumed:
    private static final int CONSUMPTION_WORTH = 40;
    //   The age at which a termite can start to breed:
    private static final int BREEDING_AGE = 1;
    //   The age to which a termite can live:
    private static final int MAX_AGE = 200;
    //   Max sustenance level of the termite:
    private static final int MAX_SUSTENANCE_LEVEL = 50;
    //   The prey this termite hunts:
    private static final List<Class<? extends Actor>> PREY = List.of(RedOatGrass.class, StarGrass.class);

    /**
     * Create a new harvester ant. A harvester ant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the harvester ant will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public HarvesterAnt(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE, MAX_SUSTENANCE_LEVEL, false);
        
        setStartingAge(randomAge);
    }

    protected Actor createChild(Field field, Location location)
    {
        return new HarvesterAnt(false, field, location);
    }
}
