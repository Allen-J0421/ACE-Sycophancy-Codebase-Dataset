import java.util.List;
import java.util.ArrayList;
/**
 * A simple model of a grasshopper.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Grasshopper extends Consumer
{
    // Constants shared by all grasshoppers (class variables):
    //   The likelihood of a grasshopper breeding:
    private static final double BREEDING_PROBABILITY = 0.60;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 2;
    //   The worth of a grasshopper if consumed:
    private static final int CONSUMPTION_WORTH = 50;
    //   The age at which a grasshopper can start to breed:
    private static final int BREEDING_AGE = 1;
    //   The age to which a grasshopper can live:
    private static final int MAX_AGE = 100;
    //   Max sustenance level of the grasshopper:
    private static final int MAX_SUSTENANCE_LEVEL = 150;
    //   The prey this grasshopper hunts:
    private static final List<Class<?>> PREY
    = new ArrayList<>(List.of(StarGrass.class));
    /**
     * Create a new grasshopper. A grasshopper may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge       If true, the grasshopper will have a random age.
     * @param field           The field currently occupied.
     * @param location        The location within the field.
     */
    public Grasshopper(boolean randomAge, Field field, Location location)
    {
        super(field, location, PREY, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE, BREEDING_AGE,MAX_SUSTENANCE_LEVEL,false,true);
        
        setStartingAge(randomAge);
    }
    
}
