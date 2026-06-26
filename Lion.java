import java.util.Map;

/**
 * A simple model of a Lion.
 * Lions age, move, eat gazelle & cheetahs, breed and die.
 *
 * @version 2022.03.01
 */
public class Lion extends Predator
{
    // Characteristics shared by all Lions (class variables).

    // The age at which a Lions can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Lion can live.
    private static final  int MAX_AGE = 1500;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.40752995; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN =20;
    // The minimum food value of a single prey. In effect, this is the
    // number of steps a Lion can go before it has to eat again.
    //Lions have 2 prey.
    private static final int PREY_GAZELLE_FOOD_VALUE = 24;
    private static final int PREY_CHEETAH_FOOD_VALUE = 25;

    private static final double MAX_FOOD_LEVEL = 50;
    
    // The likelihood of a Lion finding food depending on the weather.
    private static final Map<Weather, Double> FINDING_FOOD_PROBABILITIES =
        Map.<Weather, Double>of(
            Weather.SUNNY, 1.0,
            Weather.RAINY, 0.9,
            Weather.FOGGY, 0.8);

    private static final Map<Class<? extends Actor>, Integer> FOOD_VALUES =
        Map.<Class<? extends Actor>, Integer>of(
            Gazelle.class, PREY_GAZELLE_FOOD_VALUE,
            Cheetah.class, PREY_CHEETAH_FOOD_VALUE);
    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(PREY_CHEETAH_FOOD_VALUE));
        }
        else {
            setAge( 0);
            setFoodLevel(PREY_CHEETAH_FOOD_VALUE);
        }
        setGrowthLevel(getAge()/100.0);
    }

    /**
     * Returns the maximum number of babies the lion can give birth to at once.
     * @return max litter size of the lion.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the lion
     * @return breeding probability of the lion.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Creates a newborn lion at the given location.
     * @param field The field the newborn is in.
     * @param location The newborn's location.
     * @return A newborn lion.
     */
    protected Animal createYoung(Field field, Location location){
        return new Lion(false, field, location);
    }

    /**
     * Get breeding age of a lion.
     * @return int. Breeding age of the lion;
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a lion.
     * @return int. Max age of the lion.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Returns the prey types the lion eats and the amount of food each gives.
     * @return A map of prey type to food value.
     */
    protected Map<Class<? extends Actor>, Integer> getFoodValues(){
        return FOOD_VALUES;
    }

    /**
     * Gets the maximum food level a lion can have.
     * @return Max food level of the lion.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns how much the lion grows each step.
     * @return The lion's per-step growth increment.
     */
    protected double getGrowthPerStep(){
        return 0.01;
    }

    /**
     * Gets the maximum time a lion needs to wait until it can breed again
     * @return Max time before the lion can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the lion will find food for the current weather.
     * @param weather The current weather.
     * @return The probability the lion will find food.
     */
    protected double getFindingFoodProbability(Weather weather){
        return FINDING_FOOD_PROBABILITIES.get(weather);
    }
}
