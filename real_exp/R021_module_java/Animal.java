import java.util.List;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 *
 * 
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Species
{
    // Characteristics shared by all speciess (class variables).

    // The likelihood of an animal infected by self.
    protected static final double DISEASE_RATE = 0.005;
    // The likelihood of an animal infected by others.
    protected static final double INFECT_RATE = 0.05;
    
    // Individual characteristics (instance fields).

    // The animal's food level, which is increased by eating food.
    protected int foodLevel;
    // The animal's food class
    protected Class[] foodTypes;
    // Whether the animal is infected by disease or not.
    protected boolean infected;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge If true, the animal will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param maxAge The age to which an animal can live.
     * @param foodRequire The number of steps an animal can go before it has to eat again.
     */
    public Animal(boolean randomAge, Field field, Location location, int maxAge, int foodRequire)
    {
        super(randomAge, field, location, maxAge);
        if(randomAge) {
            foodLevel = rand.nextInt(foodRequire);
        }
        else {
            foodLevel = foodRequire;
        }
        speed = 1; // default animal speed
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
     * Look for food adjacent to the current location.
     * Only the first live insect is eaten.
     * @param foodRequirementTime The number of steps an animal can go before it has to eat again.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(int foodRequirementTime)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            for(Class cls : foodTypes){
                if(animal != null && animal.getClass() == cls){
                    Species species = (Species)animal;
                    if(species.isAlive()){
                        species.setDead();
                        foodLevel = foodRequirementTime;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Animal try to find food and move to a new location
     * 
     * @param foodRequirementTime The number of steps an animal can go before it has to eat again.
     */
    protected void findFoodAndMove(int foodRequirementTime){
        // Move towards a source of food if found.
        Location newLocation = findFood(foodRequirementTime);
        if(newLocation == null) { 
            // No food found - try to move to a free or plant location.
            newLocation = getField().freeOrPlantAdjacentLocation(getLocation(), speed);
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

    /**
     * @return Whether the animal is infected by disease or not.
     */
    public boolean isInfected(){
        return infected;
    }

    /**
     * In every step, check if the animal infected by desease.
     */
    protected void diseaseInfect(){
        if(infected){
            return;
        }
        // Infected by self
        if(rand.nextDouble() < DISEASE_RATE){
            infected = true;
            maxAge = maxAge / 2;
            return;
        }
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for(Location location : adjacent){
            Object species = field.getObjectAt(location);
            if(species instanceof Animal){
                Animal animal = (Animal)species;
                // Infected by others
                if(animal.infected && rand.nextDouble() < INFECT_RATE){
                    infected = true;
                    maxAge = maxAge / 2;
                    return;
                }
            }
        }
    }
    
    /**
     * Get locations where can birth
     */
    protected List<Location> getBirthLocations(Location location){
        Field field = getField();
        List<Location> free = field.getFreeOrPlantAdjacentLocations(getLocation(), speed);
        return free;
    }
}
