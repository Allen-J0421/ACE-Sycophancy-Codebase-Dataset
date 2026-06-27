/**
 * Immutable configuration for animal breeding behavior.
 */
public record BreedingProfile(int breedingAge, double breedingProbability,
                              int maxLitterSize, boolean requiresMate,
                              int mateSearchRadius)
{
    /**
     * Create breeding settings for species that do not require a mate.
     */
    public BreedingProfile(int breedingAge, double breedingProbability, int maxLitterSize)
    {
        this(breedingAge, breedingProbability, maxLitterSize, false, 1);
    }
}
