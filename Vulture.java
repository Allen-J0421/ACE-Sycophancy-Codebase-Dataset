/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Vulture scavenger in the simulation.
 *
 * @version 2022.03.02
 */
public class Vulture extends Scavenger {

    private static final AnimalTraits TRAITS = new AnimalTraits(
        /* breedingProbability      */ 0.08,
        /* maxLitterSize            */ 3,
        /* breedingAge              */ 12,
        /* maxAge                   */ 80,
        /* diseaseSpreadProbability */ 0.01,
        /* deathByDiseaseProbability*/ 0.001,
        /* restTime                 */ TimeOfDay.SUNSET
    );

    private static final int DEFAULT_FOOD_LEVEL = 40;

    /**
     * Constructor for a vulture in the simulation.
     *
     * @param foodLevel The initial food level.
     * @param randomAge Whether the vulture should have a random age or not.
     * @param field     The field in which the vulture resides.
     * @param location  The location in which the vulture spawns into.
     */
    public Vulture(int foodLevel, boolean randomAge, Field field, Location location) {
        super(TRAITS, foodLevel, randomAge, field, location);
    }

    /** Creates a vulture with a random age for initial population seeding. */
    public static Vulture spawn(Field field, Location location) {
        return new Vulture(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return spawn(field, location);
    }
}
