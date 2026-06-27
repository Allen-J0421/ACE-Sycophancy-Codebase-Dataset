import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.01
 */
public abstract class Animal extends Organism
{
    // Shared random source for animal lifecycle decisions.
    private static final Random rand = Randomizer.getRandom();
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
        if(!randomAge) {
            setAge(0);
            foodLevel = rand.nextInt(10) + 8;
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Actor> newOrganisms) {
        // All animals eat; all organisms also increment age and water level
        super.act(newOrganisms);
        incrementHunger();
        if(isAlive() && !sleeping) {        
            // Move towards a source of food if found
            giveBirth(newOrganisms);  
            Location newLocation = null;
            if(getWaterLevel() < 3) {
                newLocation = findWater();
            }
            if (newLocation == null && getFoodLevel() < 8 && rand.nextDouble() <= getHuntProbability()) {
                newLocation = findFood(getPrey());
            }
            if (newLocation == null) {
                newLocation = findMate();
            }
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
        else if (sleeping) {
            // code for what to do when sleeping
            sleeping = false;
        }
    }
    
    /**
     * Move towards another animal of the same species to find a mate to breed with
     * 
     * @return The location of a potential mate if found, null if not
     */
    protected Location findMate() 
    {
        if (hasCompatibleMateAdjacent()) {
            return getField().freeAdjacentLocation(getLocation());
        }
        return null;
    }

    /**
     * Increase the age and kill the animal when it exceeds its lifespan.
     */
    @Override
    public void incrementAge()
    {
        super.incrementAge();
        if (getAge() > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(List<String> preyList)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getObjectAt(where);
            if(food!= null) {
                if(preyList.contains(food.getClass().getName())) {
                    Organism prey = (Organism) food;
                    if(prey.isAlive()) { 
                        prey.setDead();
                        int increment = prey.getFoodValue() + this.getFoodLevel();
                        this.setFoodLevel(increment);
                        int newWaterLevel = getWaterLevel() + 5;
                        this.setWaterLevel(newWaterLevel);
                        return where;  
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Make the animal more hungry. This could result in the animal's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * @return A list of the free adjacent locations for new young to be born into
     */
    protected List<Location> giveBirth(List<Actor> newAnimals)
    {
        int births = breed();
        if (births <= 0) {
            return null;
        }

        List<Location> free = getBirthLocations();
        if (free == null) {
            return null;
        }

        Field field = getField();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal young = createOffspring(field, loc);
            if (this.isInfected()) {
                young.setInfected();
            }
            newAnimals.add(young);
        }
        return null;
    }

    /**
     * Find free adjacent locations for births if a compatible mate is nearby.
     * @return The free locations available for offspring, or null if breeding is not possible.
     */
    protected List<Location> getBirthLocations()
    {
        if (hasCompatibleMateAdjacent()) {
            return getField().getFreeAdjacentLocations(getLocation());
        }
        return null;
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
     * @return True if this animal is a female, capable of breeding
     */
    protected void setNocturnal()
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
    protected void setSleepStatus(){
        sleeping = ! sleeping;
    }
    
    /**
     * @return Returns list of prey for each animal
     */
    abstract public List<String> getPrey();

    /**
     * @return The probability that this animal successfully hunts when it is hungry.
     */
    protected double getHuntProbability()
    {
        return 1.0;
    }

    /**
     * @return The age at which this animal can breed.
     */
    protected abstract int getBreedingAge();

    /**
     * @return The maximum age this animal can reach.
     */
    protected abstract int getMaxAge();

    /**
     * @return The chance this animal successfully breeds when it can breed.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum litter size for this animal.
     */
    protected abstract int getMaxLitterSize();

    /**
     * Create a new offspring of the current species.
     * @param field The field the offspring should occupy.
     * @param location The offspring's location.
     * @return A new instance of the same species.
     */
    protected abstract Animal createOffspring(Field field, Location location);

    /**
     * Determine whether this animal can currently breed.
     */
    private boolean canBreed()
    {
        return isFemale() && getAge() >= getBreedingAge();
    }

    /**
     * Generate the number of offspring produced by this animal.
     */
    private int breed()
    {
        if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            return rand.nextInt(getMaxLitterSize()) + 1;
        }
        return 0;
    }

    /**
     * Determine whether there is a compatible mate adjacent to this animal.
     */
    private boolean hasCompatibleMateAdjacent()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal != null && animal.getClass().equals(this.getClass())) {
                Animal mate = (Animal) animal;
                if (isFemale() != mate.isFemale()) {
                    return true;
                }
            }
        }
        return false;
    }
}
