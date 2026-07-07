/**
 * Immutable configuration object holding all species-level constants for an animal.
 * Separates tunable data from behaviour so that model classes carry only logic.
 *
 * @version 2022.03.01
 */
public class AnimalConfig {
    public final int    breedingAge;
    public final int    maxAge;
    public final double breedingProbability;
    public final int    maxLitterSize;
    public final int    maxTimeUntilBreedingAgain;
    public final double maxFoodLevel;
    public final double sunnyFoodProbability;
    public final double rainyFoodProbability;
    public final double foggyFoodProbability;
    /** Food level added unconditionally before the random branch in initialise(). 0 for predators. */
    public final int    initialFoodBase;
    /** Upper bound for random food; non-random food = initialFoodCap - initialFoodBase. */
    public final int    initialFoodCap;
    /** Divisor for age-based initial growth level in initialise(). */
    public final double growthDivisor;

    public AnimalConfig(
            int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, int maxTimeUntilBreedingAgain, double maxFoodLevel,
            double sunnyFoodProbability, double rainyFoodProbability, double foggyFoodProbability,
            int initialFoodBase, int initialFoodCap, double growthDivisor) {
        this.breedingAge               = breedingAge;
        this.maxAge                    = maxAge;
        this.breedingProbability       = breedingProbability;
        this.maxLitterSize             = maxLitterSize;
        this.maxTimeUntilBreedingAgain = maxTimeUntilBreedingAgain;
        this.maxFoodLevel              = maxFoodLevel;
        this.sunnyFoodProbability      = sunnyFoodProbability;
        this.rainyFoodProbability      = rainyFoodProbability;
        this.foggyFoodProbability      = foggyFoodProbability;
        this.initialFoodBase           = initialFoodBase;
        this.initialFoodCap            = initialFoodCap;
        this.growthDivisor             = growthDivisor;
    }
}
