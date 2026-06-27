/**
 * Immutable, instance-based configuration of a plant species' characteristics.
 *
 * Replaces the {@code static final} constants that used to live directly on
 * the plant subclasses. Each plant instance carries its own config.
 *
 * @version 2022.03.01
 */
public final class PlantConfig
{
    // The age to which the plant can live.
    private final int maxAge;
    // The likelihood of the plant reproducing when eligible.
    private final double reproductionProbability;
    // The maximum number of new plants produced in a single reproduction.
    private final int maxLitterSize;
    // The food value gained by a herbivore that eats this plant.
    private final int foodValue;

    /**
     * @param maxAge The age to which the plant can live.
     * @param reproductionProbability The likelihood of reproducing when eligible.
     * @param maxLitterSize The maximum number of new plants per reproduction.
     * @param foodValue The food value gained by a herbivore that eats it.
     */
    public PlantConfig(int maxAge, double reproductionProbability, int maxLitterSize, int foodValue)
    {
        this.maxAge = maxAge;
        this.reproductionProbability = reproductionProbability;
        this.maxLitterSize = maxLitterSize;
        this.foodValue = foodValue;
    }

    public int getMaxAge() { return maxAge; }
    public double getReproductionProbability() { return reproductionProbability; }
    public int getMaxLitterSize() { return maxLitterSize; }
    public int getFoodValue() { return foodValue; }
}
