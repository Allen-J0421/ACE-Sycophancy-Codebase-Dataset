/**
 * Immutable configuration for a plant species.
 */
public class PlantProfile
{
    @FunctionalInterface
    public interface PlantFactory
    {
        Plant create(Field field, Location location);
    }

    private final int matureAge;
    private final int maxAge;
    private final int maxPropagateSize;
    private final double oxygenRequiredAtNight;
    private final double oxygenGeneratedAtDay;
    private final PlantFactory plantFactory;

    public PlantProfile(int matureAge, int maxAge, int maxPropagateSize,
                        double oxygenRequiredAtNight, double oxygenGeneratedAtDay,
                        PlantFactory plantFactory)
    {
        this.matureAge = matureAge;
        this.maxAge = maxAge;
        this.maxPropagateSize = maxPropagateSize;
        this.oxygenRequiredAtNight = oxygenRequiredAtNight;
        this.oxygenGeneratedAtDay = oxygenGeneratedAtDay;
        this.plantFactory = plantFactory;
    }

    public int getMatureAge()
    {
        return matureAge;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public int getMaxPropagateSize()
    {
        return maxPropagateSize;
    }

    public double getOxygenRequiredAtNight()
    {
        return oxygenRequiredAtNight;
    }

    public double getOxygenGeneratedAtDay()
    {
        return oxygenGeneratedAtDay;
    }

    public Plant createYoung(Field field, Location location)
    {
        return plantFactory.create(field, location);
    }
}
