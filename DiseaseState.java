/**
 * Mutable disease-related state for an organism.
 *
 * @version 26/02/2022
 */
public class DiseaseState
{
    private boolean infected;
    private boolean immune;
    private double diseaseProbability;
    private double diseaseSpreadProbability;
    private double deathFromInfectionProbability;
    private double immuneProbability;

    public boolean isInfected()
    {
        return infected;
    }

    public void setInfected(boolean infected)
    {
        this.infected = infected;
    }

    public boolean isImmune()
    {
        return immune;
    }

    public void setImmune(boolean immune)
    {
        this.immune = immune;
    }

    public double getDiseaseProbability()
    {
        return diseaseProbability;
    }

    public void setDiseaseProbability(double diseaseProbability)
    {
        this.diseaseProbability = diseaseProbability;
    }

    public double getDiseaseSpreadProbability()
    {
        return diseaseSpreadProbability;
    }

    public void setDiseaseSpreadProbability(double diseaseSpreadProbability)
    {
        this.diseaseSpreadProbability = diseaseSpreadProbability;
    }

    public double getDeathFromInfectionProbability()
    {
        return deathFromInfectionProbability;
    }

    public void setDeathFromInfectionProbability(double deathFromInfectionProbability)
    {
        this.deathFromInfectionProbability = deathFromInfectionProbability;
    }

    public double getImmuneProbability()
    {
        return immuneProbability;
    }

    public void setImmuneProbability(double immuneProbability)
    {
        this.immuneProbability = immuneProbability;
    }
}
