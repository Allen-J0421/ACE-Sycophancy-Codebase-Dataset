/**
 * Immutable configuration for predators.
 *
 * @version 2022.03.02
 */
record PredatorTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                      int maxLitterSize, int breedingAge, int maxAge,
                      double diseaseSpreadProbability, double deathByDiseaseProbability,
                      double eatingProbability, TimeOfDay restingTime)
        implements AnimalTraitConfig {
}
