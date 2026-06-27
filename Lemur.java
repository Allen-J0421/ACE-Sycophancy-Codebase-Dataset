import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a lemur.
 * Lemurs age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Lemur extends Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the lemur
    public static final String name = "Lemur";
    private static final AnimalProfile PROFILE = new AnimalProfile(
        name, 6, 20, 0.7, 5, 9, createPreyFoodValueMap(),
        false, true, false,
        new AnimalFactory() {
            public Animal create(Time time, Field field, Location location, Set<Disease> parentDiseases)
            {
                if(parentDiseases != null) {
                    return new Lemur(time, field, location, parentDiseases);
                }
                return new Lemur(time, field, location);
            }
        });

    /**
     * Create a lemur. The lemur is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this lemur had.
     */
    public Lemur(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, PROFILE, rand, parentDiseases);
    }

    /**
     * Create a lemur. The lemur is created with a random age and food level.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lemur(Time time, Field field, Location location)
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
        mapTemp.put("Grass", 3);
        mapTemp.put("Salamander",4);
        return mapTemp;
    }

}
