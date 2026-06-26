import java.util.List;
import java.util.Random;

/**
 * A simple model of a Salmon.
 * 
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends Animal
{
    // Characteristics shared by all salmons (class variables).

    // The age at which a salmon can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a salmon can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a salmon breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;

    // The food value of a single salmon. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The salmon's age.
    private int age;

    // The salmon's food level
    private int foodLevel;


    /**
     * Create a new salmon. A salmon may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SEAWEED_FOOD_VALUE;
        }
    }

    /**
     * This is what the salmon does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newSalmons A list to return newly born salmons.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this salmon more hungry. This could result in the salmon's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten, if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step){
        return findFood(disease, step, 1, SEAWEED_FOOD_VALUE, Seaweed.class);
    }
    /**
     * Check whether or not this salmon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newsalmons A list to return newly born salmons.
     */
    protected void giveBirth(List<Creature> newSalmons)
    {
        // New salmons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Salmon young = new Salmon(false, field, loc);
            newSalmons.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A salmon can breed if it has reached the breeding age and has countered the salmon of opposite sex.
     * @return true if the salmon can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && encounterWithDiffSex();
    }

    
    /**
     * Decide if two salmons countered has different sex;
     */
    public boolean encounterWithDiffSex(){
        return hasDifferentSexNearby(Salmon.class, 2);
    }

    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }

}
