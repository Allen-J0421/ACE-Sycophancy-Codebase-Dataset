import java.util.List;
import java.util.Random;

/**
 * Plants do not move around but they do grow and produce more plants. 
 * They have a max age that if they reach, they will die. 
 * They will also die if eaten by either Hedgehogs or Frogs. 
 * Their growth is controlled by the probability that it rains. 
 * Plants produce more seeds when it is day time.
 *
 * @version 2022.02.24
 */
public class Plant extends LivingBeing
{
    // The plant's age
    private int age;
    //The probability that the plant can grow
    private static final double GROWING_PROBABILITY = 0.35;
    //The probability that it will rain, affects growth
    private static final double RAIN_PROBABILITY = 0.25;

    //The plant can only produce new plants if it is ripe
    private static final int RIPE_AGE = 3;
    //Maximum age of a plant
    private static final int MAX_AGE = 10000;
    //Maximum number of plants a plant can create
    private static final int MAX_NEW_PLANTS = 10;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Plant
     * @param randomAge If true, the plant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        // initialise instance variables
        super(field,location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }

    /**
     * What plants will do day to day, they do not move around, just increase in age and
     * make new plants
     * @param newPlants A list to return newly born plants
     */
    @Override
    public void act(List<LivingBeing> newPlants) {
        incrementAge();
        if(isAlive()) {
            makeNewPlants(newPlants);
        }
    }

    /**
     * Check if new plants will be created
     * @param newPlants A list to return newly made plants
     */
    private void makeNewPlants(List<LivingBeing> newPlants) {
        //gets adjacent locations and populates it if it can give birth
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = grow();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant p = new Plant(false, field, loc);
            newPlants.add(p);
        }
    }

    /**
     * Decides how many new plants we want. Depends on the probability that a new plant can grow
     * and the probability that it is raining
     * @return The number of new plants made (may be zero)
     */
    private int grow() {
        int numOfNewPlants = 0;
        if(rand.nextDouble() <= GROWING_PROBABILITY) {
            if(canDisperseSeeds() && rand.nextDouble() <= RAIN_PROBABILITY) {
                if(isNight()) {
                    numOfNewPlants = rand.nextInt(MAX_NEW_PLANTS) + 1; //Plants will only grow if it rains
                } else{
                    numOfNewPlants = rand.nextInt(MAX_NEW_PLANTS) + 2; //More new plants created in day time
                }
            } else {
                numOfNewPlants = 0; 
            }
        }
        return numOfNewPlants;
    }

    /**
     * A Plant can only disperse if it is ripe
     */
    private boolean canDisperseSeeds() {
        return age >= RIPE_AGE;
    }

    /**
     * Increase the age.
     * This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
}
