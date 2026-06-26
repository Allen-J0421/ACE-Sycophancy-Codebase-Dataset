/**
 * Immutable shared configuration for a species in the simulation.
 */
public abstract class SpeciesProfile
{
    private final String name;
    private final int maximumTemperature;
    private final int minimumTemperature;
    private final int nutritionalValue;
    private final double reproductionProbability;

    public SpeciesProfile(String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability)
    {
        this.name = name;
        this.maximumTemperature = maximumTemperature;
        this.minimumTemperature = minimumTemperature;
        this.nutritionalValue = nutritionalValue;
        this.reproductionProbability = reproductionProbability;
    }

    public String getName()
    {
        return name;
    }

    public int getMaximumTemperature()
    {
        return maximumTemperature;
    }

    public int getMinimumTemperature()
    {
        return minimumTemperature;
    }

    public int getNutritionalValue()
    {
        return nutritionalValue;
    }

    public double getReproductionProbability()
    {
        return reproductionProbability;
    }
}
