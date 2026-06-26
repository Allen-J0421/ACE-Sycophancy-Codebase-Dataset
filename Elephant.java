import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Elephant extends Prey {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.3;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;

    private static final int DEFAULT_FOOD_VALUE = 5;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.1;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the elephant.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the elephant's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the elephant.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the elephant.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this elephant's disease spreading probability.
     *
     * @return The elephant's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this elephant dies from disease.
     *
     * @return The elephant's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Create a new instance of Elephant.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Elephant instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Elephant(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Method for what the elephant does, i.e. what is always run at every step.
     *
     * @param newElephants A list of all newborn elephants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newElephants, Weather weather, TimeOfDay time) {
        incrementAge();
        setActiveness(1);

        if(isAlive()) {
            giveBirth(newElephants);


            if (rand.nextDouble() <= getDeathByDiseaseProbability() ) {
                remove();
                return;
            }

            if (time == TimeOfDay.SUNSET){
                this.setActiveness(0.85);
            }

            if (rand.nextDouble() <= getActiveness()){
                // Try to move into a free location.
                Location newLocation;

                if (rand.nextDouble() <= getDiseaseSpreadProbability() ) {
                    newLocation = findAnimalToInfect();
                } else {
                    newLocation = findFood();
                }

                // Random chance to do either?

                if ((newLocation == null) || (getFoodValue() > 10)) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }

                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    remove();
                }
            }
        } else {
            decayifDead();
        }
    }

    /**
     * Checks all adjacent location for elephants that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this elephant can breed or not.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= getBreedingAge() && hasCompatibleMateNearby(Elephant.class);
    }

    /**
     * Find a food source the elephant would want to eat.
     * @return The location of the food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();

            Object organism = field.getObjectAt(where);
            if(organism instanceof Plant) {
                Plant plant = (Plant) organism;
                // kills animal
                //prey.setDead();
                //eatOrLeave(prey);
                if (plant.isAlive()) {
                    //System.out.println("ALIVE");
                    plant.setDead();
                    // NOTE: ONLY RETURN WHERE IF EATEN
                    boolean eaten = eat(plant);

                    //return where;
                    return eaten ? where : null;
                }
            }
        }
        return null;
    }
}
