/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Herbivore {

    private static final HerbivoreAttributes ATTRIBUTES =
            new HerbivoreAttributes(150, 10, 3, 0.305, 0.1, 0.001,
                    5, TimeOfDay.AROUND_MIDNIGHT, 0.9);

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field The field in which this zebra resides.
     * @param location The location in which this zebra is spawned into.
     */
    public Zebra(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Zebra::new);
    }
}
