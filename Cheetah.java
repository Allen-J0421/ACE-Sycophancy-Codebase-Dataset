import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.115;
    private static final double EATING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 2;
    private static final int BREEDING_AGE = 26;
    private static final int MAX_AGE = 140;

    private static final int DEFAULT_FOOD_LEVEL = 19;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.01;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the cheetah.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the cheetah's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the cheetah.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the cheetah.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this cheetah's disease spreading probability.
     *
     * @return The cheetah's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this cheetah dies from disease.
     *
     * @return The cheetah's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Create a new instance of Cheetah.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Cheetah instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Cheetah(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    /**
     * Method for what the cheetah does, i.e. what is always run at every step.
     *
     * @param newCheetahs A list of all newborn cheetahs in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Organism> newCheetahs, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {

            giveBirth(newCheetahs);

            //Only call giveBirth() method if it's a certain time of day for this animal.
            if (time == TimeOfDay.EARLY_AFTERNOON){
                return;
            }

            if (rand.nextDouble() <= getDeathByDiseaseProbability() ) {
                remove();
                return;
            }

            // Move towards a source of food if found.
            Location newLocation;

            if (rand.nextDouble() <= getDiseaseSpreadProbability() ) {
                newLocation = findAnimalToInfect();
            } else {
                newLocation = findFood();
            }

            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                remove();
            }
        }
    }

    /**
     * Getter method to return this cheetah's probability of eating if food is found.
     *
     * @return The cheetah's eating probability.
     */
    @Override
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

}
