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
    private static final Map<Weather, Double> FINDING_FOOD_PROBABILITIES =
        Map.<Weather, Double>of(
            Weather.SUNNY, 1.0,
            Weather.RAINY, 1.0,
            Weather.FOGGY, 1.0);

    private static final Map<Class<? extends Actor>, Integer> FOOD_VALUES =
        Map.<Class<? extends Actor>, Integer>of(Grass.class, GRASS_FOOD_VALUE);
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
        setGrowthLevel(getAge()/75.0);
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
     * Creates a newborn gazelle at the given location.
     * @param field The field the newborn is in.
     * @param location The newborn's location.
     * @return A newborn gazelle.
     */
    protected Animal createYoung(Field field, Location location){
        return new Gazelle(false, field, location);
    }

    /**
     * Returns the prey types the gazelle eats and the amount of food each gives.
     * @return A map of prey type to food value.
     */
    protected Map<Class<? extends Actor>, Integer> getFoodValues(){
        return FOOD_VALUES;
    }

    /**
     * Gets the maximum food level a gazelle can have.
     * @return Max food level of the gazelle.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns how much the gazelle grows each step.
     * @return The gazelle's per-step growth increment.
     */
    protected double getGrowthPerStep(){
        return 0.012;
    }
    
    /**
     * Gets the maximum time a gazelle needs to wait until it can breed again
     * @return Max time before the gazelle can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the gazelle will find food for the current weather.
     * @param weather The current weather.
     * @return The probability the gazelle will find food.
     */
    protected double getFindingFoodProbability(Weather weather){
        return FINDING_FOOD_PROBABILITIES.get(weather);
    }
}
