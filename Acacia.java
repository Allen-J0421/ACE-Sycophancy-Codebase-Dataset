/**
 * A simple model of an acacia plant.
 * Acacia plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Acacia extends Plant
{
    // Tunable settings for acacias.
    private SimulationConfig.PlantSettings settings;

    /**
     * Create an acacia. An acacia is created as a newborn with age 0
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Acacia(Field field, Location location)
    {
        this(field, location, SimulationConfig.defaultConfig());
    }

    /**
     * Create an acacia with the given simulation configuration.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config The simulation configuration to use.
     */
    public Acacia(Field field, Location location, SimulationConfig config)
    {
        super(field, location, config);
        settings = config.getPlantSettings(Acacia.class);
    }

    protected double getReproducingProbability() {
        return settings.getReproducingProbability();
    }

    protected int getMaxOffspringSize() {
        return settings.getMaxOffspringSize();
    }

    protected Plant createYoung(Field field, Location location) {
        return new Acacia(field, location, getConfig());
    }
}
