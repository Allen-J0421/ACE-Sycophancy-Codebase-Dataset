import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a alligator.
 * Alligators age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Alligator extends Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the alligator
    public static final String name = "Alligator";
    private static final AnimalProfile PROFILE = new AnimalProfile(
        name, 15, 200, 0.3, 5, 25, createPreyFoodValueMap(),
        false, true, true,
        new AnimalFactory() {
            public Animal create(Time time, Field field, Location location, Set<Disease> parentDiseases)
            {
                if(parentDiseases != null) {
                    return new Alligator(time, field, location, parentDiseases);
                }
                return new Alligator(time, field, location);
            }
        });

    /**
     * Create a alligator. The alligator is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this alligator had.
     */
    public Alligator(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, PROFILE, rand, parentDiseases);
    }

    /**
     * Create a alligator. The alligator is created with a random age and food level.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Alligator(Time time, Field field, Location location)
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
        mapTemp.put(Lemur.name, 8);
        mapTemp.put(Catfish.name, 4);
        return mapTemp;
    }

}
