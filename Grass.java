import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {

    private static final PlantProfile PROFILE = new PlantProfile(
            false, 10.0, 25, 16, 0.15, 0.25, 2, 5, 1.00, 1.2,
            WeatherType.RAIN, WeatherType.SUN);

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(foodValue, size, randomAge, field, location);
    }

    /**
     * Create grass with default starting values.
     *
     * @param field The field in which the grass will reside.
     * @param location The location in which the grass will occupy.
     * @return A new Grass instance.
     */
    static Grass createDefault(Field field, Location location) {
        return new Grass(PROFILE.getDefaultFoodValue(), PROFILE.getDefaultSize(), true, field, location);
    }

    @Override
    protected PlantProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of Grass.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Grass instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return createDefault(field, location);
    }

    /**
     * Method for what the grass does, i.e. what is always run at every step.
     *
     * @param newGrass A list of all newborn grass in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newGrass, Weather weather, TimeOfDay time) {
        actAsPlant(newGrass, weather);
    }

}
