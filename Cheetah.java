/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.115,
        /* maxLitterSize            */ 2,
        /* breedingAge              */ 26,
        /* maxAge                   */ 140,
        /* diseaseSpreadProbability */ 0.01,
        /* deathByDiseaseProbability*/ 0.01,
        /* restTime                 */ TimeOfDay.EARLY_AFTERNOON
    );

    private static final double EATING_PROBABILITY = 0.6;
    private static final int    DEFAULT_FOOD_LEVEL = 19;

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The initial food level.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field     The field in which this cheetah resides.
     * @param location  The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodLevel, randomAge, field, location);
    }

    @Override
    public double getEatingProbability() { return EATING_PROBABILITY; }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Cheetah(DEFAULT_FOOD_LEVEL, true, field, location);
    }
}
