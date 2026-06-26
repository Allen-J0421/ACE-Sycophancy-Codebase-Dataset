import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple model of a Gazelle.
 * Gazelles age, move,eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all Gazelles (class variables).

    // The age at which a Gazelle can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Gazelle can live.
    private static final  int MAX_AGE  = 80;
    // The likelihood of a Gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.8900995; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 2;
    //The minimum food value of the grass. In effect, this is the
    // number of steps a Gazelle can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 14; //8 //15
    //maximum food a gazelle can eat.
    private static final double MAX_FOOD_LEVEL = 28; //60 //160
    
    // The likelihood of a Gazelle finding food depending on the weather.
    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 1;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 1;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 1;

    // Individual characteristics (instance fields).
    private Map<Class<? extends Actor>, Integer> food;
    /**
     * Create a new Gazelle. A Gazelle may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        setFoodLevel(GRASS_FOOD_VALUE);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GRASS_FOOD_VALUE));
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/75.0);
        addFood();
    }

    /**
     * This is what the Gazelle does most of the time: it finds
     * grass to eat. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     * 
     * @param newGazelles A list to return newly born Gazelles.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newGazelles, Simulator simulator)
    {
        setGrowthLevel(0.012);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newGazelles);  
                super.act(newGazelles,simulator);
            }
        }else{
            //space for potential night activities
        }
    }

    /**
     * Returns the maximum number of babies the gazelle can give birth to at once.
     * @return max litter size of the gazelle.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the gazelle
     * @return breeding probability of the gazelle.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Get breeding age of a gazelle.
     * @return Breeding age of the gazelle;
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a gazelle.
     * @return Max age of the gazelle.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Returns the current gazelle occupying the location.
     * @return the current gazelle.
     */
    protected Animal getAnimal(){
        return this;
    }

    /**
     * Adds the food the gazelle eats and the corresponding food value to a map.
     */
    private void addFood(){
        food.put(Grass.class, GRASS_FOOD_VALUE);
    }

    /**
     * Returns the food values keyed by prey type.
     * @return The food values for the gazelle's prey types.
     */
    protected Map<Class<? extends Actor>, Integer> getFood(){
        return food;
    }

    /**
     * Gets the maximum food level a gazelle can have.
     * @return Max food level of the gazelle.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }
    
    /**
     * Gets the maximum time a gazelle needs to wait until it can breed again
     * @return Max time before the gazelle can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the gazelle will find food when it is sunny
     * @return The probability the gazelle will find food when it is sunny
     */
    protected double getSunnyFindingFoodProbability(){
        return SUNNY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the gazelle will find food when it is rainy
     * @return The probability the gazelle will find food when it is rainy
     */
    protected double getRainyFindingFoodProbability(){
        return RAINY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the gazelle will find food when it is foggy
     * @return The probability the gazelle will find food when it is foggy
     */
    protected double getFoggyFindingFoodProbability(){
        return FOGGY_FINDING_FOOD_PROBABILITY;
    }
}
