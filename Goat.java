/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Goat extends Herbivore {

    private static final HerbivoreAttributes ATTRIBUTES =
            new HerbivoreAttributes(150, 10, 3, 0.3065, 0.1, 0.001, 5);

    /**
     * Constructor for a Goat in the simulation.
     *
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field The field in which this goat resides.
     * @param location The location in which this goat is spawned into.
     */
    public Goat(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Goat::new);
    }

    @Override
    protected double getActivenessFor(TimeOfDay time) {
        if (time == TimeOfDay.LATE_MORNING) {
            return 0.8;
        }
        return super.getActivenessFor(time);
    }
}
