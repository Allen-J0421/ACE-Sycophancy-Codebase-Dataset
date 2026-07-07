/**
 * Reproductive parameters for the standard animal breeding algorithm.
 * Each species supplies its own constants at construction time.
 */
public class StandardReproduction implements ReproductiveBehaviour
{
    private final int    breedingAge;
    private final int    maxLitterSize;
    private final double breedingProbability;

    public StandardReproduction(int breedingAge, int maxLitterSize, double breedingProbability)
    {
        this.breedingAge        = breedingAge;
        this.maxLitterSize      = maxLitterSize;
        this.breedingProbability = breedingProbability;
    }

    @Override public int    getBreedingAge()        { return breedingAge; }
    @Override public int    getMaxLitterSize()       { return maxLitterSize; }
    @Override public double getBreedingProbability() { return breedingProbability; }
}
