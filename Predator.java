import java.util.Iterator;
import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A predator animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Predator extends HungryAnimal {

    // define fields
    private final double eatingProbability;

    /**
     * Constructor for a predator in the simulation.
     *
     * @param foodLevel The food level of this predator.
     * @param randomAge Whether the predator should have a random age or not.
     * @param field The field in which the predator resides.
     * @param location The location in which the predator spawns into.
     */
    public Predator(int foodLevel, boolean randomAge, Field field, Location location,
                    Class<? extends Animal> speciesClass, double breedingProbability,
                    int maxLitterSize, int breedingAge, int maxAge,
                    double diseaseSpreadProbability, double deathByDiseaseProbability,
                    TimeOfDay restingTime, double eatingProbability) {
        super(foodLevel, randomAge, field, location, speciesClass, breedingProbability,
                maxLitterSize, breedingAge, maxAge, diseaseSpreadProbability,
                deathByDiseaseProbability, restingTime);
        this.eatingProbability = eatingProbability;
    }

    /**
     * Finds the nearest food source and returns its location.
     *
     * @return Location of food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();

            Object animal = field.getObjectAt(where);
            if(animal instanceof Prey) {
                Prey prey = (Prey) animal;
                if (prey.isAlive()) {
                    // kills animal
                    prey.setDead();
                    // random chance to eat
                    boolean eaten = eat(prey);

                    return eaten ? where : null;
                }
            }
        }
        return null;
    }

    /**
     * Called when a predator either eats or leaves a prey it
     * has killed.
     *
     * @param consumable The prey to be eaten.
     * @return Whether the prey has been eaten or not.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if (Randomizer.getRandom().nextDouble() <= eatingProbability) {
            incrementFoodLevel(consumable.getFoodValue());
            consumable.setEaten();
            return true;
        }
        return false;
    }
}
