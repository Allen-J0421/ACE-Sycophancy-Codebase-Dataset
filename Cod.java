import java.util.Random;
import java.util.List;

/**
 * A simple model of a cod.
 * 
 * Cods age, move, eat seaweed, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    // Characteristics shared by all cods (class variables).

    // The age at which a cod can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a cod can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a cod breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;

    // The food value of a cod. In effect, this is the
    // number of steps a cod can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The cod's age.
    private int age;

    // THe cod's food level
    private int foodLevel;

    /**
     * Create a new cod. A cod may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the cod will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cod(boolean randomAge, Field field, Location location)
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
     * This is what the cod does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newCods A list to return newly born cods.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     * 
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this cod more hungry. This could result in the cod's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this cod is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCods A list to return newly born cods.

     */
    protected void giveBirth(List<Creature> newCod)
    {
        // New cods are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Cod young = new Cod(false, field, loc);
            newCod.add(young);
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
     * A cod can breed if it has reached the breeding age.
     * @return true if the cod can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && encounterWithDiffSex();
    }

    /**
     * Decide whether two cods have different sex.
     * @return true if two cods have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){
        return hasDifferentSexNearby(Cod.class, 1);
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten., if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step){
        return findFood(disease, step, 1, SEAWEED_FOOD_VALUE, Seaweed.class);
    }

    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }

}
