import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An animal that survives by seeking nearby food.
 *
 * @version 2022.03.02
 */
public abstract class Hunter extends Animal {

    private static final Random rand = Randomizer.getRandom();

    private int foodLevel;

    /**
     * Constructor for a hunter in the simulation.
     *
     * @param foodLevel The food level of this hunter.
     * @param randomAge Whether the hunter should have a random age or not.
     * @param field The field in which the hunter resides.
     * @param location The location in which the hunter spawns into.
     */
    public Hunter(HunterAttributes attributes, boolean randomAge, Field field,
                  Location location, OrganismFactory offspringFactory) {
        super(attributes, randomAge, field, location, offspringFactory);
        this.foodLevel = attributes.getInitialFoodLevel();
    }

    @Override
    public void act(List<Organism> newHunters, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newHunters);

            if (isRestingTime(time)) {
                return;
            }

            if (rand.nextDouble() <= getDeathByDiseaseProbability() ) {
                remove();
                return;
            }

            Location newLocation = chooseTargetLocation();
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                remove();
            }
        }
    }

    /**
     * Increase the hunter's food level by a given integer amount.
     *
     * @param foodLevel The value to increment food level by.
     */
    protected void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this hunter more hungry. This could result in the hunter's death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }

    private Location chooseTargetLocation() {
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            return findAnimalToInfect();
        }
        return findFood();
    }

    protected abstract boolean isRestingTime(TimeOfDay time);
}
