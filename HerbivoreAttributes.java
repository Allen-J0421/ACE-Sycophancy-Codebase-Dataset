/**
 * Shared attributes for a herbivore species.
 */
public class HerbivoreAttributes extends AnimalAttributes {

    private final int initialFoodValue;

    public HerbivoreAttributes(int maxAge, int breedingAge, int maxLitterSize,
                               double breedingProbability,
                               double diseaseSpreadProbability,
                               double deathByDiseaseProbability,
                               int initialFoodValue) {
        super(maxAge, breedingAge, maxLitterSize,
                breedingProbability, diseaseSpreadProbability, deathByDiseaseProbability);
        this.initialFoodValue = initialFoodValue;
    }

    public int getInitialFoodValue() {
        return initialFoodValue;
    }
}
