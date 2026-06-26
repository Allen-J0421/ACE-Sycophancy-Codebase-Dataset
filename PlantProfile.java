/**
 * Immutable configuration describing one plant species.
 */
public class PlantProfile extends SpeciesProfile
{
    private final int maxHealth;

    public PlantProfile(String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability, int maxHealth)
    {
        super(name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability);
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }
}
