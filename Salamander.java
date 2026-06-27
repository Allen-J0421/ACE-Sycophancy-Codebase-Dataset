import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a salamander.
 * Salamanders age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Salamander extends Animal
{
    // Characteristics shared by all salamanders (class variables).

    // The age at which a salamander can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a salamander can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a salamander breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the salamander. In effect, this is the
    // number of steps a salamander can go before it has to eat again.
    private static final int MAX_FOOD = 7;
    // String name for the salamander
    public static final String name = "Salamander";

    /**
     * Create a salamander. The salamander is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this salamander had.
     */
    public Salamander(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        initializeNewbornState(false, true, true, MAX_FOOD, parentDiseases);
    }

    public Salamander(Time time, Field field, Location location)
    {
        super(time, field, location);
        initializeRandomStartState(false, true, true, MAX_AGE, MAX_FOOD, name);
    }

    /**
     * Create a Map with a key of the prey String name
     * and a value of the food level is given when eaten.
     * @return The prey food value Map.
     */
    private static Map<String, Integer> createPreyFoodValueMap()
    {
        Map<String,Integer> mapTemp = new HashMap<>();
        mapTemp.put("Grass", 4);
        mapTemp.put("Water_Fern", 5);
        return mapTemp;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        return breedOffspringCount(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, rand);
    }

    /**
     * Returns the String name.
     * @return The String name.
     */
    public String getActorName (){
        return name;
    }

    /**
     * Returns the max food level that the salamander can have.
     * @return The max food level that the salamander can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the salamander can have before dying.
     * @return The max age that the salamander can have before dying.
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
     * Creates a new salamander 
     * If the salamander is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new salamander created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Salamander(getTime(), getField(), loc, parentDiseases);
    }
}
