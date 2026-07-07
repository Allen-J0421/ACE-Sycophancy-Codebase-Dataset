/**
 * Immutable configuration data for one species of animal.
 * Holds breeding/lifecycle parameters shared across all instances of that species.
 */
public class AnimalConfig {
    public final int breedingAge;
    public final int maxAge;
    public final double breedingProbability;
    public final int maxLitterSize;
    public final int initialFoodLevel;

    public AnimalConfig(int breedingAge, int maxAge, double breedingProbability,
                        int maxLitterSize, int initialFoodLevel) {
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.initialFoodLevel = initialFoodLevel;
    }
}
