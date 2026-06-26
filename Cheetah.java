package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A simple model of a cheetah.
 * cheetahs age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Cheetah extends Predator
{
    /**
     * Create a cheetah. A cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Cheetah(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(randomAge, field, location, isInfected, isImmune, SimulationConfig.DEFAULT);
    }

    /**
     * Create a cheetah. A cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     * @param config Shared simulation configuration.
     */
    public Cheetah(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        super(field, location, isInfected, isImmune, config);
        
        preyCatchingProbability = config.cheetah.preyCatchingProbability;
        breedingAge = config.cheetah.breedingAge;
        maxAge = config.cheetah.maxAge;
        breedingProbability = config.cheetah.breedingProbability;
        maxLitterSize = config.cheetah.maxLitterSize;
        maxFoodLevel = config.cheetah.maxFoodLevel;
        foodValue = config.cheetah.foodValue;
        movementProbability = config.cheetah.movementProbability;
        
        if (randomAge) 
        {
            age = rand.nextInt(config.cheetah.maxAge);
            foodLevel = rand.nextInt(config.cheetah.maxFoodLevel);
        }
        else
        {
            age = 0;
            
            // Chooses a random percentage between 0-18% of the max
            // food level to start at - prevents a self-sustaining
            // loop and add variability.
            double percentageOfMaxFoodLevel = rand.nextDouble() * config.cheetah.randomNewbornFoodLevelMaxFraction;
            foodLevel = (int) (percentageOfMaxFoodLevel * config.cheetah.maxFoodLevel);
        }
    }

    @Override
    protected Animal createOffspring(Location location, boolean inheritedInfection, boolean inheritedImmunity)
    {
        OffspringHealthState healthState = inheritHealthState(inheritedInfection, inheritedImmunity);
        return new Cheetah(false, field, location, healthState.isInfected(), healthState.isImmune());
    }
}
