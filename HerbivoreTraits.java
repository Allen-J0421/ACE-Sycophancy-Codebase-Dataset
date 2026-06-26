/**
 * Immutable configuration for herbivores.
 *
 * @version 2022.03.02
 */
record HerbivoreTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                       int maxLitterSize, int breedingAge, int maxAge,
                       double diseaseSpreadProbability, double deathByDiseaseProbability,
                       TimeOfDay reducedActivenessTime, double reducedActiveness)
        implements AnimalTraitConfig {
}
