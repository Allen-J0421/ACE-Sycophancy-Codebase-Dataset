/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Lion predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Lion extends Predator {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.115,
        /* maxLitterSize            */ 2,
        /* breedingAge              */ 32,
        /* maxAge                   */ 130,
        /* diseaseSpreadProbability */ 0.01,
        /* deathByDiseaseProbability*/ 0.01,
        /* restTime                 */ TimeOfDay.NIGHT
    );

    private static final double EATING_PROBABILITY = 0.6;
    private static final int    DEFAULT_FOOD_LEVEL = 19;

    /**
     * Constructor for a lion in the simulation.
     *
     * @param foodLevel The initial food level.
     * @param randomAge Whether we assign this lion a random age or not.
     * @param field     The field in which this lion resides.
     * @param location  The location in which this lion is spawned into.
     */
    public Lion(int foodLevel, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodLevel, randomAge, field, location);
    }

    @Override
    public double getEatingProbability() { return EATING_PROBABILITY; }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Lion(DEFAULT_FOOD_LEVEL, true, field, location);
    }
}
