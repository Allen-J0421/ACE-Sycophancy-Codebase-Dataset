/**
 * Shared attributes for a herbivore species.
 */
public class HerbivoreAttributes extends AnimalAttributes {

    private final int initialFoodValue;
    private final TimeOfDay reducedActivenessTime;
    private final double reducedActiveness;

    public HerbivoreAttributes(int maxAge, int breedingAge, int maxLitterSize,
                               double breedingProbability,
                               double diseaseSpreadProbability,
                               double deathByDiseaseProbability,
                               int initialFoodValue,
                               TimeOfDay reducedActivenessTime,
                               double reducedActiveness) {
        super(maxAge, breedingAge, maxLitterSize,
                breedingProbability, diseaseSpreadProbability, deathByDiseaseProbability);
        this.initialFoodValue = initialFoodValue;
        this.reducedActivenessTime = reducedActivenessTime;
        this.reducedActiveness = reducedActiveness;
    }

    public int getInitialFoodValue() {
        return initialFoodValue;
    }

    public TimeOfDay getReducedActivenessTime() {
        return reducedActivenessTime;
    }

    public double getReducedActiveness() {
        return reducedActiveness;
    }
}
