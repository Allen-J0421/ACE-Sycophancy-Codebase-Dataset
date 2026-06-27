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
    private final AnimalStatus status;
    
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
        status = new AnimalStatus(randomAge);
        Random rand = new Random();
        setWaterLevel(rand.nextInt(10) + 5);
        if(!randomAge) {
            setAge(0);
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
        if(isAlive() && !status.isSleeping()) {
            // Move towards a source of food if found
            giveBirth(newOrganisms);  
            Location newLocation = null;
            if(getWaterLevel() < 3) {
                newLocation = findWater();
            }
            if (newLocation == null && getFoodLevel() < 8) {
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
        else if (status.isSleeping()) {
            // code for what to do when sleeping
            status.wakeUp();
        }
    }
    
    /**
     * Move towards another animal of the same species to find a mate to breed with
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
            // Checks nearby location (adjacent to adjacent locations)
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
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(ArrayList<String> preyList)
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
        status.decrementFoodLevel();
        if(status.getFoodLevel() <= 0) {
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
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            // can only breed if next to an animal of opposite sex
            if(animal != null && this.getClass().equals(animal.getClass())) {
                Animal mate = (Animal) animal;
                if (isFemale() != mate.isFemale()) {
                    return free;
                }
                else {
                    return null;
                }
            }
        }    
        return null;
    }
    
    /**
     * @param foodValue The level of food an animal has eaten
     */
    protected void setFoodLevel(int foodValue)
    {
        status.setFoodLevel(foodValue);
    }
    
    /** 
     * @return The level of food an animal has eaten
     */
    protected int getFoodLevel()
    {
        return status.getFoodLevel();
    }
    
    /** 
     * @return True if this animal is a female, capable of breeding
     */
    public boolean isFemale()
    {
        return status.isFemale();
    }
    
    /** 
     * @return True if this animal is a female, capable of breeding
     */
    public void setNocturnal()
    {
        status.setNocturnal();
    }
    
    /** 
     * @return True if this animal is nocturnal, false if not
     */
    public boolean isNocturnal()
    {
        return status.isNocturnal();
    }
    
    /**
     * Sets the sleeping boolean variable to the opposite state it currently has.
     */
    public void setSleepStatus(){
        status.toggleSleeping();
    }
    
    /**
     * @return Returns list of prey for each animal
     */
    abstract public ArrayList<String> getPrey();
}
