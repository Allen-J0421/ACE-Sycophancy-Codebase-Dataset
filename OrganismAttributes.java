/**
 * Shared lifecycle attributes for an organism species.
 */
public class OrganismAttributes {

    private final int maxAge;
    private final int breedingAge;
    private final int maxLitterSize;

    public OrganismAttributes(int maxAge, int breedingAge, int maxLitterSize) {
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getBreedingAge() {
        return breedingAge;
    }

    public int getMaxLitterSize() {
        return maxLitterSize;
    }
}
