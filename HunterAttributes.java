/**
 * Shared attributes for a hunter species.
 */
public class HunterAttributes extends AnimalAttributes {

    private final int initialFoodLevel;
    private final double eatingProbability;
    private final TimeOfDay restingTime;

    public HunterAttributes(int maxAge, int breedingAge, int maxLitterSize,
                            double breedingProbability,
                            double diseaseSpreadProbability,
                            double deathByDiseaseProbability,
                            int initialFoodLevel,
                            double eatingProbability,
                            TimeOfDay restingTime) {
        super(maxAge, breedingAge, maxLitterSize,
                breedingProbability, diseaseSpreadProbability, deathByDiseaseProbability);
        this.initialFoodLevel = initialFoodLevel;
        this.eatingProbability = eatingProbability;
        this.restingTime = restingTime;
    }

    public int getInitialFoodLevel() {
        return initialFoodLevel;
    }

    public double getEatingProbability() {
        return eatingProbability;
    }

    public TimeOfDay getRestingTime() {
        return restingTime;
    }
}
