import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a panther.
 * Pantheres age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Panther extends Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the panther
    public static final String name = "Panther";
    private static final AnimalProfile PROFILE = new AnimalProfile(
        name, 15, 150, 0.4, 4, 25, createPreyFoodValueMap(),
        true, true, false,
        new AnimalFactory() {
            public Animal create(Time time, Field field, Location location, Set<Disease> parentDiseases)
            {
                if(parentDiseases != null) {
                    return new Panther(time, field, location, parentDiseases);
                }
                return new Panther(time, field, location);
            }
        });
    
    /**
     * Create a panther. The panther is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this panther had.
     */
    public Panther(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, PROFILE, rand, parentDiseases);
    }

    /**
     * Create a panther. The panther is created with a random age and food level.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Panther(Time time, Field field, Location location)
    {
        super(time, field, location, PROFILE, rand);
    }

    /**
     * Create a Map with a key of the prey String name
     * and a value of the food level is given when eaten.
     * @return The prey food value Map.
     */
    private static Map<String, Integer> createPreyFoodValueMap()
    {
        Map<String,Integer> mapTemp = new HashMap<>();
        mapTemp.put("Lemurs", 10);
        return mapTemp;
    }

}
