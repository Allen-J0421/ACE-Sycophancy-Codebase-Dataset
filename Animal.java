
import java.util.List;
import java.util.Map;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.01
 */
public abstract class Animal extends Actor
{
    // Steps an infected animal survives before dying.
    private static final int INFECTION_DEATH_STEPS = 5;

    private double foodLevel;
    private boolean isGirl;
    private boolean isHealthy;
    private int infectedStepCounter;
    private int timeLeftUntilBreedingAgain;
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field,location);
        setRandomGender();
        isHealthy = true;
        timeLeftUntilBreedingAgain = isGirl ? getRandom().nextInt(getMaxTimeUntilBreedingAgain()) : 0;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born animals.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newActors, Simulator simulator) {
        addGrowthLevel(getGrowthRate());
        if(simulator.isDay()) {
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newActors);
                Location newLocation = findFood(getFoodProbability(simulator.getWeather()));
                if(newLocation == null) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                if(newLocation != null) {
                    setLocation(newLocation);
                } else {
                    setDead();
                }
            }
        }
    }

    abstract protected double getGrowthRate();

    abstract protected double getFoodProbability(Weather weather);

    /**
     * An animal can breed if it has reached the breeding age.
     * @return true if an animal is able to breed
     */
    protected boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     * Get breeding age of an animal.
     * @return int. Breeding age of the animal;
     */
    abstract protected int getBreedingAge();

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if (timeLeftUntilBreedingAgain != 0){
            timeLeftUntilBreedingAgain --;
        }
        int births = breed();
        for(int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            Animal baby = createOffspring(field, loc);
            if(!getHealth()){
                baby.setUnhealthy();
            }
            newAnimals.add(baby);
        }
    }

    abstract protected Animal createOffspring(Field field, Location location);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * Females can mate with a male even  regardless of the males breeding time .
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && findMate() && getRandom().nextDouble() <= getBreedingProbability() && timeLeftUntilBreedingAgain == 0) {
            births = getRandom().nextInt(getMaxLitterSize()) + 1;
            timeLeftUntilBreedingAgain = getMaxTimeUntilBreedingAgain();
        }
        return births;
    }

    /**
     * If the animal can find a mate of the opposite gender that can breed, true is returned.
     * @return true if the animal can find a mate.
     */
    protected boolean findMate()
    {
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation())) {
            Object animal = field.getObjectAt(where);
            if (animal != null && animal.getClass() == this.getClass()){
                Animal mate = (Animal) animal;
                if(isActive() && getIsGirl() && !mate.getIsGirl() && mate.canBreed()){
                    if(!getHealth() || !mate.getHealth()){
                        setUnhealthy();
                        mate.setUnhealthy();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the breeding probability of the animal
     * @return breeding probability of the animal.
     */
    abstract protected double getBreedingProbability();

    /**
     * Returns the maximum number of babies the animal can give birth to at once.
     * @return max litter size of the animal.
     */
    abstract protected int getMaxLitterSize();

    /**
     * Returns the animal's current food level.
     * @return the animal's food level.
     */
    protected double getFoodLevel(){
        return foodLevel;
    }

    /**
     * Sets the animal's food level.
     * @param foodlevel The new food level.
     */
    protected void setFoodLevel(double foodlevel){
        this.foodLevel = foodlevel;
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for preys adjacent to the current location.
     * Only the first live prey is eaten.
     * @param probability The probability the animal finds prey.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(double probability)
    {
        if(getRandom().nextDouble() < probability && getFoodLevel() < getMaxFoodLevel()){
            Field field = getField();
            for(Location where : field.adjacentLocations(getLocation())) {
                Object animal = field.getObjectAt(where);
                if(animal instanceof Actor){
                    Actor currentAnimal = (Actor) animal;
                    Integer foodValue = getFood().get(currentAnimal.getClass());
                    if(foodValue != null){
                        if (currentAnimal instanceof Animal){
                            Animal current = (Animal)currentAnimal;
                            if(!current.getHealth()){
                                setUnhealthy();
                            }
                        }
                        currentAnimal.setDead();
                        setFoodLevel(Math.min(getFoodLevel() + foodValue + currentAnimal.getGrowthLevel(), getMaxFoodLevel()));
                        return where;
                    }
                }
            }
        }
        return null;
    }

    abstract protected Map<Class<? extends Actor>, Integer> getFood();

    protected void setRandomGender(){
        isGirl = getRandom().nextDouble() > 0.5;
    }

    /**
     * Returns true if the animal is a female.
     * @return boolean. True if the animal is a female.
     */
    protected boolean getIsGirl(){
        return isGirl;
    }

    /**
     * The animal has been infected.
     */
    protected void setUnhealthy(){
        isHealthy = false;
    }

    /**
     * Returns whether the animal is healthy or not.
     * @return true if the animal is healthy.
     */
    protected boolean getHealth(){
        return isHealthy;
    }

    /**
     * Increase the age. This could result in the animal's death.
     * If the animal is unhealthy it also increases the infected step counter.
     * After 5 steps inthe infectedStepCounter this may result in the animal's death.
     * @param step. The number of steps in the simulation.
     */
    protected void incrementAge(int step)
    {
        super.incrementAge(step);
        if(!getHealth()) {
            infectedStepCounter++;
        }
        if(infectedStepCounter == INFECTION_DEATH_STEPS) {
            setDead();
        }
    }

    /**
     * Gets the max food level of an animal.
     * @return Maximum food level of the current animal.
     */
    abstract protected double getMaxFoodLevel();

    /**
     * Gets the max time an animal needs to wait before it can breed again.
     * @return Maximum time the animal waits before it can breed again.
     */
    abstract protected int getMaxTimeUntilBreedingAgain();

}
