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
    // Characteristics shared by all alligators (class variables).

    // The age at which a alligator can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a alligator can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a alligator breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of all prey.
    private static final Map<Class<? extends Actor>, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the alligator. In effect, this is the
    // number of steps a alligator can go before it has to eat again.
    private static final int MAX_FOOD = 25;

    /**
     * Create a alligator. The alligator is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this alligator had.
     */
    private Alligator(Time time, Field field, Location location, int age, int foodLevel)
    {
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = false;
        canGoLand = true;
        canGoWater = true;
        this.age = age;
        this.foodLevel = foodLevel;
    }

    public Alligator(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        this(time, field, location, 0, MAX_FOOD);
        inheritDiseases(parentDiseases);
    }

    public Alligator(Time time, Field field, Location location)
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
        mapTemp.put(Lemur.class, 8);
        mapTemp.put(Catfish.class, 4);
        return mapTemp;
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /**
     * Returns the max food level that the alligator can have.
     * @return The max food level that the alligator can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the alligator can have before dying.
     * @return The max age that the alligator can have before dying.
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
        return new Alligator(getTime(), getField(), loc, parentDiseases);
    }

    public Animal spawnRandom(Location loc)
    {
        return new Alligator(getTime(), getField(), loc);
    }
}
