import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of organisms.
 *
 * @version 15/03/2022
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    protected boolean alive;
    // The current weather condition.
    protected String weather;
    // The organism's field.
    protected Field field;
    // The current time translated to minutes.
    protected int mins;
    // The number of minutes with an offset.
    protected int clock;
    // The organism's age.
    protected int age;
    // The organism's position in the field.
    protected Location location;
    // A shared random number generator to control eating, breeding and behaviour.
    public static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new organism at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Makes this organism act - makes it eat, breed, move or behave.     
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void act(List<Organism> newOrganisms);
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Increase the age. This could result in the organism's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this organism is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOrganisms A list to return newly born organisms.
     */
    protected void giveBirth(List<Organism> newOrganisms)
    {
        // New organisms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Organism young = getNewOrganism(false, field, loc);
            newOrganisms.add(young);
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        
        // Check if the current organism is a monkey.
        if (this instanceof Monkey){
            int mins = getTime();
        
            // Monkeys are slightly less likely to breed between 11:00-11:59.
            if (mins >= 660 && mins < 720) {
                double newBreedingProbability = getBreedingProbability() * 0.99999;
                setBreedingProbability(newBreedingProbability);
            }
            
            // Monkeys are slightly more likely to breed between 21:00-04:59.
            if (!(mins > 299 && mins < 1260)) {
                double newBreedingProbability = getBreedingProbability() * 1.00001;
                setBreedingProbability(newBreedingProbability);
            }
        }
        
        // Check if weather is stormy or sunny.
        // Slightly increase or decrease the breeding probabilities if it's stormy or sunny.
        if (this instanceof Animal) {
            if (getWeather() == "Stormy") {
                double newBreedingProbability = getBreedingProbability() * 0.99999;
                setBreedingProbability(newBreedingProbability);
            }
        }
        else if (this instanceof Plant) {
            if (getWeather() == "Sunny") {
                double newBreedingProbability = getBreedingProbability() * 1.00001;
                setBreedingProbability(newBreedingProbability);
            }
        }
        
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
    
    /**
     * An organism can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }
    
    /**
     * Returns a new Organism instance. 
     * @param randomAge If true, the organism will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Organism instance. 
     */
    abstract protected Organism getNewOrganism(boolean randomAge, Field field, Location loc);
    
    /**
     * Returns an organism's maximum possible age. 
     * @return an organism's maximum possible age.
     */
    abstract protected int getMaxAge();
    
    /**
     * Returns an organism's minimum breeding age. 
     * @return an organism's minimum breeding age. 
     */
    abstract protected int getBreedingAge();
    
    /**
     * Returns an organism's probability of breeding successfully.
     * @return an organism's probability of breeding successfully. 
     */
    abstract protected double getBreedingProbability();
    
    /**
     * Gives an organism a new breeding proability.
     * @param newBreedingProbability The organism's probability of breeding.
     */
    abstract protected void setBreedingProbability(double newBreedingProbability);
    
    /**
     * Returns an organism's maximum possible litter size.
     * @return an organism's maximum possible litter size.
     */
    abstract protected int getMaxLitterSize();
    
    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Returns the current time in minutes.
     * @return the current time in minutes.
     */
    protected int getTime() {
        return mins;
    }
    
    /**
     * Makes the current time, in minutes,
     * accessible to the organism to control their actions.
     * @param minutes The current time in minutes.
     */
    public void setTime(int minutes) {
        mins = minutes;
    }
    
    /**
     * Returns the current time in minutes, with an offset.
     * @return the current time in minutes, with an offset.
     */
    protected int getClock() {
        return clock;
    }
    
    /**
     * Makes the current time, in minutes and with an offset,
     * accessible to the organisms to control their actions.
     * @param minutes The current time in minutes, with an offset.
     */
    public void setClock(int minutes) {
        clock = minutes;
    }
    
    /**
     * Makes the current weather condition
     * accessible to the organisms to control their actions.
     * @param currentWeather The current weather condition.
     */
    public void setWeather(String currentWeather) {
        weather = currentWeather;
    }
    
    /**
     * Returns the current weather condition.
     * @return the current weather condition.
     */
    protected String getWeather() {
        return weather;
    }
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
}
