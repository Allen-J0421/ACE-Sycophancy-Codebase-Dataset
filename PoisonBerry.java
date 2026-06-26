import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A PoisonBerry plant present in the simulation.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {

    private static final PlantProfile PROFILE = new PlantProfile(
            true, 10.0, 20, 16, 0.104, 0.2, 3, 5, 1.00, 1.2,
            WeatherType.RAIN, WeatherType.SNOW);

    /**
     * Constructor for poison berries in the simulation.
     *
     * @param foodValue The food value of this plant.
     * @param size The initial size of this plant.
     * @param randomAge Whether the berries should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public PoisonBerry(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(foodValue, size, randomAge, field, location);
    }

    /**
     * Create poison berries with default starting values.
     *
     * @param field The field in which the berry will reside.
     * @param location The location in which the berry will occupy.
     * @return A new PoisonBerry instance.
     */
    static PoisonBerry createDefault(Field field, Location location) {
        return new PoisonBerry(PROFILE.getDefaultFoodValue(), PROFILE.getDefaultSize(), true, field, location);
    }

    @Override
    protected PlantProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of this berry.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new PoisonBerry instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return createDefault(field, location);
    }

    /**
     * Method for what the PoisonBerry does, i.e. what is always run at every step.
     *
     * @param newBerries A list of all newborn berries in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Organism> newBerries, Weather weather, TimeOfDay time) {
        actAsPlant(newBerries, weather);
    }

}
