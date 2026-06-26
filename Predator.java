import java.util.List;
import java.util.Iterator;
/**
 * A class representing the shared characteristics between predators
 *
 * @version 25/02/2022
 */
public abstract class Predator extends Animal
{
    // The likelihood of the predator catching its prey
    protected double preyCatchingProbability;

    /**
     * Create a new predator at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param infected : intial state if the prey is infected or not
     * @param immmune : intial state if the prey is immune or not
     */
    protected Predator(Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune);
        movementProbability = 0.8;
    }

    /**
     * Newborn predators start with a small, variable food reserve.
     */
    protected int initialNewbornFoodLevel()
    {
        double percentageOfMaxFoodLevel = rand.nextDouble() / 5.5;
        return (int) (percentageOfMaxFoodLevel * maxFoodLevel);
    }
    
    /**
     * @Override
     * 
     * This is what the predators does most of the time - it may move around, it eats prey, 
     * it may breed, it may get infected with a disease, it may become immune to that disease, 
     * it may die of the disease, it may die of hunger, and it may die of old age.
     * 
     * @param newPredators A list to return newly born prey.
     */
    public void act(List<LivingOrganism> newPredators)
    {
        // Predators have a lower chance of acting when its night
        if(Time.isNight()) 
        {
            if (rand.nextDouble() > 0.25)
            {
                return;
            }
        }
        
        // Predators have a lower chance of acting when its foggy
        if (Weather.getWeather() == Weather.WeatherType.Foggy) 
        {
            if (rand.nextDouble() > 0.50)
            {
                return;
            }
        }

        super.act(newPredators);
    }
    
    /**
     * @Override
     * 
     * Look for prey adjacent to the current location.
     * Eats prey till full.
     * Predators may catch the prey's disease if eaten.
     * 
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext()) 
        {
            Location where = it.next();
            Object animal = field.getObjectAt(where, Animal.class);
            
            if(animal instanceof Prey) 
            {
                Prey prey = (Prey) animal;
                if(prey.isAlive() && foodLevel < maxFoodLevel && rand.nextDouble() < preyCatchingProbability) 
                { 
                    // Predator is hungry and prey is availiable so eat
                    // the prey.
                    int preyFoodValue = prey.beEaten();
                    foodLevel += preyFoodValue;
                    
                    // if the prey being eaten is infected then the predator most likely
                    // gets infected
                    if (prey.getIsInfected() && !getIsImmune() && rand.nextDouble() <= diseaseSpreadProbability)
                    {
                        this.infected = true; 
                    }
                    
                    return where;
                }
            }
        }
        
        return null;
    }
}
