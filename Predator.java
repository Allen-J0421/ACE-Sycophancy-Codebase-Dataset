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

    // Chance a predator still acts during the night (it mostly rests).
    private static final double NIGHT_ACTIVITY_CHANCE = 0.25;
    // Chance a predator still acts when the weather is foggy.
    private static final double FOG_ACTIVITY_CHANCE = 0.50;

    /**
     * Create a new predator at location in field, initialising its species-specific
     * statistics. Shared by every predator species so the per-species constructors
     * carry only their constants, not duplicated initialisation logic.
     *
     * @param randomAge If true, the predator starts with a random age and food level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Intial state if the predator is infected or not.
     * @param isImmune Intial state if the predator is immune or not.
     * @param preyCatchingProbability The likelihood of the predator catching its prey.
     * @param breedingAge The age at which the predator can start to breed.
     * @param maxAge The age to which the predator can live.
     * @param breedingProbability The likelihood of the predator breeding.
     * @param maxLitterSize The maximum number of births.
     * @param maxFoodLevel The food level the predator stops being hungry at.
     * @param foodValue How much another predator's food level increases when eating it.
     */
    protected Predator(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune,
                       double preyCatchingProbability, int breedingAge, int maxAge, double breedingProbability,
                       int maxLitterSize, int maxFoodLevel, int foodValue)
    {
        super(field, location, isInfected, isImmune);
        movementProbability = 0.8;

        this.preyCatchingProbability = preyCatchingProbability;
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
            // Chooses a random percentage between 0-18% of the max food level
            // to start at - prevents a self-sustaining loop and adds variability.
            double percentageOfMaxFoodLevel = rand.nextDouble() / 5.5;
            foodLevel = (int) (percentageOfMaxFoodLevel * maxFoodLevel);
        }
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
            if (rand.nextDouble() > NIGHT_ACTIVITY_CHANCE)
            {
                return;
            }
        }

        // Predators have a lower chance of acting when its foggy
        if (Weather.getWeather() == Weather.WeatherType.Foggy)
        {
            if (rand.nextDouble() > FOG_ACTIVITY_CHANCE)
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