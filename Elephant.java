/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Elephant extends Herbivore {

    private static final HerbivoreAttributes ATTRIBUTES =
            new HerbivoreAttributes(150, 10, 3, 0.3, 0.1, 0.001, 5);

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Elephant::new);
    }

    @Override
    protected double getActivenessFor(TimeOfDay time) {
        if (time == TimeOfDay.SUNSET) {
            return 0.85;
        }
        return super.getActivenessFor(time);
    }
}
