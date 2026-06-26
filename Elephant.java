/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Elephant extends Prey {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.3,
        /* maxLitterSize            */ 3,
        /* breedingAge              */ 10,
        /* maxAge                   */ 150,
        /* diseaseSpreadProbability */ 0.1,
        /* deathByDiseaseProbability*/ 0.001,
        /* restTime                 */ TimeOfDay.SUNSET
    );

    private static final int    DEFAULT_FOOD_VALUE = 5;
    private static final double REST_ACTIVENESS    = 0.85;

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param foodValue The initial food value carried by this elephant.
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field     The field in which this elephant resides.
     * @param location  The location in which this elephant is spawned into.
     */
    public Elephant(int foodValue, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodValue, randomAge, field, location);
    }

    @Override
    protected double getRestActiveness() { return REST_ACTIVENESS; }

    /** Creates an elephant with a random age for initial population seeding. */
    public static Elephant spawn(Field field, Location location) {
        return new Elephant(DEFAULT_FOOD_VALUE, true, field, location);
    }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return spawn(field, location);
    }
}
