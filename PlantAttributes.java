import java.util.EnumSet;
import java.util.Set;

/**
 * Shared attributes for a plant species.
 */
public class PlantAttributes extends OrganismAttributes {

    private final double maxSize;
    private final double lowBreedingProbability;
    private final double highBreedingProbability;
    private final double initialSize;
    private final double growthRate;
    private final int foodValue;
    private final boolean poisonous;
    private final Set<WeatherType> favorableWeather;

    public PlantAttributes(int maxAge, int breedingAge, int maxLitterSize,
                           double maxSize,
                           double lowBreedingProbability,
                           double highBreedingProbability,
                           double initialSize,
                           double growthRate,
                           int foodValue,
                           boolean poisonous,
                           Set<WeatherType> favorableWeather) {
        super(maxAge, breedingAge, maxLitterSize);
        this.maxSize = maxSize;
        this.lowBreedingProbability = lowBreedingProbability;
        this.highBreedingProbability = highBreedingProbability;
        this.initialSize = initialSize;
        this.growthRate = growthRate;
        this.foodValue = foodValue;
        this.poisonous = poisonous;
        this.favorableWeather = EnumSet.copyOf(favorableWeather);
    }

    public double getMaxSize() {
        return maxSize;
    }

    public double getLowBreedingProbability() {
        return lowBreedingProbability;
    }

    public double getHighBreedingProbability() {
        return highBreedingProbability;
    }

    public double getInitialSize() {
        return initialSize;
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public int getFoodValue() {
        return foodValue;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public Set<WeatherType> getFavorableWeather() {
        return favorableWeather;
    }
}
