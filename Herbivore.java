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
    private static final double DEFAULT_ACTIVENESS = 1;

    private double activeness;

    /**
     * Constructor for a herbivore in the simulation.
     *
     * @param foodValue The value of food of this herbivore.
     * @param randomAge Whether we assign this herbivore a random age or not.
     * @param field The field in which this herbivore resides.
     * @param location The location in which this herbivore is spawned into.
     */
    public Herbivore(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
        this.activeness = DEFAULT_ACTIVENESS;
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
        setActiveness(DEFAULT_ACTIVENESS);

        if (isAlive()) {
            giveBirth(newHerbivores);

            if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
                remove();
                return;
            }

            if (time == getReducedActivenessTime()) {
                setActiveness(getReducedActiveness());
            }

            if (rand.nextDouble() <= getActiveness()) {
                Location newLocation;
                if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
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
     * @return The time of day when this herbivore is less active.
     */
    protected abstract TimeOfDay getReducedActivenessTime();

    /**
     * @return The activeness value used at the reduced-activeness time.
     */
    protected abstract double getReducedActiveness();

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
