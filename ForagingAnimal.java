import java.util.List;
import java.util.Random;

/**
 * Shared movement, feeding, and breeding behavior for foraging animals.
 */
public abstract class ForagingAnimal extends Animal
{
    private static final Random rand = Randomizer.getRandom();

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    private final ForagingBehavior behavior;
    // The age of the animal.
    private int age;
    // The animal's food level.
    private int foodLevel;

    protected ForagingAnimal(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        this.behavior = ForagingBehaviorFactory.forAnimal(getClass().asSubclass(ForagingAnimal.class));
        initializeLife(randomAge);
    }

    /**
     * Initialize the shared age and hunger state for a concrete species.
     * @param randomAge Whether the animal should start at a random age.
     */
    protected final void initializeLife(boolean randomAge)
    {
        if(randomAge) {
            age = rand.nextInt(behavior.getMaxAge());
            foodLevel = rand.nextInt(behavior.getFoodValue());
        }
        else {
            age = 0;
            foodLevel = behavior.getFoodValue();
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
     */
    protected final void incrementAge()
    {
        age++;
        if(age > behavior.getMaxAge()) {
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
     * @return true if the animal is old enough to breed.
     */
    protected final boolean isOldEnoughToBreed()
    {
        return age >= behavior.getBreedingAge();
    }

    /**
     * Search for prey, spread disease from infected nearby animals, and consume prey.
     * @param disease The disease model.
     * @param step The current simulation step.
     * @return The location of the prey if found, otherwise null.
     */
    public final Location search(Disease disease, int step)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), behavior.getSearchDistance());
        for(Location loc : adjacent) {
            Object creature = field.getObjectAt(loc);
            if(creature instanceof Animal) {
                Animal animal = (Animal)creature;
                if(animal.getIsInfected()) {
                    makeInfected(disease, step);
                }
            }
            if(isOneOf(creature, behavior.getPreyTypes())) {
                Creature prey = (Creature)creature;
                if(prey.isAlive()) {
                    prey.setDead();
                    setFoodLevel(behavior.getFoodValue());
                    return loc;
                }
            }
        }
        return null;
    }

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
     * Run the standard animal life-cycle shared by foraging animals.
     * @param newCreatures A list to receive newborn creatures.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The current oxygen level.
     * @param disease The disease model.
     * @param step The current simulation step.
     * @return The oxygen delta for this action.
     */
    public final double act(List<Creature> newCreatures, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }

        if(dieOfInfection(disease)) {
            return 0;
        }

        ifCanGrantImmunity(disease, step);
        incrementAge();
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
     * Create a birth if breeding conditions are met.
     * @return Number of offspring to create.
     */
    protected final int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= behavior.getBreedingProbability()) {
            births = rand.nextInt(behavior.getMaxLitterSize()) + 1;
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
            newCreatures.add(behavior.createOffspring(field, free.remove(0)));
        }
    }

    /**
     * Determine whether this animal can currently breed.
     * @return true if breeding is allowed.
     */
    protected final boolean canBreed()
    {
        return isOldEnoughToBreed() && (!behavior.breedingRequiresMate() || hasOppositeSexMate(behavior.getMateSpecies(), behavior.getMateDistance()));
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

    /**
     * Determine whether there is an opposite-sex animal of the configured species nearby.
     * @return true if a mate is nearby.
     */
    private boolean hasOppositeSexMate(Class<? extends Animal> species, int adjacentDistance)
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
}
