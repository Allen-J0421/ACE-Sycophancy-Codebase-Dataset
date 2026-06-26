/**
 * Immutable configuration for animal species behavior.
 */
public final class AnimalProfile {

    private final double breedingProbability;
    private final int maxLitterSize;
    private final int breedingAge;
    private final int maxAge;
    private final double diseaseSpreadProbability;
    private final double deathByDiseaseProbability;

    /**
     * Constructor for an animal profile.
     *
     * @param breedingProbability The probability this animal will breed.
     * @param maxLitterSize The maximum number of newborns from one breeding event.
     * @param breedingAge The minimum age at which this animal can breed.
     * @param maxAge The maximum age of this animal.
     * @param diseaseSpreadProbability The probability this animal attempts to spread disease.
     * @param deathByDiseaseProbability The probability this animal dies from disease.
     */
    public AnimalProfile(double breedingProbability, int maxLitterSize, int breedingAge, int maxAge,
                         double diseaseSpreadProbability, double deathByDiseaseProbability) {
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
    }

    /**
     * Getter method for the probability to breed.
     *
     * @return A double value representing the breeding probability.
     */
    public double getBreedingProbability() {
        return breedingProbability;
    }

    /**
     * Getter method for the maximum litter size.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    public int getMaxLitterSize() {
        return maxLitterSize;
    }

    /**
     * Getter method for the age of breeding.
     *
     * @return An integer value representing the breeding age.
     */
    public int getBreedingAge() {
        return breedingAge;
    }

    /**
     * Getter method for the maximum age.
     *
     * @return An integer value representing the maximum age.
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Getter method for disease spreading probability.
     *
     * @return The disease spreading probability.
     */
    public double getDiseaseSpreadProbability() {
        return diseaseSpreadProbability;
    }

    /**
     * Getter method for death by disease probability.
     *
     * @return The disease death probability.
     */
    public double getDeathByDiseaseProbability() {
        return deathByDiseaseProbability;
    }
}
