import java.util.Random;
import java.util.List;

/**
 * A simple model of grass - a type of plant.
 *
 * @version 2022.02.28
 */
public class Grass extends Plant
{
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a piece of grass. Grass can be created as a new born (age zero)
     * or with a random age and water level.
     *
     * @param randomAge If true, the grass will have random age and water level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.setAge(0);
        if(randomAge) {
            this.setAge(rand.nextInt(SimulationConfiguration.GRASS_MAX_AGE));
            setWaterLevel(rand.nextInt(15));
        }
    }

    /**
     * This is what the grass does each step of the simulation
     * - it grows and may reproduce.
     * @param newGrass A list to return newly grown grass.
     */
    public void act(List<Actor> newGrass)
    {
        super.act(newGrass);
        decreaseWaterLevel();
        if(isAlive()) {
            giveBirth(newGrass);
            findWater();
        }
    }

    @Override
    protected int getMaxAge() { return SimulationConfiguration.GRASS_MAX_AGE; }

    @Override
    protected double getReproductionProbability() { return SimulationConfiguration.GRASS_REPRODUCTION_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.GRASS_MAX_LITTER_SIZE; }

    @Override
    protected boolean canReproduce() {
        return getWaterLevel() >= SimulationConfiguration.GRASS_WATER_REPRODUCTION_THRESHOLD;
    }

    @Override
    protected Plant createOffspring(Field field, Location loc) {
        return new Grass(false, field, loc);
    }

    public int getFoodValue() { return SimulationConfiguration.GRASS_FOOD_VALUE; }
}
