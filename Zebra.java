/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Prey {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.305;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;

    private static final int DEFAULT_FOOD_VALUE = 5;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.1;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

    // The zebra is 10% less active around midnight.
    private static final TimeOfDay LOW_ACTIVITY_TIME = TimeOfDay.AROUND_MIDNIGHT;
    private static final double LOW_ACTIVENESS = 0.9;

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field The field in which this zebra resides.
     * @param location The location in which this zebra is spawned into.
     */
    public Zebra(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the zebra.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the zebra's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the zebra.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the zebra.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this zebra's disease spreading probability.
     *
     * @return The zebra's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this zebra dies from disease.
     *
     * @return The zebra's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Create a new instance of zebra.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new zebra instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Zebra(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Getter method for the time of day at which the zebra is less active.
     *
     * @return The TimeOfDay during which the zebra is less active.
     */
    @Override
    protected TimeOfDay getLowActivityTime() {
        return LOW_ACTIVITY_TIME;
    }

    /**
     * Getter method for the zebra's activeness during its low-activity time.
     *
     * @return A double value representing the reduced activeness.
     */
    @Override
    protected double getLowActiveness() {
        return LOW_ACTIVENESS;
    }
}
