package savannah.model;

import java.util.Iterator;
import java.util.List;

import savannah.engine.SimulationContext;

/**
 * A class representing the shared characteristics between Prey
 *
 * @version 26/02/2022
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey using the shared simulation context.
     * 
     * @param context Shared simulation context.
     * @param location The location within the field.
     * @param randomAge If true, the prey will have random age and hunger level.
     * @param infected Initial state if the prey is infected or not.
     * @param immune Initial state if the prey is immune or not.
     * @param speciesType The species being created.
     */
    protected Prey(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune, SpeciesType speciesType)
    {
        super(context, location, randomAge, infected, immune, speciesType);
    }

    /**
     * @Override
     * 
     * This is what the prey does most of the time - it may move around, it eats plants, 
     * it may breed, it may get infected with a disease, it may become immune to that disease, 
     * it may die of the disease, it may die of hunger, and it may die of old age.
     * 
     * @param newPrey A list to return newly born prey.
     */
    public void act(List<LivingOrganism> newPrey)
    {
        // Prey sleep at night
        if(Time.isNight()) 
        {
            return;
        }
        
        super.act(newPrey);
    }

    /**
     * @Override
     * 
     * Look for plants adjacent to the current location.
     * Eats plants till full.
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
            
            // Get the plant and animal currently at this new location.
            Plant plant = (Plant) field.getObjectAt(where, Plant.class);
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);
            
            if(animal == null)
            {
                if(plant != null)
                {
                    if(plant.isAlive() && foodLevel < maxFoodLevel)
                    { 
                        // Prey is hungry and plant is availiable so eat
                        // the plant.
                        int plantFoodValue = plant.beEaten();
                        foodLevel += plantFoodValue;
                        
                        return where;
                    }
                }
            }
        }
        
        return null;
    }
}
