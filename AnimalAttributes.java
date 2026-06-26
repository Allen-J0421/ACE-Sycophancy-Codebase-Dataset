/**
 * Shared attributes for an animal species.
 */
public class AnimalAttributes extends OrganismAttributes {

    private final double breedingProbability;
    private final double diseaseSpreadProbability;
    private final double deathByDiseaseProbability;

    public AnimalAttributes(int maxAge, int breedingAge, int maxLitterSize,
                            double breedingProbability,
                            double diseaseSpreadProbability,
                            double deathByDiseaseProbability) {
        super(maxAge, breedingAge, maxLitterSize);
        this.breedingProbability = breedingProbability;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
    }

    public double getBreedingProbability() {
        return breedingProbability;
    }

    public double getDiseaseSpreadProbability() {
        return diseaseSpreadProbability;
    }

    public double getDeathByDiseaseProbability() {
        return deathByDiseaseProbability;
    }
}
