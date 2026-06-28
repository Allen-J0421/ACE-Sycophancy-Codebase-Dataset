
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

    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 1;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 0.9;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 0.8;

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
        addFood(field);
    }

    /**
     * How much a lion grows during a single step.
     * @return The lion's per-step growth increment.
     */
    protected double getStepGrowthLevel(){
        return 0.01;
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
     * Create a new-born lion at the given location.
     * @return A new-born lion.
     */
    protected Animal reproduce(Field field, Location location){
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
     * Adds the food the lion eats & the corresponding food value to a hashMap
     * @param field The field the lion is in
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        registerPrey(new Gazelle(true, field, tempLocation), PREY_GAZELLE_FOOD_VALUE);
        registerPrey(new Cheetah(true, field, tempLocation), PREY_CHEETAH_FOOD_VALUE);
    }

    /**
     * Gets the maximum food level a lion can have.
     * @return Max food level of the lion.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Gets the maximum time a lion needs to wait until it can breed again
     * @return Max time before the lion can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the lion will find food in the given weather.
     * @param weather The current weather.
     * @return The probability the lion will find food.
     */
    protected double getFindingFoodProbability(Weather weather){
        switch(weather){
            case SUNNY: return SUNNY_FINDING_FOOD_PROBABILITY;
            case RAINY: return RAINY_FINDING_FOOD_PROBABILITY;
            default:    return FOGGY_FINDING_FOOD_PROBABILITY;
        }
    }
}
