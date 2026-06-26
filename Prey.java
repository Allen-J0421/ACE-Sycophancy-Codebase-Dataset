import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Abstract class for a prey in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Prey extends Animal implements Consumable {

    // define fields
    private static final double DEFAULT_ACTIVENESS = 1;
    // Food value above which prey prefers to wander rather than keep eating.
    private static final int SATIATED_FOOD_VALUE = 10;
    private int foodValue;
    private double activeness;  // denotes how likely it is for the act method to be called

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a Prey in the simulation.
     *
     * @param foodValue The value of food of this prey.
     * @param randomAge Whether we assign this prey a random age or not.
     * @param field The field in which this prey resides.
     * @param location The location in which this prey is spawned into.
     */
    public Prey(int foodValue, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.foodValue = foodValue;
        this.activeness = DEFAULT_ACTIVENESS;
    }

    /**
     * What the prey does at every step: age and (if alive) breed, possibly die
     * of disease, then - subject to its activeness - spread disease or forage
     * and move. A dead prey instead decays. This behaviour is shared by all
     * prey; species vary only in the time of day they are less active and by
     * how much.
     *
     * @param newPrey A list of all newborn prey in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newPrey, Weather weather, TimeOfDay time) {
        incrementAge();
        setActiveness(DEFAULT_ACTIVENESS); // reset activeness each step

        if (!isAlive()) {
            decayifDead();
            return;
        }

        giveBirth(newPrey);

        if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
            remove();
            return;
        }

        if (time == getLowActivityTime()) {
            setActiveness(getLowActiveness());
        }

        if (rand.nextDouble() <= getActiveness()) {
            // Either spread disease to a neighbour or look for food.
            Location newLocation;
            if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
                newLocation = findAnimalToInfect();
            } else {
                newLocation = findFood();
            }

            // Wander to a free location if no food was found or already satiated.
            if (newLocation == null || getFoodValue() > SATIATED_FOOD_VALUE) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                remove();
            }
        }
    }

    /**
     * Find an adjacent living plant for this prey to eat, returning its
     * location once eaten. All prey graze on plants.
     *
     * @return The location of the food source, or null if none was eaten.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation()).iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if (plant.isAlive()) {
                    plant.setDead();
                    boolean eaten = eat(plant);
                    return eaten ? where : null;
                }
            }
        }
        return null;
    }

    /**
     * Getter method for the time of day at which this prey becomes less active.
     *
     * @return The TimeOfDay during which the prey is less active.
     */
    abstract protected TimeOfDay getLowActivityTime();

    /**
     * Getter method for this prey's activeness during its low-activity time.
     *
     * @return A double value representing the reduced activeness.
     */
    abstract protected double getLowActiveness();

    /**
     * Clear the prey from the simulation as it has been eaten.
     */
    @Override
    public void setEaten() {
        if(getLocation() != null) {
            getField().clear(getLocation());
            setLocationToNull();
            setField(null);
        }
    }

    /**
     * Getter method for this prey's food value.
     * @return The food value of the prey.
     */
    @Override
    public int getFoodValue() {
        return this.foodValue;
    }

    /**
     * Increment the food value of this prey by a specified amount.
     * @param foodValue The given amount to increase the food value by.
     */
    protected void incrementFoodValue(int foodValue) {
        this.foodValue += foodValue;
    }

    /**
     * Called when this prey eats a plant.
     *
     * @param consumable The plant to be eaten.
     * @return Whether the plant was eaten.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if ((consumable.isPoisonous()) && (!isInfected())) {
            infect(this);
        }
        incrementFoodValue(consumable.getFoodValue());
        consumable.setEaten();
        return true;
    }

    /**
     * Prey can never be poisonous.
     * @return false as prey don't have this attribute.
     */
    @Override
    public boolean isPoisonous() {
        return false;
    }

    /**
     * The activeness of this prey.
     * @return A double value for the activeness of this prey
     */
    public double getActiveness() {
        return activeness;
    }

    /**
     * Setter method for the activeness of this prey.
     *
     * @param activeness A given value for activeness.
     */
    public void setActiveness(double activeness) {
        this.activeness = activeness;
    }
}
