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

    private final HerbivoreTraits traits;
    private double activeness;

    /**
     * Constructor for a herbivore in the simulation.
     *
     * @param foodValue The value of food of this herbivore.
     * @param randomAge Whether we assign this herbivore a random age or not.
     * @param field The field in which this herbivore resides.
     * @param location The location in which this herbivore is spawned into.
     * @param traits Immutable species configuration.
     */
    public Herbivore(int foodValue, boolean randomAge, Field field, Location location,
                     HerbivoreTraits traits) {
        super(foodValue, randomAge, traits.maxAge, field, location);
        this.traits = traits;
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

            if (rand.nextDouble() <= traits.deathByDiseaseProbability) {
                remove();
                return;
            }

            if (time == traits.reducedActivenessTime) {
                setActiveness(traits.reducedActiveness);
            }

            if (rand.nextDouble() <= getActiveness()) {
                Location newLocation;
                if (rand.nextDouble() <= traits.diseaseSpreadProbability) {
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
     * Checks all adjacent locations for compatible herbivore mates.
     *
     * @return Whether this herbivore can breed or not.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= traits.breedingAge && hasCompatibleMateNearby(traits.speciesClass);
    }

    /**
     * @return The breeding probability for this herbivore species.
     */
    @Override
    public double getBreedingProbability() {
        return traits.breedingProbability;
    }

    /**
     * @return The maximum litter size for this herbivore species.
     */
    @Override
    public int getMaxLitterSize() {
        return traits.maxLitterSize;
    }

    /**
     * @return The maximum age for this herbivore species.
     */
    @Override
    public int getMaxAge() {
        return traits.maxAge;
    }

    /**
     * @return The breeding age for this herbivore species.
     */
    @Override
    public int getBreedingAge() {
        return traits.breedingAge;
    }

    /**
     * @return The disease spread probability for this herbivore species.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return traits.diseaseSpreadProbability;
    }

    /**
     * @return The death-by-disease probability for this herbivore species.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return traits.deathByDiseaseProbability;
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
