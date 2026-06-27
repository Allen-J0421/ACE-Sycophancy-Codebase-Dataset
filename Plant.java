import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants. Reproduction and
 * aging are driven by an instance SpeciesConfig, with the concrete plant
 * produced by {@link #createOffspring}.
 *
 * @version 2022.02.28
 */
public abstract class Plant extends Organism
{
    // This plant's species characteristics (instance-based configuration).
    private final SpeciesConfig config;
    // The seeded random number generator, obtained per instance from the
    // shared Randomizer so the whole simulation stays reproducible.
    private final Random rand;

    /**
     * Create a new plant at location in field.
     * @param config The species characteristics for this plant.
     * @param randomAge If true, the plant will have a random age assigned to it.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(SpeciesConfig config, boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.config = config;
        this.rand = Randomizer.getRandom();
        if(randomAge) {
            //random age & water level assigned to plant
            setAge(rand.nextInt(config.maxAge()));
            setWaterLevel(rand.nextInt(15));
        }
        else {
            setAge(0);
            //sets water level to a random value when first created
            setWaterLevel(rand.nextInt(15));
        }
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it needs to do.
     * @param newPlants A list to receive newly grown plants
     */
    public void act(List<Actor> newPlants)
    {
        super.act(newPlants);
    }

    /**
     * Fog raises a plant's water level slightly.
     */
    public void onFog() {
        setWaterLevel(getWaterLevel() + 2);
    }

    /**
     * Increase the age. This could result in the plant's death once it
     * passes its species' maximum age.
     */
    public void incrementAge()
    {
        super.incrementAge();
        if(getAge() > config.maxAge()) {
            setDead();
        }
    }

    /**
     * @return The food value gained by a herbivore that eats this plant.
     */
    public int getFoodValue()
    {
        return config.foodValue();
    }

    /**
     * Reproduce into free adjacent locations. This is a template method:
     * shared for every plant, with the concrete young produced by
     * {@link #createOffspring}.
     * @param newPlants A list to return newly grown plants.
     */
    protected void giveBirth(List<Actor> newPlants)
    {
        // New plants are grown into adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = reproduce();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = createOffspring(field, loc);
            newPlants.add(young);
        }
    }

    /**
     * Generate a number representing how many new plants are grown,
     * if this plant can reproduce.
     * @return The number of new plants (may be zero).
     */
    private int reproduce()
    {
        int births = 0;
        if(canReproduce() && rand.nextDouble() <= config.breedingProbability()) {
            births = rand.nextInt(config.maxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * A plant can reproduce once its water level is high enough.
     * @return true if the plant can reproduce, false otherwise.
     */
    private boolean canReproduce()
    {
        return (getWaterLevel() >= 9);
    }

    /**
     * Create a new plant of this species at the given location.
     * @param field The field the young is grown into.
     * @param location The location the young is grown at.
     * @return The newly created plant.
     */
    protected abstract Plant createOffspring(Field field, Location location);

    /**
     * Reduces the current water level of the plant by 1 and updates it.
     * Checks if water level is 0. If it does, the plant dies.
     */
    public void decreaseWaterLevel(){
        //newWaterLevel - passes updated value to set method in Organism class
        int newWaterLevel = getWaterLevel() - 1;
        setWaterLevel(newWaterLevel);
        if(getWaterLevel() <=0) {
            setDead();
        }
    }

    /**
     * Look for water adjacent to the plant's current location
     * @return Where water was found, or null if it wasn't
     */
    protected Location findWater() {
        if (getWaterLevel() <= 5) {
            super.findWater();
        }
        return null;
    }
}
