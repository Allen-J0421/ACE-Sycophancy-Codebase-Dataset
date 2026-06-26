/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    private static final HunterAttributes ATTRIBUTES =
            new HunterAttributes(140, 26, 2, 0.115, 0.01, 0.01,
                    19, 0.6, TimeOfDay.EARLY_AFTERNOON);

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Cheetah::new);
    }
}
