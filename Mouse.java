import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, move, eat seed, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mouses (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 125;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.25;
    // The likelihood of a mouse infect by disease.
    private static final double INFECT_PROBABILITY = 0.01;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single mouse. In effect, this is the
    // number of steps a mouse can go.
    private static final int DEFAULT_FOOD_LEVEL = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single mouse. 
    private static final int FOOD_VALUE = 5;
    
    // Individual characteristics (instance fields).    
    // The mouse's age.
    private int age;
    // The mouse's food level, which is increased by eating seed.
    private int foodLevel;
    // The mouse infected by deisease.
    // 0 - not infect, 1-3 degree of infection.
    private int infect;
    // chance of a mice to recover or deteriorate from disease.
    private static final double recover_probability = 0.3 ;
    private static final double deteriorate_probability = 0.15 ;
    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        }
        else {
            age = 0;
            foodLevel = DEFAULT_FOOD_LEVEL;
        }
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newMice A list to return newly born mouse.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Animal> newMice, int step, String weather)
    {
        incrementAge();
        updateBurnStatus(weather);
        CheckInfectLevel();
        if(isAlive()) {
            // Infected mouse will spread the disease to other mouse.
            if (getInfected() != 0){
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
                Iterator<Location> it = adjacent.iterator();
                while(it.hasNext()) {
                    Location where = it.next();
                    Object animal = field.getObjectAt(where);
                    if(animal instanceof Mouse) {
                        Mouse mouse = (Mouse) animal;
                        if(mouse.getInfected() == 0) { 
                            mouse.infect();
                            //break;
                        }
                    }
                }
                diseaseRecover();
            }
            else {
                giveBirth(newMice);    
                infect();
            }

            // Move towards a source of food if found.
            Location newLocation = findFood();

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
    }

    /**
     * Increase the age.
     * This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMice A list to return newly born mouse.
     */
    private void giveBirth(List<Animal> newMice)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Mouse young = new Mouse(false, field, loc);
            newMice.add(young);
        }
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first seed (belong to grass) will be located.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    foodLevel+=2;
                }
                return where;                
            }
        }
        return null;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A mouse can breed if it has reached the breeding age.
     * @return true if the mouse can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * @return true if the mouse is infected by disease, false otherwise.
     */
    private int getInfected()
    {
        return infect;
    }

    /**
     * A mouse can be infected by disease.
     */
    private void infect()
    {
        if(rand.nextDouble() <= INFECT_PROBABILITY) {
            infect = 1;
        }        
    }
    /**
     * mouse will die from disease when the infect indicator reach 3.
     */
    private void CheckInfectLevel()
    {
        if (infect==3){
                setDead();
            }
    }

    /**
     * A mouse can recover or deteriorate from disease
     */
    private void diseaseRecover()
    {
        if(rand.nextDouble() <= deteriorate_probability) {
            infect ++ ;
        }  
        else if (rand.nextDouble() <= recover_probability) {
            infect -- ;
        }  
    }

    /**
     * @return mouse's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
