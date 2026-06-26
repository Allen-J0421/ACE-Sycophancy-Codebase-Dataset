/**
 * Immutable configuration for predators.
 *
 * @version 2022.03.02
 */
final class PredatorTraits extends AnimalTraits {

    final double eatingProbability;
    final TimeOfDay restingTime;

    PredatorTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                   int maxLitterSize, int breedingAge, int maxAge,
                   double diseaseSpreadProbability, double deathByDiseaseProbability,
                   double eatingProbability, TimeOfDay restingTime) {
        super(speciesClass, breedingProbability, maxLitterSize, breedingAge, maxAge,
                diseaseSpreadProbability, deathByDiseaseProbability);
        this.eatingProbability = eatingProbability;
        this.restingTime = restingTime;
    }
}
