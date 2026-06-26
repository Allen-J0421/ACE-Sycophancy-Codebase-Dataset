/**
 * Immutable configuration shared by species that follow the same breeding model.
 *
 * @version 2022.03.02
 */
record AnimalTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                    int maxLitterSize, int breedingAge, int maxAge,
                    double diseaseSpreadProbability, double deathByDiseaseProbability)
        implements AnimalTraitConfig {
}
