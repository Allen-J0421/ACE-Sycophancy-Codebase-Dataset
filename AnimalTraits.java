/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Immutable data class holding species-level configuration for an animal.
 * Each concrete animal class owns one static instance, passed up through
 * the constructor chain so Animal can satisfy all stat-getter methods
 * without requiring concrete classes to override them individually.
 */
public class AnimalTraits {

    public final double breedingProbability;
    public final int    maxLitterSize;
    public final int    breedingAge;
    public final int    maxAge;
    public final double diseaseSpreadProbability;
    public final double deathByDiseaseProbability;
    public final TimeOfDay restTime;

    /**
     * @param breedingProbability      Probability of producing offspring per step.
     * @param maxLitterSize            Maximum number of offspring per birth.
     * @param breedingAge              Minimum age required to breed.
     * @param maxAge                   Age at which the animal dies naturally.
     * @param diseaseSpreadProbability Probability of choosing to spread disease over foraging.
     * @param deathByDiseaseProbability Probability of dying from disease each step.
     * @param restTime                 Time-of-day at which this species is inactive.
     */
    public AnimalTraits(double breedingProbability, int maxLitterSize, int breedingAge,
                        int maxAge, double diseaseSpreadProbability,
                        double deathByDiseaseProbability, TimeOfDay restTime) {
        this.breedingProbability      = breedingProbability;
        this.maxLitterSize            = maxLitterSize;
        this.breedingAge              = breedingAge;
        this.maxAge                   = maxAge;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
        this.restTime                 = restTime;
    }
}
