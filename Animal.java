
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.01
 */
public abstract class Animal extends Actor
{
    private double foodLevel;
    private boolean isGirl;
    private boolean isHealthy;
    //the number of steps an animal takes when infected.
    private int infectedStepCounter;
    //how many steps animal needs to wait before it can breed again.
    private int timeLeftUntilBreedingAgain = 0;
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
        infectedStepCounter = 0;
        timeLeftUntilBreedingAgain= getRandom().nextInt(getMaxTimeUntilBreedingAgain());
        if(!isGirl){
        timeLeftUntilBreedingAgain = 0;
        }
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newAnimals, Simulator simulator){
        moveAndFeed(simulator);
    }

    /**
     * Perform the standard daytime animal lifecycle.
     * @param newAnimals A list to receive newly born animals.
     * @param simulator The simulator.
     * @param growthIncrement How much the animal grows during this step.
     */
    protected void performDaytimeActions(List<Actor> newAnimals, Simulator simulator, double growthIncrement)
    {
        setGrowthLevel(growthIncrement);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newAnimals);
                moveAndFeed(simulator);
            }
        }
    }

    /**
     * Find food or move into a free adjacent location.
     */
    private void moveAndFeed(Simulator simulator)
    {
        Location newLocation = null;
        switch(simulator.getWeather()){
            case SUNNY:
                newLocation = findFood(getSunnyFindingFoodProbability());
                break;
            case RAINY:
                newLocation = findFood(getRainyFindingFoodProbability());
                break;
            case FOGGY:
                newLocation = findFood(getFoggyFindingFoodProbability());
                break;
            default:
                findFood(getRandom().nextDouble());
                break;
        }
        if(newLocation == null) { 
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }

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
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if (timeLeftUntilBreedingAgain != 0){
            timeLeftUntilBreedingAgain --;
        }
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal baby = createOffspring(field, loc);
            if(!getHealth()){
                baby.setUnhealthy(); //setting the baby to be unhealthy if the parent is unhealthy.
            }
            newAnimals.add(baby);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * Females can mate with a male even  regardless of the males breeding time .
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed()  && findMate() && getRandom().nextDouble() <= getBreedingProbability() && timeLeftUntilBreedingAgain == 0) {
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
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal != null && animal.getClass() == getClass()){
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
     * Create a new offspring of this animal's species.
     * @param field The field the offspring is born into.
     * @param location The location within the field.
     * @return A new offspring.
     */
    abstract protected Animal createOffspring(Field field, Location location);

    /**
     * Returns the animal's current food level.
     * @return the animal's food level.
     */
    protected double getFoodLevel(){
        return foodLevel;
    }

    /**
     * Sets the animal's food level.
     * @param food level The amount of food the animal gets.
     */
    protected void setFoodLevel(double foodlevel){
        this.foodLevel += foodlevel ; 
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

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
       
        if(getRandom().nextDouble() < probability && getFoodLevel() < getMaxFoodLevel()){
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal != null && animal  instanceof Actor){
                    Actor currentAnimal = (Actor) animal;
                    Integer foodValue = getFood().get(currentAnimal.getClass());
                    if(foodValue != null) {
                        if (currentAnimal instanceof Animal){
                            Animal current = (Animal)currentAnimal;
                            if(!current.getHealth()){
                                setUnhealthy();
                            }
                        }
                        currentAnimal.setDead();
                        setFoodLevel(foodValue + currentAnimal.getGrowthLevel());
                        if(getFoodLevel() > getMaxFoodLevel()){
                            setFoodLevel(getMaxFoodLevel() - getFoodLevel());
                        }
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the food values keyed by prey type.
     * @return The food values for the animal's prey types.
     */
    abstract protected Map<Class<? extends Actor>, Integer> getFood();

    /**
     * Uses a random generator to assign if the animal is female or not.
     * @return True if the animal is a female.
     */
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
        if(!getHealth()){
            infectedStepCounter++;}

        if(infectedStepCounter == 5) {
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

    /**
     * Gets the probability the animal will find food when it is sunny
     * @return The probability the animal will find food when it is sunny
     */
    abstract protected double getSunnyFindingFoodProbability();

    /**
     * Gets the probability the animal will find food when it is rainy
     * @return The probability the animal will find food when it is rainy
     */
    abstract protected double getRainyFindingFoodProbability();

    /**
     * Gets the probability the animal will find food when it is foggy
     * @return The probability the animal will find food when it is foggy
     */
    abstract protected double getFoggyFindingFoodProbability();
}
