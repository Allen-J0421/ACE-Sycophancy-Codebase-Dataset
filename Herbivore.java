import java.util.List;
import java.util.Random;

/**
 * Shared behavior for herbivores that adjust how active they are by time of day.
 *
 * @version 2022.03.02
 */
public abstract class Herbivore extends Prey {

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    private final Class<? extends Animal> speciesClass;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final int breedingAge;
    private final int maxAge;
    private final double diseaseSpreadProbability;
    private final double deathByDiseaseProbability;
    private final TimeOfDay reducedActivenessTime;
    private final double reducedActiveness;
    private double activeness;

    /**
     * Constructor for a herbivore in the simulation.
     *
     * @param foodValue The value of food of this herbivore.
     * @param randomAge Whether we assign this herbivore a random age or not.
     * @param field The field in which this herbivore resides.
     * @param location The location in which this herbivore is spawned into.
     */
    public Herbivore(int foodValue, boolean randomAge, Field field, Location location,
                     Class<? extends Animal> speciesClass,
                     double breedingProbability, int maxLitterSize, int breedingAge, int maxAge,
                     double diseaseSpreadProbability, double deathByDiseaseProbability,
                     TimeOfDay reducedActivenessTime, double reducedActiveness) {
        super(foodValue, randomAge, field, location);
        this.speciesClass = speciesClass;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
        this.reducedActivenessTime = reducedActivenessTime;
        this.reducedActiveness = reducedActiveness;
        this.activeness = 1;
    }

    /**
     * Method for what the herbivore does, i.e. what is always run at every step.
     *
     * @param newHerbivores A list of all newborn herbivores in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newHerbivores, Weather weather, TimeOfDay time) {
        incrementAge();
        setActiveness(1);

        if (isAlive()) {
            giveBirth(newHerbivores);

            if (rand.nextDouble() <= deathByDiseaseProbability) {
                remove();
                return;
            }

            if (time == reducedActivenessTime) {
                setActiveness(reducedActiveness);
            }

            if (rand.nextDouble() <= getActiveness()) {
                Location newLocation;
                if (rand.nextDouble() <= diseaseSpreadProbability) {
                    newLocation = findAnimalToInfect();
                } else {
                    newLocation = findFood();
                }

                if (newLocation == null || getFoodValue() > 10) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }

                if (newLocation != null) {
                    setLocation(newLocation);
                } else {
                    remove();
                }
            }
        } else {
            decayifDead();
        }
    }

    /**
     * @return The breeding probability for this herbivore species.
     */
    @Override
    public double getBreedingProbability() {
        return breedingProbability;
    }

    /**
     * @return The maximum litter size for this herbivore species.
     */
    @Override
    public int getMaxLitterSize() {
        return maxLitterSize;
    }

    /**
     * @return The maximum age for this herbivore species.
     */
    @Override
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * @return The breeding age for this herbivore species.
     */
    @Override
    public int getBreedingAge() {
        return breedingAge;
    }

    /**
     * @return The disease spread probability for this herbivore species.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return diseaseSpreadProbability;
    }

    /**
     * @return The death-by-disease probability for this herbivore species.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return deathByDiseaseProbability;
    }

    /**
     * Checks all adjacent location for herbivores that meet breeding conditions.
     *
     * @return Whether this herbivore can breed or not.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= breedingAge && hasCompatibleMateNearby(speciesClass);
    }

    /**
     * Getter method for this herbivore's activeness.
     *
     * @return The activeness value.
     */
    public double getActiveness() {
        return activeness;
    }

    /**
     * Setter method for this herbivore's activeness.
     *
     * @param activeness A given value for activeness.
     */
    public void setActiveness(double activeness) {
        this.activeness = activeness;
    }
}
