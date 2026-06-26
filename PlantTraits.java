/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Immutable data class holding species-level configuration for a plant.
 * Each concrete plant class owns one static instance, passed up through
 * the constructor chain so Plant can satisfy all stat-getter methods
 * and the shared act() logic without requiring concrete classes to
 * override them individually.
 */
public class PlantTraits {

    public final int    maxAge;
    public final int    breedingAge;
    public final double maxSize;
    public final int    maxLitterSize;
    public final double growthRate;
    public final double lowBreedingProbability;
    public final double highBreedingProbability;

    /**
     * @param maxAge                  Age at which the plant dies naturally.
     * @param breedingAge             Minimum age required to spread seeds.
     * @param maxSize                 Maximum size the plant can reach.
     * @param maxLitterSize           Maximum number of offspring per spread.
     * @param growthRate              Multiplicative size growth per step.
     * @param lowBreedingProbability  Base breeding probability.
     * @param highBreedingProbability Boosted breeding probability in favourable weather.
     */
    public PlantTraits(int maxAge, int breedingAge, double maxSize, int maxLitterSize,
                       double growthRate, double lowBreedingProbability,
                       double highBreedingProbability) {
        this.maxAge                  = maxAge;
        this.breedingAge             = breedingAge;
        this.maxSize                 = maxSize;
        this.maxLitterSize           = maxLitterSize;
        this.growthRate              = growthRate;
        this.lowBreedingProbability  = lowBreedingProbability;
        this.highBreedingProbability = highBreedingProbability;
    }
}
