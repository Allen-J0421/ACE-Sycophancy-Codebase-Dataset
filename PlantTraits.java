/**
 * Immutable configuration for plants.
 *
 * @version 2022.03.02
 */
record PlantTraits(double maxSize, int maxAge, int breedingAge, int maxLitterSize,
                   double lowBreedingProbability, double highBreedingProbability,
                   double growthRate, WeatherType... favorableWeather) {

    PlantTraits {
        favorableWeather = favorableWeather.clone();
    }
}
