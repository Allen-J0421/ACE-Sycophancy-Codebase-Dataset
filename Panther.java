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
    private static final Map<Class<? extends Actor>, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the panther. In effect, this is the
    // number of steps a panther can go before it has to eat again.
    private static final int MAX_FOOD = 25;
    /**
     * Create a panther. The panther is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this panther had.
     */
    private Panther(Time time, Field field, Location location, int age, int foodLevel)
    {
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        canGoLand = true;
        canGoWater = false;
        this.age = age;
        this.foodLevel = foodLevel;
    }

    public Panther(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        this(time, field, location, 0, MAX_FOOD);
        inheritDiseases(parentDiseases);
    }

    public Panther(Time time, Field field, Location location)
    {
        this(time, field, location, rand.nextInt(MAX_AGE), rand.nextInt(MAX_FOOD) + 1);
        initStartingDiseases(Simulator.diseases);
    }

    /**
     * Create a Map with a key of the prey class
     * and a value of the food level is given when eaten.
     * @return The prey food value Map.
     */
    private static Map<Class<? extends Actor>, Integer> createPreyFoodValueMap()
    {
        Map<Class<? extends Actor>, Integer> mapTemp = new HashMap<>();
        mapTemp.put(Lemur.class, 10);
        return mapTemp;
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

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
    public Map<Class<? extends Actor>, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    public Animal spawnOffspring(Location loc, Set<Disease> parentDiseases)
    {
        return new Panther(getTime(), getField(), loc, parentDiseases);
    }

    public Animal spawnRandom(Location loc)
    {
        return new Panther(getTime(), getField(), loc);
    }
}
