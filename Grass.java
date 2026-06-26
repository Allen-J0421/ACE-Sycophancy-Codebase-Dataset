/**
 * A simple model of a grass plant.
 * Grass plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Grass extends Plant
{
    // Tunable settings for grass.
    private SimulationConfig.PlantSettings settings;

    /**
     * Create a grass. A grass is created as a newborn age 0
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(Field field, Location location) {
        this(field, location, SimulationConfig.defaultConfig());
    }

    /**
     * Create grass with the given simulation configuration.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config The simulation configuration to use.
     */
    public Grass(Field field, Location location, SimulationConfig config) {
        super(field, location, config);
        settings = config.getPlantSettings(Grass.class);
    }

    protected double getReproducingProbability() {
        return settings.getReproducingProbability();
    }

    protected int getMaxOffspringSize() {
        return settings.getMaxOffspringSize();
    }

    protected Plant createYoung(Field field, Location location) {
        return new Grass(field, location, getConfig());
    }
}
