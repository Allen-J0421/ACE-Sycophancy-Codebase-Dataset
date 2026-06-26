package safari;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.01
 */
public abstract class Animal extends Actor
{
    private final SpeciesType speciesType;
    private final SpeciesConfig speciesConfig;

    private double foodLevel;
    private boolean isGirl;
    private boolean isHealthy;
    // The number of steps an animal takes when infected.
    private int infectedStepCounter;
    // How many steps an animal needs to wait before it can breed again.
    private int timeLeftUntilBreedingAgain = 0;

    /**
     * Create a new animal at location in field.
     *
     * @param speciesType The animal species.
     * @param randomAge True if the animal should start with a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Animal(SpeciesType speciesType, boolean randomAge, Field field, Location location)
    {
        super(field, location);
        this.speciesType = speciesType;
        this.speciesConfig = SpeciesRegistry.config(speciesType);
        setRandomGender();
        isHealthy = true;
        infectedStepCounter = 0;
        if(randomAge) {
            setAge(getRandom().nextInt(speciesConfig.maxAge()));
            setFoodLevel(getRandom().nextInt(speciesConfig.randomFoodUpperBound()));
        }
        else {
            setAge(0);
            setFoodLevel(speciesConfig.initialFoodLevel());
        }
        timeLeftUntilBreedingAgain = getRandom().nextInt(speciesConfig.maxTimeUntilBreedingAgain());
        if(!isGirl) {
            timeLeftUntilBreedingAgain = 0;
        }
        setGrowthLevel(getAge() / speciesConfig.initialGrowthScale());
    }

    /**
     * Make this animal act - that is: make it do whatever it wants or needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newAnimals, Simulator simulator)
    {
        if(!simulator.isDay()) {
            return;
        }

        setGrowthLevel(speciesConfig.actGrowthIncrement());
        incrementAge(simulator.getSteps());
        incrementHunger();
        if(isActive()) {
            giveBirth(newAnimals);
            Location newLocation = findFood(speciesConfig.foodFindingProbability(simulator.getWeather()));
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
    }

    /**
     * An animal can breed if it has reached the breeding age.
     * @return true if an animal is able to breed
     */
    protected boolean canBreed()
    {
        return getAge() >= speciesConfig.breedingAge();
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(timeLeftUntilBreedingAgain != 0) {
            timeLeftUntilBreedingAgain--;
        }
        int births = breed();
        for(int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            Animal baby = SpeciesRegistry.create(speciesType, false, field, loc);
            if(!getHealth()) {
                baby.setUnhealthy();
            }
            newAnimals.add(baby);
        }
    }

    /**
     * Generate a number representing the number of births, if it can breed.
     * Females can mate with a male even regardless of the male's breeding time.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && findMate() && getRandom().nextDouble() <= speciesConfig.breedingProbability() && timeLeftUntilBreedingAgain == 0) {
            births = getRandom().nextInt(speciesConfig.maxLitterSize()) + 1;
            timeLeftUntilBreedingAgain = speciesConfig.maxTimeUntilBreedingAgain();
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
            Actor occupant = field.getActorAt(where);
            if(occupant instanceof Animal mate && mate.speciesType == speciesType) {
                if(isActive() && getIsGirl() && !mate.getIsGirl() && mate.canBreed()) {
                    if(!getHealth() || !mate.getHealth()) {
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
     * Returns the animal's current food level.
     * @return the animal's food level.
     */
    protected double getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Sets the animal's food level.
     * @param foodLevel The amount of food the animal gets.
     */
    protected void setFoodLevel(double foodLevel)
    {
        this.foodLevel += foodLevel;
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
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @param probability The probability the animal finds prey.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(double probability)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());

        if(getRandom().nextDouble() < probability && getFoodLevel() < speciesConfig.maxFoodLevel()) {
            for(Location where : adjacent) {
                Actor occupant = field.getActorAt(where);
                if(occupant instanceof Actor currentAnimal) {
                    for(Map.Entry<Class<? extends Actor>, Integer> prey : speciesConfig.food().entrySet()) {
                        if(prey.getKey() == currentAnimal.getClass()) {
                            if(currentAnimal instanceof Animal current && !current.getHealth()) {
                                setUnhealthy();
                            }
                            currentAnimal.setDead();
                            setFoodLevel(prey.getValue() + currentAnimal.getGrowthLevel());
                            if(getFoodLevel() > speciesConfig.maxFoodLevel()) {
                                setFoodLevel(speciesConfig.maxFoodLevel() - getFoodLevel());
                            }
                            return where;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Uses a random generator to assign if the animal is female or not.
     */
    protected void setRandomGender()
    {
        isGirl = getRandom().nextDouble() > 0.5;
    }

    /**
     * Returns true if the animal is a female.
     * @return true if the animal is a female.
     */
    protected boolean getIsGirl()
    {
        return isGirl;
    }

    /**
     * The animal has been infected.
     */
    protected void setUnhealthy()
    {
        isHealthy = false;
    }

    /**
     * Returns whether the animal is healthy or not.
     * @return true if the animal is healthy.
     */
    protected boolean getHealth()
    {
        return isHealthy;
    }

    /**
     * Increase the age. This could result in the animal's death.
     * If the animal is unhealthy it also increases the infected step counter.
     * After 5 steps in the infectedStepCounter this may result in the animal's death.
     * @param step The number of steps in the simulation.
     */
    protected void incrementAge(int step)
    {
        super.incrementAge(step);
        if(!getHealth()) {
            infectedStepCounter++;
        }

        if(infectedStepCounter == 5) {
            setDead();
        }
    }

    /**
     * Gets the maximum age for this animal.
     * @return The maximum age of the current animal.
     */
    protected int getMaxAge()
    {
        return speciesConfig.maxAge();
    }
}
