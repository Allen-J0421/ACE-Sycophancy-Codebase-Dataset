import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;

/**
 * A simple model of a panther.
 * Pantheres age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public final class Panther extends Animal
{
    // Characteristics shared by all pantheres (class variables).

    // The age at which a panther can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a panther can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a panther breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the panther. In effect, this is the
    // number of steps a panther can go before it has to eat again.
    private static final int MAX_FOOD = 25;
    // String name for the panther
    public static final String name = "Panther";
    
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
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        canGoLand = true;
        canGoWater = false;
        age = 0;
        foodLevel = MAX_FOOD;
        inheritDiseases(setDiseases, parentDiseases);
        setLocation(location);
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
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        canGoLand = true;
        canGoWater = false;
        age = rand.nextInt(MAX_AGE);
        foodLevel = rand.nextInt(MAX_FOOD)+1;
        addStartingDiseases(name, setDiseases, Simulator.diseases, rand);
        setLocation(location);
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
        return Collections.unmodifiableMap(mapTemp);
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        return breedIfAble(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, rand);
    }

    /**
     * Returns the String name.
     * @return The String name.
     */
    public String getActorName (){
        return name;
    }

    /**
     * Returns the max food level that the panther can have.
     * @return The max food level that the panther can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the panther can have before dying.
     * @return The max age that the panther can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the prey food values Map.
     * @return The prey food values Map.
     */
    public Map<String, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    /**
     * Creates a new panther 
     * If the panther is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param loc The new location of the child
     * @param parentDiseases The diseases that the parent had is passed down
     * @return The new panther created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Panther(getTime(), getField(), loc, parentDiseases);
    }

    /**
     * Creates a new panther without parent diseases.
     * @param loc The new location of the panther
     * @return The new panther created
     */
    public Animal birth(Location loc)
    {
        return new Panther(getTime(), getField(), loc);
    }
}
