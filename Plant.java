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
public final class Plant extends LivingBeing
{
    private static final double GROWING_PROBABILITY = 0.35;
    private static final double RAIN_PROBABILITY = 0.25;
    private static final int RIPE_AGE = 3;
    private static final int MAX_AGE = 10000;
    private static final int MAX_NEW_PLANTS = 10;

    private static final Random rand = Randomizer.getRandom();

    private int age;

    /**
     * Constructor for objects of class Plant
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(field, location);
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
    public void act(List<LivingBeing> newPlants)
    {
        incrementAge();
        if(isAlive()) {
            spreadSeeds(newPlants);
        }
    }

    /**
     * Check if new plants will be created
     * @param newPlants A list to return newly made plants
     */
    private void spreadSeeds(List<LivingBeing> newPlants)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = getSeedlingCount();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newPlants.add(new Plant(false, field, loc));
        }
    }

    /**
     * Decides how many new plants we want. Depends on the probability that a new plant can grow
     * and the probability that it is raining
     * @return The number of new plants made (may be zero)
     */
    private int getSeedlingCount()
    {
        if(rand.nextDouble() > GROWING_PROBABILITY) {
            return 0;
        }
        if(!canDisperseSeeds() || rand.nextDouble() > RAIN_PROBABILITY) {
            return 0;
        }

        int minimumSeedlings = isNight() ? 1 : 2;
        return rand.nextInt(MAX_NEW_PLANTS) + minimumSeedlings;
    }

    /**
     * A Plant can only disperse if it is ripe
     */
    private boolean canDisperseSeeds()
    {
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
