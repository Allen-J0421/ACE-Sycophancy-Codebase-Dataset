import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An animal that survives by seeking nearby food.
 *
 * @version 2022.03.02
 */
public abstract class Hunter extends ForagingAnimal {

    private final HunterAttributes attributes;
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
        this.attributes = attributes;
        this.foodLevel = attributes.getInitialFoodLevel();
    }

    @Override
    public void act(List<Organism> newHunters, SimulationContext context) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newHunters);

            if (isRestingTime(context.getTimeOfDay())) {
                return;
            }

            if (diesFromDiseaseThisTurn()) {
                remove();
                return;
            }

            moveToTargetOrRemove(chooseTargetLocation());
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

    protected boolean isRestingTime(TimeOfDay time) {
        return attributes.getRestingTime() == time;
    }

    protected double getEatingProbability() {
        return attributes.getEatingProbability();
    }

    protected Location findMatchingPrey(Predicate<Prey> matches, Function<Prey, Boolean> onMatch) {
        for (Location location : getField().adjacentLocations(getLocation())) {
            Organism organism = getField().getOrganismAt(location);
            if (organism instanceof Prey) {
                Prey prey = (Prey) organism;
                if (matches.test(prey) && Boolean.TRUE.equals(onMatch.apply(prey))) {
                    return location;
                }
            }
        }
        return null;
    }
}
