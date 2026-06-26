/**
 * Immutable configuration for herbivores.
 *
 * @version 2022.03.02
 */
final class HerbivoreTraits extends AnimalTraits {

    final TimeOfDay reducedActivenessTime;
    final double reducedActiveness;

    HerbivoreTraits(Class<? extends Animal> speciesClass, double breedingProbability,
                    int maxLitterSize, int breedingAge, int maxAge,
                    double diseaseSpreadProbability, double deathByDiseaseProbability,
                    TimeOfDay reducedActivenessTime, double reducedActiveness) {
        super(speciesClass, breedingProbability, maxLitterSize, breedingAge, maxAge,
                diseaseSpreadProbability, deathByDiseaseProbability);
        this.reducedActivenessTime = reducedActivenessTime;
        this.reducedActiveness = reducedActiveness;
    }
}
