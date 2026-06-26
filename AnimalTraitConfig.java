/**
 * Shared view of the common animal trait fields.
 *
 * @version 2022.03.02
 */
interface AnimalTraitConfig {

    Class<? extends Animal> speciesClass();

    double breedingProbability();

    int maxLitterSize();

    int breedingAge();

    int maxAge();

    double diseaseSpreadProbability();

    double deathByDiseaseProbability();
}
