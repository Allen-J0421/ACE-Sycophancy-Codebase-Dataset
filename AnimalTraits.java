/**
 * Immutable configuration shared by species that follow the same breeding model.
 *
 * @version 2022.03.02
 */
class AnimalTraits {

    final Class<? extends Animal> speciesClass;
    final double breedingProbability;
    final int maxLitterSize;
    final int breedingAge;
    final int maxAge;
    final double diseaseSpreadProbability;
    final double deathByDiseaseProbability;

    AnimalTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                 int maxLitterSize, int breedingAge, int maxAge,
                 double diseaseSpreadProbability, double deathByDiseaseProbability) {
        this.speciesClass = speciesClass;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
    }
}
