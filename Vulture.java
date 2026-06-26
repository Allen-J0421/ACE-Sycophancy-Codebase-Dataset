/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Vulture scavenger in the simulation.
 *
 * @version 2022.03.02
 */
public class Vulture extends Scavenger {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.08, 3, 12, 80, 0.01, 0.001);

    private static final int DEFAULT_FOOD_LEVEL = 40;

    /**
     * Constructor for a vulture in the simulation.
     *
     * @param foodLevel The food level of this vulture.
     * @param randomAge Whether the vulture should have a random age or not.
     * @param field The field in which the vulture resides.
     * @param location The location in which the vulture spawns into.
     */
    public Vulture(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Create a vulture with default starting values.
     *
     * @param field The field in which the vulture will reside.
     * @param location The location in which the vulture will occupy.
     * @return A new Vulture instance.
     */
    static Vulture createDefault(Field field, Location location) {
        return new Vulture(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of Vulture.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Vulture instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return createDefault(field, location);
    }

}
