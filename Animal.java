import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // The animal's hurt level, when > 3 die
    private int burn;
    // The animal's gender.
    private final Gender gender;
    // The animal's age.
    private int age;
    // The animal's food level.
    private int foodLevel;

    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);
        burn = 0;
        gender = Gender.random(rand);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param step The current step.
     * @param weather The current weather.
     */
    abstract public void act(List<Animal> newAnimals, int step, Weather weather);

    /**
     * Return the food value this animal provides when eaten.
     */
    abstract public int foodValue();

    /**
     * A factory for creating offspring at a specific location.
     */
    @FunctionalInterface
    protected interface OffspringFactory
    {
        Animal create(Field field, Location location);
    }
    
    /**
     * update the animal's burn status.
     * @param weather The current weather.
     */
    protected void updateBurnStatus(Weather weather)
    {
        if (burn > 0) {
            if (weather != Weather.RAINY) {
                burn();
            }
            else {
                recover();
            }
        }
    }
    
    /**
     * animal that step on fire will get burn and if burn status > 3 the animal will die 
     */
    protected void burn()
    {
        burn++;
        if (burn > 3) {
            setDead();
        }
    }
    
    /**
     * if weather is rainy the animal will recover and reset it's burn status to zero.
     */
    protected void recover()
    {
        burn = 0;
    }

    /**
     * @return the animal's gender
     */
    protected final Gender getGender()
    {
        return gender;
    }

    /**
     * Move to the new location or die if there is nowhere to go.
     */
    protected final void moveToOrDie(Location newLocation)
    {
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }

    /**
     * Initialize the animal's age.
     */
    protected final void initializeAge(boolean randomAge, int maxAge)
    {
        if(randomAge) {
            age = rand.nextInt(maxAge);
        }
        else {
            age = 0;
        }
    }

    /**
     * Initialize the animal's food level.
     */
    protected final void initializeFoodLevel(boolean randomFoodLevel, int randomUpperBound,
                                             int initialFoodLevel)
    {
        if(randomFoodLevel) {
            foodLevel = rand.nextInt(randomUpperBound);
        }
        else {
            foodLevel = initialFoodLevel;
        }
    }

    /**
     * Increase the animal's age and kill it if it exceeds the limit.
     */
    protected final void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Decrease the animal's food level and kill it if depleted.
     */
    protected final void decrementFoodLevel()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Adjust the animal's food level by the given amount.
     */
    protected final void changeFoodLevel(int delta)
    {
        foodLevel = foodLevel + delta;
    }

    /**
     * Return the animal's age.
     */
    protected final int getAge()
    {
        return age;
    }

    /**
     * Return the animal's food level.
     */
    protected final int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Determine how many offspring are produced this step.
     */
    protected final int calculateBirths(int breedingAge, double breedingProbability,
                                        int maxLitterSize)
    {
        if(age >= breedingAge && rand.nextDouble() <= breedingProbability) {
            return rand.nextInt(maxLitterSize) + 1;
        }
        return 0;
    }

    /**
     * Add offspring into available adjacent locations.
     */
    protected final void addOffspring(List<Animal> newAnimals, int births,
                                      OffspringFactory offspringFactory)
    {
        Field field = getField();
        List<Location> free = freeAdjacentLocations();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location location = free.remove(0);
            newAnimals.add(offspringFactory.create(field, location));
        }
    }

    /**
     * Check whether a mate of the same species is nearby.
     */
    protected final boolean hasAdjacentMate(Class<? extends Animal> animalClass, int radius)
    {
        for(Location where : adjacentLocations(radius)) {
            Animal mate = getObjectAt(where, animalClass);
            if(mate != null && mate.getGender() != getGender()) {
                return true;
            }
        }
        return false;
    }
}
