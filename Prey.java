import java.util.Random;
import java.util.List;

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
    private static final Random rand = Randomizer.getRandom();
    private int foodValue;
    private double activeness;  // denotes how likely it is for the act method to be called

    /**
     * Constructor for a Prey in the simulation.
     *
     * @param foodValue The value of food of this prey.
     * @param randomAge Whether we assign this prey a random age or not.
     * @param field The field in which this prey resides.
     * @param location The location in which this prey is spawned into.
     */
    public Prey(int initialFoodValue, AnimalAttributes attributes, boolean randomAge,
                Field field, Location location, OrganismFactory offspringFactory) {
        super(attributes, randomAge, field, location, offspringFactory);
        this.foodValue = initialFoodValue;
        this.activeness = DEFAULT_ACTIVENESS;
    }

    /**
     * Method for what the prey does, i.e. what is always run at every step.
     *
     * @param newPrey A list of all newborn prey in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Organism> newPrey, Weather weather, TimeOfDay time) {
        incrementAge();
        setActiveness(getActivenessFor(time));

        if(isAlive()) {
            giveBirth(newPrey);

            if (rand.nextDouble() <= getDeathByDiseaseProbability() ) {
                remove();
                return;
            }

            if (rand.nextDouble() <= getActiveness()){
                Location newLocation = chooseTargetLocation();
                if ((newLocation == null) || (getFoodValue() > 10)) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }

                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    remove();
                }
            }
        } else {
            decayifDead();
        }
    }

    /**
     * Clear the prey from the simulation as it has been eaten.
     */
    @Override
    public void setEaten() {
        clearFromField();
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

    /**
     * Find a nearby plant and consume it if possible.
     *
     * @return The location of the food source, or null if none was eaten.
     */
    protected Location findPlantFood() {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Organism organism = field.getOrganismAt(where);
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if (plant.isAlive()) {
                    plant.setDead();
                    return eat(plant) ? where : null;
                }
            }
        }
        return null;
    }

    private Location chooseTargetLocation() {
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            return findAnimalToInfect();
        }
        return findFood();
    }

    protected double getActivenessFor(TimeOfDay time) {
        return DEFAULT_ACTIVENESS;
    }
}
