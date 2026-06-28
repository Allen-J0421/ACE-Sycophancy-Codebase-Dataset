
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
        setGrowthLevel(getAge()/67.0);
        addFood( field);
    }

    /**
     * How much a zebra grows during a single step.
     * @return The zebra's per-step growth increment.
     */
    protected double getStepGrowthLevel(){
        return 0.015;
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
     * Create a new-born zebra at the given location.
     * @return A new-born zebra.
     */
    protected Animal reproduce(Field field, Location location){
        return new Zebra(false, field, location);
    }

    /**
     * Adds the food the zebra eats & the corresponding food value to a hashMap.
     * @param field The field the zebra is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        registerPrey(new Grass(true, field, tempLocation), GRASS_FOOD_VALUE);
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
     * Gets the probability the zebra will find food in the given weather.
     * @param weather The current weather.
     * @return The probability the zebra will find food.
     */
    protected double getFindingFoodProbability(Weather weather){
        switch(weather){
            case SUNNY: return SUNNY_FINDING_FOOD_PROBABILITY;
            case RAINY: return RAINY_FINDING_FOOD_PROBABILITY;
            default:    return FOGGY_FINDING_FOOD_PROBABILITY;
        }
    }
}