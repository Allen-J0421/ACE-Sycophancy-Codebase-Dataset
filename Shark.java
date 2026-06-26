import java.util.List;
import java.util.Random;

/**
 ** A simple model of a shark.
 * 
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharkes (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a shark can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 8;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(COD_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = COD_FOOD_VALUE;
        }

    }

    /**
     * This is what the shark does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newSharks A list to return newly born sharks.
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
     * Make this shark more hungry. This could result in the shark's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for Cod and Salmon adjacent to the current location.
     * Only the first live Cod or Salmon is eaten, if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step){
        return findFood(disease, step, 1, COD_FOOD_VALUE, Cod.class, Salmon.class);
    }

    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newsharkes A list to return newly born sharkes.
     */
    protected void giveBirth(List<Creature> newSharkes)
    {
        // New sharkes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharkes.add(young);
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
     * A shark can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     *  Decide whether two sharks have different sex.
     *  @return true if two sharks have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){
        return hasDifferentSexNearby(Shark.class, 2);
    }

    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }

}
