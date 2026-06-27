import java.util.List;
import java.util.Random;

/**
 * A class representing shared movement, feeding, and breeding behavior for animals.
 *
 * @version 2022/03/02
 */
public abstract class ForagingAnimal extends Animal
{
    private static final Random rand = Randomizer.getRandom();

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    // The age of the animal.
    private int age;
    // The animal's food level.
    private int foodLevel;

    public ForagingAnimal(Field field, Location location)
    {
        super(field, location);
        age = 0;
        foodLevel = 0;
    }

    /**
     * Initialize the shared age and hunger state for a concrete species.
     * @param randomAge Whether the animal should start at a random age.
     * @param maxAge The exclusive upper bound for random age generation.
     * @param foodValue The default food value for a newborn animal.
     */
    protected final void initializeLife(boolean randomAge, int maxAge, int foodValue)
    {
        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(foodValue);
        }
        else {
            age = 0;
            foodLevel = foodValue;
        }
    }

    /**
     * @return The current age of the animal.
     */
    protected final int getAge()
    {
        return age;
    }

    /**
     * @return The current food level of the animal.
     */
    protected final int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Set the current food level.
     * @param foodValue The new food value.
     */
    protected final void setFoodLevel(int foodValue)
    {
        foodLevel = foodValue;
    }

    /**
     * Increase age and kill the animal if it has exceeded its maximum age.
     * @param maxAge The maximum allowed age.
     */
    protected final void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Decrease the current food level and kill the animal if it starves.
     */
    protected final void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Determine whether the animal is old enough to breed.
     * @param breedingAge The minimum breeding age.
     * @return true if the animal is old enough to breed.
     */
    protected final boolean isOldEnoughToBreed(int breedingAge)
    {
        return age >= breedingAge;
    }

    /**
     * Determine whether there is an opposite-sex animal of the given species nearby.
     * @param species The animal type to search for.
     * @param adjacentDistance The search radius.
     * @return true if a mate is nearby.
     */
    protected final boolean hasOppositeSexMate(Class<? extends Animal> species, int adjacentDistance)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), adjacentDistance);
        for(Location where : adjacent) {
            Object occupant = field.getObjectAt(where);
            if(species.isInstance(occupant)) {
                Animal mate = (Animal)occupant;
                if(getSex() != mate.getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Search for prey, spread disease from infected nearby animals, and consume prey.
     * @param disease The disease model.
     * @param step The current simulation step.
     * @param adjacentDistance Search radius.
     * @param foodValue Food value to restore after eating.
     * @param preyTypes The allowed prey classes.
     * @return The location of the prey if found, otherwise null.
     */
    protected final Location searchForPrey(Disease disease, int step, int adjacentDistance, int foodValue, Class<?>... preyTypes)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), adjacentDistance);
        for(Location loc : adjacent) {
            Object creature = field.getObjectAt(loc);
            if(creature instanceof Animal) {
                Animal animal = (Animal)creature;
                if(animal.getIsInfected()) {
                    makeInfected(disease, step);
                }
            }
            if(isOneOf(creature, preyTypes)) {
                Creature prey = (Creature)creature;
                if(prey.isAlive()) {
                    prey.setDead();
                    setFoodLevel(foodValue);
                    return loc;
                }
            }
        }
        return null;
    }

    /**
     * Run the standard animal life-cycle shared by Cod, Salmon, Shark, and Whale.
     * @param newCreatures A list to receive newborn creatures.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The current oxygen level.
     * @param disease The disease model.
     * @param step The current simulation step.
     * @param maxAge The maximum age for this species.
     * @return The oxygen delta for this action.
     */
    protected final double standardAct(List<Creature> newCreatures, boolean atDayTime, double oxygenLevel, Disease disease, int step, int maxAge)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }

        if(dieOfInfection(disease)) {
            return 0;
        }

        ifCanGrantImmunity(disease, step);
        incrementAge(maxAge);
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newCreatures);
            Location newLocation = search(disease, step);
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                setDead();
            }
        }

        return -ANIMAL_OXYGEN_REQUIRED;
    }

    /**
     * Run the common animal act implementation.
     */
    public final double act(List<Creature> newCreatures, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        return standardAct(newCreatures, atDayTime, oxygenLevel, disease, step, getMaxAge());
    }

    /**
     * Create a new offspring at the given location.
     * @param field The field where the offspring will live.
     * @param location The offspring's location.
     * @return The new creature.
     */
    protected abstract Creature createOffspring(Field field, Location location);

    /**
     * Determine whether this animal can currently breed.
     * @return true if breeding is allowed.
     */
    protected abstract boolean canBreed();

    /**
     * @return The probability that breeding succeeds.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum litter size.
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return The maximum age for this species.
     */
    protected abstract int getMaxAge();

    /**
     * Subclasses must provide their prey search implementation through this hook.
     * @param disease The disease model.
     * @param step The current simulation step.
     * @return The location of prey if found.
     */
    public abstract Location search(Disease disease, int step);

    /**
     * Every animal have different gender. and the implementation of this method is at its subclass.
     *
     */
    public abstract boolean encounterWithDiffSex();

    /**
     * identify whether a creature need to sleep
     *
     * @param atDayTime true if it is at day time false if it is at night time.
     * @return true if currently it is night.
     */
    public boolean needSleep(boolean atDayTime)
    {
        return !atDayTime;
    }

    /**
     * Create a birth if breeding conditions are met.
     * @return Number of offspring to create.
     */
    protected final int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Create offspring in adjacent free locations.
     * @param newCreatures A list to receive newborn creatures.
     */
    protected final void giveBirth(List<Creature> newCreatures)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            newCreatures.add(createOffspring(field, free.remove(0)));
        }
    }

    /**
     * Check whether an object is one of the specified classes.
     * @param object The object to inspect.
     * @param classes The allowed classes.
     * @return true if the object matches one of the classes.
     */
    private boolean isOneOf(Object object, Class<?>... classes)
    {
        if(object == null) {
            return false;
        }
        for(Class<?> type : classes) {
            if(type.isInstance(object)) {
                return true;
            }
        }
        return false;
    }
}
