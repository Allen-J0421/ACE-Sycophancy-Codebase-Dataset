/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Prey {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.305,
        /* maxLitterSize            */ 3,
        /* breedingAge              */ 10,
        /* maxAge                   */ 150,
        /* diseaseSpreadProbability */ 0.1,
        /* deathByDiseaseProbability*/ 0.001,
        /* restTime                 */ TimeOfDay.AROUND_MIDNIGHT
    );

    private static final int    DEFAULT_FOOD_VALUE = 5;
    private static final double REST_ACTIVENESS    = 0.9;

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param foodValue The initial food value carried by this zebra.
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field     The field in which this zebra resides.
     * @param location  The location in which this zebra is spawned into.
     */
    public Zebra(int foodValue, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodValue, randomAge, field, location);
    }

    @Override
    protected double getRestActiveness() { return REST_ACTIVENESS; }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Zebra(DEFAULT_FOOD_VALUE, true, field, location);
    }
}
