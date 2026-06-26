/**
 * Immutable configuration for plants.
 *
 * @version 2022.03.02
 */
final class PlantTraits {

    final double maxSize;
    final int maxAge;
    final int breedingAge;
    final int maxLitterSize;
    final double lowBreedingProbability;
    final double highBreedingProbability;
    final WeatherType[] favorableWeather;
    final double growthRate;

    PlantTraits(double maxSize, int maxAge, int breedingAge, int maxLitterSize,
                double lowBreedingProbability, double highBreedingProbability,
                double growthRate, WeatherType... favorableWeather) {
        this.maxSize = maxSize;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
        this.lowBreedingProbability = lowBreedingProbability;
        this.highBreedingProbability = highBreedingProbability;
        this.growthRate = growthRate;
        this.favorableWeather = favorableWeather.clone();
    }
}
