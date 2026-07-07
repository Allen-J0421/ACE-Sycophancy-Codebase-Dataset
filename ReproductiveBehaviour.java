/**
 * Strategy interface for an animal's reproductive parameters.
 * The breeding algorithm is shared across all animals; only the
 * numerical parameters vary per species.
 */
public interface ReproductiveBehaviour
{
    /** Minimum age at which the animal can breed. */
    int getBreedingAge();

    /** Maximum number of offspring produced per breeding event. */
    int getMaxLitterSize();

    /** Per-step probability that a breeding-eligible female will reproduce. */
    double getBreedingProbability();
}
