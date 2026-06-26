/**
 * Mutable breeding-related state for an organism.
 *
 * @version 26/02/2022
 */
public class BreedingState
{
    private boolean female;
    private int breedingAge;
    private double breedingProbability;
    private int maxLitterSize;

    public boolean isFemale()
    {
        return female;
    }

    public void setFemale(boolean female)
    {
        this.female = female;
    }

    public int getBreedingAge()
    {
        return breedingAge;
    }

    public void setBreedingAge(int breedingAge)
    {
        this.breedingAge = breedingAge;
    }

    public double getBreedingProbability()
    {
        return breedingProbability;
    }

    public void setBreedingProbability(double breedingProbability)
    {
        this.breedingProbability = breedingProbability;
    }

    public int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    public void setMaxLitterSize(int maxLitterSize)
    {
        this.maxLitterSize = maxLitterSize;
    }
}
