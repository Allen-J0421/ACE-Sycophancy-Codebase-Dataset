import java.util.List;
import java.util.Iterator;

/**
 * A class representing the shared characteristics between Prey
 *
 * @version 26/02/2022
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey at location in field, initialising its species-specific
     * statistics. Shared by every prey species so the per-species constructors
     * carry only their constants, not duplicated initialisation logic.
     *
     * @param randomAge If true, the prey starts with a random age and food level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param infected Intial state if the prey is infected or not
     * @param immune Intial state if the prey is immune or not
     * @param breedingAge The age at which the prey can start to breed.
     * @param maxAge The age to which the prey can live.
     * @param breedingProbability The likelihood of the prey breeding.
     * @param maxLitterSize The maximum number of births.
     * @param maxFoodLevel The food level the prey stops being hungry at.
     * @param foodValue How much a predator's food level increases when eating it.
     * @param newbornFoodFraction Fraction of maxFoodLevel a new-born starts with.
     */
    protected Prey(boolean randomAge, Field field, Location location, boolean infected, boolean immune,
                   int breedingAge, int maxAge, double breedingProbability, int maxLitterSize,
                   int maxFoodLevel, int foodValue, double newbornFoodFraction)
    {
        super(field, location, infected, immune);
        movementProbability = 0.75;

        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.maxFoodLevel = maxFoodLevel;
        this.foodValue = foodValue;

        if (randomAge)
        {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(maxFoodLevel);
        }
        else
        {
            age = 0;
            foodLevel = (int) (newbornFoodFraction * maxFoodLevel);
        }
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
