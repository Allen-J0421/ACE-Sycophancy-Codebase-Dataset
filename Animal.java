import java.util.List;
import java.util.Random;
import java.util.Iterator;

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
    // This animal's species characteristics (instance-based configuration).
    private final SpeciesConfig config;
    // The seeded random number generator, obtained per instance from the
    // shared Randomizer so the whole simulation stays reproducible.
    private final Random rand;

    /**
     * Create a new animal at location in field.
     *
     * @param config The species characteristics for this animal.
     * @param randomAge If true, the animal will have a random age
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(SpeciesConfig config, boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.config = config;
        this.rand = Randomizer.getRandom();
        nocturnal = config.nocturnal();
        female = rand.nextBoolean();
        setWaterLevel(rand.nextInt(10) + 5);
        sleeping = false;
        if(randomAge) {
            setAge(rand.nextInt(config.maxAge()));
            setFoodLevel(config.startingFoodLevel());
        }
        else {
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
     * New births are placed into free adjacent locations. This is a template
     * method: shared for every species, with the concrete young produced by
     * {@link #createOffspring}.
     * @param newAnimals A list to return newly born animals.
     * @return Always null (births are added directly to newAnimals).
     */
    protected List<Location> giveBirth(List<Actor> newAnimals)
    {
        Field field = getField();
        int births = breed();
        List<Location> free = availableBirthLocations();
        if (free != null) {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Animal young = createOffspring(field, loc);
                // If this animal is infected with a disease, pass it to the young.
                if (this.isInfected()) {
                    young.setInfected();
                }
                newAnimals.add(young);
            }
        }
        return null;
    }

    /**
     * Find the free adjacent locations a new young could be born into, but
     * only if this animal is next to a mate of the opposite sex.
     * @return The free adjacent locations, or null if there is no eligible mate.
     */
    private List<Location> availableBirthLocations()
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
     * Increase the age. This could result in the animal's death once it
     * passes its species' maximum age.
     */
    public void incrementAge()
    {
        super.incrementAge();
        if(getAge() > config.maxAge()) {
            setDead();
        }
    }

    /**
     * Generate a number representing the number of births, if this animal
     * can breed at this step.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= config.breedingProbability()) {
            births = rand.nextInt(config.maxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it is female and has reached its breeding age.
     * @return true if this animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return (isFemale() && getAge() >= config.breedingAge());
    }

    /**
     * Roll against this species' hunt probability. Hunters that gate their
     * eating use this so the chance lives in configuration, not in code.
     * @return true if a hunt attempt succeeds this step.
     */
    protected boolean huntSucceeds()
    {
        return rand.nextDouble() < config.huntProbability();
    }

    /**
     * @return The food value gained by a predator that eats this animal.
     */
    public int getFoodValue()
    {
        return config.foodValue();
    }

    /**
     * @return The list of prey (class names) which this animal eats.
     */
    public List<String> getPrey()
    {
        return config.prey();
    }

    /**
     * Create a new-born of this species at the given location.
     * @param field The field the young is born into.
     * @param location The location the young is born at.
     * @return The newly created young.
     */
    protected abstract Animal createOffspring(Field field, Location location);
    
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
        sleeping = ! sleeping;
    }

    /**
     * Fog stops an animal behaving normally: instead of gaining water it
     * is sent to sleep.
     */
    public void onFog() {
        setSleepStatus();
    }

    /**
     * An animal can only act when the time of day matches its habits:
     * nocturnal animals act at night, others act during the day.
     * @param isNight Whether it is currently night time.
     * @return true if this animal may act this step.
     */
    public boolean canActNow(boolean isNight) {
        return isNight == isNocturnal();
    }
}
