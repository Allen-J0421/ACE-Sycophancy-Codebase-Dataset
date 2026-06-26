package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A simple model of a lion.
 * lions age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Lion extends Predator
{
    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lion(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(randomAge, field, location, isInfected, isImmune, SimulationConfig.DEFAULT);
    }

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     * @param config Shared simulation configuration.
     */
    public Lion(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        super(field, location, isInfected, isImmune, config);
        
        preyCatchingProbability = config.lion.preyCatchingProbability;
        breedingAge = config.lion.breedingAge;
        maxAge = config.lion.maxAge;
        breedingProbability = config.lion.breedingProbability;
        maxLitterSize = config.lion.maxLitterSize;
        maxFoodLevel = config.lion.maxFoodLevel;
        foodValue = config.lion.foodValue;
        movementProbability = config.lion.movementProbability;
        
        if (randomAge) 
        {
            age = rand.nextInt(config.lion.maxAge);
            foodLevel = rand.nextInt(config.lion.maxFoodLevel);
        }
        else
        {
            age = 0;
            
            // Chooses a random percentage between 0-18% of the max
            // food level to start at - prevents a self-sustaining
            // loop and add variability.
            double percentageOfMaxFoodLevel = rand.nextDouble() * config.lion.randomNewbornFoodLevelMaxFraction;
            foodLevel = (int) (percentageOfMaxFoodLevel * config.lion.maxFoodLevel);
        }
    }

    @Override
    protected Animal createOffspring(Location location, boolean inheritedInfection, boolean inheritedImmunity)
    {
        OffspringHealthState healthState = inheritHealthState(inheritedInfection, inheritedImmunity);
        return new Lion(false, field, location, healthState.isInfected(), healthState.isImmune());
    }
}
