import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An animal that sustains itself by hunting: its hunger rises by one each step
 * and is reduced by eating, and it starves to death once its hunger reaches the
 * shared {@link #MAX_HUNGER} threshold. Both predators (which hunt live prey)
 * and scavengers (which eat carrion) are hungry animals; they differ only in
 * what they consider food and the time of day at which they rest.
 *
 * @version 2022.03.02
 */
public abstract class HungryAnimal extends Animal {

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    // A hungry animal starves and dies once its hunger reaches this threshold.
    // It therefore also bounds how many steps any animal can survive without
    // food, so a species' starting food level must not exceed it.
    private static final int MAX_HUNGER = 40;

    // How hungry this animal currently is: 0 means fully fed, and it starves
    // when hunger reaches MAX_HUNGER. Eating lowers it (possibly below zero when
    // very well fed); each step raises it by one.
    private int hunger;

    /**
     * Constructor for a hungry animal in the simulation.
     *
     * @param foodLevel How well fed this animal starts out; a higher value
     *                  means it begins further from starvation. Must not exceed
     *                  {@link #MAX_HUNGER}.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the animal resides.
     * @param location The location in which the animal spawns into.
     */
    public HungryAnimal(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        // Translate the starting food level into a starting hunger: the fuller
        // the animal, the lower (further from MAX_HUNGER) its initial hunger.
        this.hunger = MAX_HUNGER - foodLevel;
    }

    /**
     * What a hungry animal does at every step: age, get hungrier, breed, and -
     * unless it is resting - either spread disease or hunt for food and move.
     * Subclasses vary only in what counts as food ({@link #findFood()}) and the
     * time of day at which they rest ({@link #getInactiveTime()}).
     *
     * @param newAnimals A list of all newborn animals in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newAnimals, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if (!isAlive()) {
            return;
        }

        giveBirth(newAnimals);

        // The animal rests (does nothing further) during its inactive time.
        if (time == getInactiveTime()) {
            return;
        }

        if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
            remove();
            return;
        }

        // Either spread disease to a neighbour or move towards a food source.
        Location newLocation;
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            newLocation = findAnimalToInfect();
        } else {
            newLocation = findFood();
        }

        if (newLocation == null) {
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if (newLocation != null) {
            setLocation(newLocation);
        } else {
            // Overcrowding.
            remove();
        }
    }

    /**
     * Getter method for the time of day at which this animal rests and performs
     * no further actions.
     *
     * @return The TimeOfDay during which the animal is inactive.
     */
    abstract protected TimeOfDay getInactiveTime();

    /**
     * Feed this animal, reducing its hunger by the given food value.
     *
     * @param foodValue The food value gained from eating.
     */
    public void incrementFoodLevel(int foodValue) {
        hunger -= foodValue;
    }

    /**
     * Make this animal hungrier. It starves and dies once its hunger reaches
     * the {@link #MAX_HUNGER} threshold.
     */
    public void incrementHunger() {
        hunger++;
        if (hunger >= MAX_HUNGER) {
            remove();
        }
    }
}
