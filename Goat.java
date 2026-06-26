/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Goat extends Prey {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.3065,
        /* maxLitterSize            */ 3,
        /* breedingAge              */ 10,
        /* maxAge                   */ 150,
        /* diseaseSpreadProbability */ 0.1,
        /* deathByDiseaseProbability*/ 0.001,
        /* restTime                 */ TimeOfDay.LATE_MORNING
    );

    private static final int    DEFAULT_FOOD_VALUE = 5;
    private static final double REST_ACTIVENESS    = 0.8;

    /**
     * Constructor for a Goat in the simulation.
     *
     * @param foodValue The initial food value carried by this goat.
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field     The field in which this goat resides.
     * @param location  The location in which this goat is spawned into.
     */
    public Goat(int foodValue, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodValue, randomAge, field, location);
    }

    @Override
    protected double getRestActiveness() { return REST_ACTIVENESS; }

    /** Creates a goat with a random age for initial population seeding. */
    public static Goat spawn(Field field, Location location) {
        return new Goat(DEFAULT_FOOD_VALUE, true, field, location);
    }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return spawn(field, location);
    }
}
