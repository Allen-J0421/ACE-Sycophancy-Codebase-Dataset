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
     * Getter method for immutable species configuration.
     *
     * @return The animal's species profile.
     */
    abstract protected AnimalProfile getProfile();

    /**
     * Getter method for the probability to breed of the animal.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return getProfile().getBreedingProbability();
    }

    /**
     * Getter method for the maximum litter size of the animal's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return getProfile().getMaxLitterSize();
    }

    /**
     * Getter method for the maximum age of the animal.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return getProfile().getMaxAge();
    }

    /**
     * Getter method for the age of breeding of the animal.
     *
     * @return An integer value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return getProfile().getBreedingAge();
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
     * Checks whether a nearby animal of the same species can breed with this animal.
     *
     * @param species The species to look for in adjacent locations.
     * @return true if a nearby mate of the opposite sex is available.
     */
    protected boolean hasNearbyMate(Class<? extends Animal> species) {
        if (getAge() < getBreedingAge()) {
            return false;
        }

        for (Location loc : getField().adjacentLocations(getLocation())) {
            Organism organism = Placement.getOccupant(getField(), loc);
            if (species.isInstance(organism)) {
                Animal animal = species.cast(organism);
                if (animal.isMale() != isMale()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determine whether disease kills this animal during the current step.
     *
     * @return true if the animal dies from disease.
     */
    protected boolean diesFromDisease() {
        return rand.nextDouble() <= getDeathByDiseaseProbability();
    }

    /**
     * Choose between spreading disease and searching for food.
     *
     * @return the target location found, or null if no target was found.
     */
    protected Location findFoodOrInfectionTarget() {
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            return findAnimalToInfect();
        }
        return findFood();
    }

    /**
     * Move to the selected location, or remove this animal if overcrowded.
     *
     * @param newLocation The location to move to.
     */
    protected void moveOrRemove(Location newLocation) {
        if (newLocation != null) {
            setLocation(newLocation);
        }
        else {
            remove();
        }
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
            Organism organism = Placement.getOccupant(getField(), loc);
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
     * Getter method to return this animal's disease spreading probability.
     *
     * @return The animal's disease spreading probability.
     */
    protected double getDiseaseSpreadProbability() {
        return getProfile().getDiseaseSpreadProbability();
    }

    /**
     * Getter method to return the probability this animal dies from disease.
     *
     * @return The animal's disease death probability.
     */
    protected double getDeathByDiseaseProbability() {
        return getProfile().getDeathByDiseaseProbability();
    }
}
