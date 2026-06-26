import java.util.Map;

/**
 * A simple model of a Jaguar.
 * Jaguars age, move, eat gazelle, and die.
 *
 * @version 2022.03.01
 */
public class Jaguar extends Predator
{
    // Characteristics shared by all Jaguars (class variables).

    // The age at which a Jaguar can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Jaguar can live.
    private static final  int MAX_AGE  = 1000;
    // The likelihood of a Jaguar breeding.
    private static final double BREEDING_PROBABILITY = 0.2; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 6;
    //The minimum food value of the gazelle. In effect, this is the
    // number of steps a Jaguar can go before it has to eat again.
    private static final int GAZELLE_FOOD_VALUE = 35; 
    private static final double MAX_FOOD_LEVEL = 39; 
    
    // The likelihood of a Jaguar finding food depending on the weather.
    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 1;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 0.7;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 0.4;

    private static final Map<Class<? extends Actor>, Integer> FOOD_VALUES =
        Map.<Class<? extends Actor>, Integer>of(Gazelle.class, GAZELLE_FOOD_VALUE);
    /**
     * Create a Jaguar. A Jaguar can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Jaguar will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Jaguar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GAZELLE_FOOD_VALUE));
        }
        else {
            setAge( 0);
            setFoodLevel(GAZELLE_FOOD_VALUE);
        }
        setGrowthLevel(getAge()/89.0);
    }

    /**
     * Returns the maximum number of babies the jaguar can give birth to at once.
     * @return max litter size of the jaguar.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the jaguar
     * @return breeding probability of the jaguar.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Creates a newborn jaguar at the given location.
     * @param field The field the newborn is in.
     * @param location The newborn's location.
     * @return A newborn jaguar.
     */
    protected Animal createYoung(Field field, Location location){
        return new Jaguar(false, field, location);
    }

    /**
     * Get breeding age of a jaguar.
     * @return Breeding age of the jaguar;
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a jaguar.
     * @return Max age of the jaguar.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Returns the prey types the jaguar eats and the amount of food each gives.
     * @return A map of prey type to food value.
     */
    protected Map<Class<? extends Actor>, Integer> getFoodValues(){
        return FOOD_VALUES;
    }

    /**
     * Gets the maximum food level a jaguar can have.
     * @return Max food level of the jaguar.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns how much the jaguar grows each step.
     * @return The jaguar's per-step growth increment.
     */
    protected double getGrowthPerStep(){
        return 0.013;
    }

    /**
     * Gets the maximum time a jaguar needs to wait until it can breed again
     * @return Max time before the jaguar can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the jaguar will find food when it is sunny
     * @return The probability the jaguar will find food when it is sunny
     */
    protected double getSunnyFindingFoodProbability(){
        return SUNNY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the jaguar will find food when it is rainy
     * @return The probability the jaguar will find food when it is rainy
     */
    protected double getRainyFindingFoodProbability(){
        return RAINY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the jaguar will find food when it is foggy
     * @return The probability the jaguar will find food when it is foggy
     */
    protected double getFoggyFindingFoodProbability(){
        return FOGGY_FINDING_FOOD_PROBABILITY;
    }
}
