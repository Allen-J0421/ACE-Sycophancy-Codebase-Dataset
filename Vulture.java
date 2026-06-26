/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Vulture scavenger in the simulation.
 *
 * @version 2022.03.02
 */
public class Vulture extends Scavenger {

    private static final HunterAttributes ATTRIBUTES =
            new HunterAttributes(80, 12, 3, 0.08, 0.01, 0.001,
                    40, 1.0, TimeOfDay.SUNSET);

    /**
     * Constructor for a vulture in the simulation.
     *
     * @param randomAge Whether the vulture should have a random age or not.
     * @param field The field in which the vulture resides.
     * @param location The location in which the vulture spawns into.
     */
    public Vulture(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Vulture::new);
    }
}
