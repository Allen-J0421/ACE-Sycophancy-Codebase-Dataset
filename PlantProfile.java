import java.util.List;

/**
 * Immutable configuration for plant species behavior.
 */
public final class PlantProfile {

    private final boolean poisonous;
    private final double maxSize;
    private final int maxAge;
    private final int breedingAge;
    private final double lowBreedingProbability;
    private final double highBreedingProbability;
    private final int maxLitterSize;
    private final int defaultFoodValue;
    private final double defaultSize;
    private final double defaultGrowthRate;
    private final WeatherType[] favorableWeather;

    /**
     * Constructor for a plant profile.
     *
     * @param poisonous Whether the plant is poisonous.
     * @param maxSize The maximum size before growth wraps.
     * @param maxAge The maximum age of this plant.
     * @param breedingAge The minimum age at which this plant can breed.
     * @param lowBreedingProbability The default breeding probability.
     * @param highBreedingProbability The breeding probability during favorable weather.
     * @param maxLitterSize The maximum number of newborns from one breeding event.
     * @param defaultFoodValue The default food value for new plants.
     * @param defaultSize The default size for new plants.
     * @param defaultGrowthRate The default growth rate.
     * @param favorableWeather Weather types that increase breeding probability.
     */
    public PlantProfile(boolean poisonous, double maxSize, int maxAge, int breedingAge,
                        double lowBreedingProbability, double highBreedingProbability,
                        int maxLitterSize, int defaultFoodValue, double defaultSize,
                        double defaultGrowthRate, WeatherType... favorableWeather) {
        this.poisonous = poisonous;
        this.maxSize = maxSize;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.lowBreedingProbability = lowBreedingProbability;
        this.highBreedingProbability = highBreedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.defaultFoodValue = defaultFoodValue;
        this.defaultSize = defaultSize;
        this.defaultGrowthRate = defaultGrowthRate;
        this.favorableWeather = favorableWeather;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public double getMaxSize() {
        return maxSize;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getBreedingAge() {
        return breedingAge;
    }

    public double getLowBreedingProbability() {
        return lowBreedingProbability;
    }

    public double getHighBreedingProbability() {
        return highBreedingProbability;
    }

    public int getMaxLitterSize() {
        return maxLitterSize;
    }

    public int getDefaultFoodValue() {
        return defaultFoodValue;
    }

    public double getDefaultSize() {
        return defaultSize;
    }

    public double getDefaultGrowthRate() {
        return defaultGrowthRate;
    }

    public boolean isFavorableWeather(List<WeatherType> recentWeather) {
        for (WeatherType weather : favorableWeather) {
            if (recentWeather.contains(weather)) {
                return true;
            }
        }
        return false;
    }
}
