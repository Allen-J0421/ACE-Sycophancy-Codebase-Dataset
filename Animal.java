import java.util.List;
import java.util.Random;

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
     * Checks all adjacent locations for an animal of the same species and
     * opposite gender, which is the shared breeding condition for every animal.
     *
     * @return Whether this animal can breed or not.
     */
    @Override
    protected boolean canBreed() {
        if (getAge() < getBreedingAge()) {
            return false;
        }

        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object occupant = getField().getObjectAt(loc);
            // Same species (exact class) and opposite gender means breeding is possible.
            if (occupant != null && occupant.getClass() == getClass()) {
                Animal mate = (Animal) occupant;
                if (mate.isMale() != isMale()) {
                    return true;
                }
            }
        }
        return false;
    }

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
     * Find an animal in an adjacent location for this animal to infect.
     *
     * @return The location of a nearby animal that can be infected.
     */
    protected Location findAnimalToInfect() {
        if (!infected) {
            return null;
        }

        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object organism = getField().getObjectAt(loc);
            if (organism instanceof Animal) {
                Animal animal = (Animal) organism;
                if (animal.isAlive() && (!animal.isInfected())) {
                    if (rand.nextDouble() <= 0.01) {
                        infect(animal);
                    }
                    return loc;
                }
            }
        }

        return null;
    }

    /**
     * Look in adjacent locations for living food of the given type. The first
     * such organism found is killed and an attempt is made to eat it. This is
     * the common hunting behaviour for predators (which eat live prey) and
     * herbivorous prey (which eat live plants).
     *
     * @param foodType The kind of consumable organism this animal eats.
     * @return The location of the food if it was eaten, otherwise null.
     */
    protected Location huntLivingFood(Class<? extends Consumable> foodType) {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Object occupant = field.getObjectAt(where);
            if (foodType.isInstance(occupant)) {
                // Every Consumable in this simulation is also an Organism.
                Organism food = (Organism) occupant;
                if (food.isAlive()) {
                    food.setDead();
                    boolean eaten = eat((Consumable) occupant);
                    return eaten ? where : null;
                }
            }
        }
        return null;
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