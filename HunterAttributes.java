/**
 * Shared attributes for a hunter species.
 */
public class HunterAttributes extends AnimalAttributes {

    private final int initialFoodLevel;

    public HunterAttributes(int maxAge, int breedingAge, int maxLitterSize,
                            double breedingProbability,
                            double diseaseSpreadProbability,
                            double deathByDiseaseProbability,
                            int initialFoodLevel) {
        super(maxAge, breedingAge, maxLitterSize,
                breedingProbability, diseaseSpreadProbability, deathByDiseaseProbability);
        this.initialFoodLevel = initialFoodLevel;
    }

    public int getInitialFoodLevel() {
        return initialFoodLevel;
    }
}
