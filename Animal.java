import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.01
 */
public abstract class Animal extends Organism
{
    // The level of food an animal has consumed, increases when it eats
    private int foodLevel;
    // Indicates whether an animal can give birth or not
    private boolean female;
    // Indicates whether the animal is nocturnal or not
    private boolean nocturnal;
    // The animal's age
    private double age;
    // Indicates whether the animal is sleeping or not
    private boolean sleeping;
    // Determines where the animal moves each step; swappable per-species or at runtime.
    private MovementStrategy movementStrategy;
    // Shared random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     *
     * @param randomAge If true, the animal will have a random age
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        nocturnal = false;
        female = rand.nextBoolean();
        setWaterLevel(rand.nextInt(10) + 5);
        sleeping = false;
        movementStrategy = new StandardMovementStrategy();
        if(!randomAge) {
            setAge(0);
            foodLevel = rand.nextInt(10) + 8;
        }
    }

    /**
     * Replace the movement strategy used by this animal.
     * Call from a subclass constructor to give a species a custom strategy.
     * @param strategy The new movement strategy.
     */
    protected void setMovementStrategy(MovementStrategy strategy)
    {
        movementStrategy = strategy;
    }

    // --- Abstract methods each subclass must define ---

    /** @return The minimum age at which this animal can breed. */
    abstract protected int getBreedingAge();

    /** @return The probability of breeding when conditions are met. */
    abstract protected double getBreedingProbability();

    /** @return The maximum number of offspring per birth event. */
    abstract protected int getMaxLitterSize();

    /** @return The maximum age before this animal dies. */
    abstract public int getMaxAge();

    /** @return A new instance of this animal type placed at loc. */
    abstract protected Animal createOffspring(Field field, Location loc);

    /** @return Returns list of prey for each animal */
    abstract public ArrayList<String> getPrey();

    /**
     * @return The probability of a successful hunt (default 1.0 — always succeeds).
     *         Override in subclasses that have a hunt success rate.
     */
    protected double getHuntProbability() { return 1.0; }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Actor> newOrganisms) {
        super.act(newOrganisms);
        incrementHunger();
        if(isAlive() && !sleeping) {
            giveBirth(newOrganisms);
            Location newLocation = movementStrategy.selectDestination(this);
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                setDead();
            }
        }
        else if (sleeping) {
            sleeping = false;
        }
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    @Override
    public void incrementAge() {
        super.incrementAge();
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Move towards another animal of the same species to find a mate to breed with.
     *
     * @return The location of a potential mate if found, null if not
     */
    public Location findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            List<Location> adjacent2 = field.adjacentLocations(where);
            Iterator<Location> it2 = adjacent2.iterator();
            while (it2.hasNext()) {
                Location where2 = it2.next();
                Object animal = field.getObjectAt(where);
                if(animal != null && animal.getClass().equals(this.getClass())) {
                    return where2;
                }
            }
        }
        return null;
    }

    /**
     * Look for prey adjacent to the current location.
     * Hunt success is gated by getHuntProbability().
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(ArrayList<String> preyList)
    {
        if(rand.nextDouble() >= getHuntProbability()) {
            return null;
        }
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getObjectAt(where);
            if(food != null) {
                if(preyList.contains(food.getClass().getName())) {
                    Organism prey = (Organism) food;
                    if(prey.isAlive()) {
                        prey.setDead();
                        setFoodLevel(prey.getFoodValue() + getFoodLevel());
                        setWaterLevel(getWaterLevel() + 5);
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Look for water adjacent to the animal's current location
     * @return Where water was found, or null if it wasn't
     */
    protected Location findWater() {
        super.findWater();
        return null;
    }

    /**
     * Make the animal more hungry. This could result in the animal's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * Finds an adjacent mate of opposite sex, then spawns offspring into free locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        int births = breed();
        if(births == 0) return;

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object other = field.getObjectAt(where);
            if(other != null && this.getClass().equals(other.getClass())) {
                Animal mate = (Animal) other;
                if(isFemale() != mate.isFemale()) {
                    for(int b = 0; b < births && !free.isEmpty(); b++) {
                        Location loc = free.remove(0);
                        Animal young = createOffspring(field, loc);
                        if(isInfected()) {
                            young.setInfected();
                        }
                        newAnimals.add(young);
                    }
                }
                return;
            }
        }
    }

    /**
     * A animal can breed if it is female and has reached the breeding age.
     */
    private boolean canBreed()
    {
        return isFemale() && getAge() >= getBreedingAge();
    }

    /**
     * Generate a number representing the number of births, if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * @param foodValue The level of food an animal has eaten
     */
    protected void setFoodLevel(int foodValue)
    {
        foodLevel = foodValue;
    }

    /**
     * @return The level of food an animal has eaten
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * @return True if this animal is a female, capable of breeding
     */
    public boolean isFemale()
    {
        return female;
    }

    /**
     * Mark this animal as nocturnal.
     */
    public void setNocturnal()
    {
        nocturnal = true;
    }

    /**
     * @return True if this animal is nocturnal, false if not
     */
    public boolean isNocturnal()
    {
        return nocturnal;
    }

    /**
     * Sets the sleeping boolean variable to the opposite state it currently has.
     */
    public void setSleepStatus(){
        sleeping = !sleeping;
    }
}
