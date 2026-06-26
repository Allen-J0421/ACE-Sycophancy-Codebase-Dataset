import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract animal organism present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism implements AbleToEat {

    // define fields
    private final Gender gender; // gender of specific animal
    private boolean infected; // whether an animal has been infected or not

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for an animal in the simulation.
     *
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the animal resides.
     * @param location The location in which the animal spawns into.
     */
    public Animal(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        // randomly assign male or female
        this.gender = Gender.getRandom();
        this.infected = false;
    }

    /**
     * Abstract method for what the animal does, i.e. what is always run at every step.
     *
     * @param newAnimals A list of all newborn animals in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    abstract public void act(List<Entity> newAnimals, Weather weather, TimeOfDay time);

    /**
     * Called when a consumable food item may be eaten.
     *
     * @param consumable The item to be eaten.
     * @return Whether the item was eaten or not.
     */
    @Override
    abstract public boolean eat(Consumable consumable);

    /**
     * Checks all adjacent location for animals that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this animal can breed or not.
     */
    @Override
    abstract protected boolean canBreed();

    /**
     * Getter method returning the gender of this animal.
     *
     * @return The gender of the animal.
     */
    private Gender getGender() {
        return this.gender;
    }

    /**
     * Returns if the animal is a male or not.
     *
     * @return Whether the animal's gender is male or not.
     */
    protected boolean isMale() {
        return getGender() == Gender.MALE;
    }

    /**
     * Getter method returning whether the animal has been
     * infected or not.
     *
     * @return Whether the animal is infectious or not.
     */
    protected boolean isInfected() {
        return this.infected;
    }

    /**
     * Setter method to alter the infected field of this animal
     * to a given boolean.
     *
     * @param infected The value the infected field should be changed to.
     */
    private void setInfected(boolean infected) {
        this.infected = infected;
    }

    /**
     * Infects a given animal.
     *
     * @param animal The animal to infect.
     */
    protected void infect(Animal animal) {
        animal.setInfected(true);
    }

    /**
     * Check whether there is an adjacent, alive animal of the requested
     * species with the opposite gender.
     *
     * @param species The species to search for.
     * @return true if a compatible mate is nearby.
     */
    protected boolean hasCompatibleMateNearby(Class<? extends Animal> species) {
        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object organism = getField().getObjectAt(loc);
            if (species.isInstance(organism)) {
                Animal mate = (Animal) organism;
                if (mate.isAlive() && mate.isMale() != isMale()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the first adjacent object of the requested type that satisfies the given handler.
     *
     * @param targetType The type of adjacent object to search for.
     * @param handler A predicate that decides whether the object should be accepted.
     * @param <T> The concrete adjacent object type.
     * @return The matching location, or null if none matched.
     */
    protected <T> Location findAdjacentObject(Class<T> targetType, Predicate<? super T> handler) {
        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object organism = getField().getObjectAt(loc);
            if (targetType.isInstance(organism)) {
                T target = targetType.cast(organism);
                if (handler.test(target)) {
                    return loc;
                }
            }
        }
        return null;
    }

    /**
     * Find an animal in an adjacent location for this animal to infect.
     *
     * @return The location of a nearby animal that can be infected.
     */
    protected Location findAnimalToInfect() {
        if (!infected) {
            return null;
        }

        return findAdjacentObject(Animal.class, animal -> {
            if (!animal.isAlive() || animal.isInfected()) {
                return false;
            }
            if (rand.nextDouble() <= 0.01) {
                infect(animal);
            }
            return true;
        });
    }

    /**
     * Getter method to return this animal's disease spreading probability.
     *
     * @return The animal's disease spreading probability.
     */
    abstract protected double getDiseaseSpreadProbability();

    /**
     * Getter method to return the probability this animal dies from disease.
     *
     * @return The animal's disease death probability.
     */
    abstract protected double getDeathByDiseaseProbability();
}
