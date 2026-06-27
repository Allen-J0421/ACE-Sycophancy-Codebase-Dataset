import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a catfish.
 * Catfishs age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Catfish extends Animal
{
    // Characteristics shared by all catfishs (class variables).

    // The age at which a catfish can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a catfish can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a catfish breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of all prey.
    private static final Map<Class<? extends Actor>, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the catfish. In effect, this is the
    // number of steps a catfish can go before it has to eat again.
    private static final int MAX_FOOD = 7;
    /**
     * Create a catfish. The catfish is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this catfish had.
     */
    private Catfish(Time time, Field field, Location location, int age, int foodLevel)
    {
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        canGoLand = false;
        canGoWater = true;
        this.age = age;
        this.foodLevel = foodLevel;
    }

    public Catfish(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        this(time, field, location, 0, MAX_FOOD);
        inheritDiseases(parentDiseases);
    }

    public Catfish(Time time, Field field, Location location)
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
        mapTemp.put(Water_Fern.class, 3);
        return mapTemp;
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /**
     * Returns the max food level that the catfish can have.
     * @return The max food level that the catfish can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the catfish can have before dying.
     * @return The max age that the catfish can have before dying.
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
        return new Catfish(getTime(), getField(), loc, parentDiseases);
    }

    public Animal spawnRandom(Location loc)
    {
        return new Catfish(getTime(), getField(), loc);
    }
}

