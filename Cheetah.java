import java.util.Map;
/**
 * A simple model of a Cheetah.
 * Cheetahs age, move, eat zebras, breed and die.
 *
 * @version 2022.03.01
 */
public class Cheetah extends Predator
{
    // Characteristics shared by all Cheetahs (class variables).

    // The age at which a Cheetah can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Cheetah can live.
    private static final  int MAX_AGE = 1200;
    // The likelihood of a Cheetah breeding.
    private static  double BREEDING_PROBABILITY = 0.4196975694969952;  
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 11;
    // The minimum food value of a single prey. In effect, this is the
    // number of steps a Cheetah can go before it has to eat again.
    // Cheetahs have 2 prey
    private static final int ZEBRA_FOOD_VALUE = 34; 
    private static final int GAZELLE_FOOD_VALUE = 33;
    private static final double MAX_FOOD_LEVEL = 40;
    
    // The likelihood of a Cheetah finding food depending on the weather.
    private static final Map<Weather, Double> FINDING_FOOD_PROBABILITIES =
        Map.<Weather, Double>of(
            Weather.SUNNY, 0.9,
            Weather.RAINY, 0.8,
            Weather.FOGGY, 0.7);

    private static final Map<Class<? extends Actor>, Integer> FOOD_VALUES =
        Map.<Class<? extends Actor>, Integer>of(
            Zebra.class, ZEBRA_FOOD_VALUE,
            Gazelle.class, GAZELLE_FOOD_VALUE);
    /**
     * Create a Cheetah. A Cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true,the Cheetah will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(ZEBRA_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(ZEBRA_FOOD_VALUE);
        }
        setGrowthLevel(getAge()/102.0);
    }

    /**
     * Returns the maximum number of babies the cheetah can give birth to at once.
     * @return the max litter size of the cheetah.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the cheetah.
     * @return the breeding probability of the cheetah.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Creates a newborn cheetah at the given location.
     * @param field The field the newborn is in.
     * @param location The newborn's location.
     * @return A newborn cheetah.
     */
    protected Animal createYoung(Field field, Location location){
        return new Cheetah(false, field, location);
    }

    /**
     * Get breeding age of a cheetah.
     * @return The breeding age of the cheetah.
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a cheetah.
     * @return The max age of the cheetah.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Returns the prey types the cheetah eats and the amount of food each gives.
     * @return A map of prey type to food value.
     */
    protected Map<Class<? extends Actor>, Integer> getFoodValues(){
        return FOOD_VALUES;
    }

    /**
     * Gets the maximum food level a cheetah can have.
     * @return Max food level of the cheetah.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns how much the cheetah grows each step.
     * @return The cheetah's per-step growth increment.
     */
    protected double getGrowthPerStep(){
        return 0.012;
    }
    
    /**
     * Gets the maximum time a cheetah needs to wait until it can breed again
     * @return Max time before the cheetah can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the cheetah will find food for the current weather.
     * @param weather The current weather.
     * @return The probability the cheetah will find food.
     */
    protected double getFindingFoodProbability(Weather weather){
        return FINDING_FOOD_PROBABILITIES.get(weather);
    }
}
