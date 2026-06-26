
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple model of a Zebra.
 * Zebras age, move, eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Zebra extends Animal
{
    // Characteristics shared by all Zebra (class variables).

    // The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Zebra can live.
    private static final  int MAX_AGE = 160;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY =0.27587999058995; //0.2587998058995
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN =15;
    //The minimum food value of the grass. In effect, this is the
    // number of steps a Zebra can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 19; 
    private static final double MAX_FOOD_LEVEL = 28; 
    
    // The likelihood of a Zebra finding food depending on the weather.
    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 1;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 1;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 1;

    // Individual characteristics (instance fields).
    private Map<Class<? extends Actor>, Integer> food;
    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        setFoodLevel(GRASS_FOOD_VALUE);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GRASS_FOOD_VALUE));
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/67.0);
        addFood();
    }

    /**
     * This is what the Zebra does most of the time: it finds
     * grass to eat. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     * 
     * @param newZebras A list to return newly born Zebras.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newZebra, Simulator simulator)
    {
        performDaytimeActions(newZebra, simulator, 0.015);
    }

    /**
     * Returns the maximum number of babies the zebra can give birth to at once.
     * @return The max litter size of the zebra.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the zebra
     * @return The breeding probability of the zebra.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Get breeding age of a zebra.
     * @return The breeding age of the zebra.
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a zebra.
     * @return The max age of the zebra.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Create a newborn zebra.
     * @param field The field the offspring is born into.
     * @param location The location within the field.
     * @return A newborn zebra.
     */
    protected Animal createOffspring(Field field, Location location){
        return new Zebra(false, field, location);
    }

    /**
     * Adds the food the zebra eats and the corresponding food value to a map.
     */
    private void addFood(){
        food.put(Grass.class, GRASS_FOOD_VALUE);
    }

    /**
     * Returns the food values keyed by prey type.
     * @return The food values for the zebra's prey types.
     */
    protected Map<Class<? extends Actor>, Integer> getFood(){
        return food;
    }

    /**
     * Gets the maximum food level a zebra can have.
     * @return Max food level of the zebra.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }
    
    /**
     * Gets the maximum time a zebra needs to wait until it can breed again
     * @return Max time before the zebra can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**

     * Gets the probability the zebra will find food when it is sunny
     * @return The probability the zebra will find food when it is sunny
     */
    protected double getSunnyFindingFoodProbability(){
        return SUNNY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the zebra will find food when it is rainy
     * @return The probability the zebra will find food when it is rainy
     */
    protected double getRainyFindingFoodProbability(){
        return RAINY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the zebra will find food when it is foggy
     * @return The probability the zebra will find food when it is foggy
     */
    protected double getFoggyFindingFoodProbability(){
        return FOGGY_FINDING_FOOD_PROBABILITY;
    }
}
