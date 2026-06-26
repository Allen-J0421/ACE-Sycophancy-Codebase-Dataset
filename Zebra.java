package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A simple model of a Zebra.
 * Zebras age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Zebra extends Prey
{
    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Zebra(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(randomAge, field, location, isInfected, isImmune, SimulationConfig.DEFAULT);
    }

    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     * @param config Shared simulation configuration.
     */
    public Zebra(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        super(field, location, isInfected, isImmune, config);
        
        breedingAge = config.zebra.breedingAge;
        maxAge = config.zebra.maxAge;
        breedingProbability = config.zebra.breedingProbability;
        maxLitterSize = config.zebra.maxLitterSize;
        maxFoodLevel = config.zebra.maxFoodLevel;
        foodValue = config.zebra.foodValue;
        movementProbability = config.zebra.movementProbability;
        
        if (randomAge) 
        {
            age = rand.nextInt(config.zebra.maxAge);
            foodLevel = rand.nextInt(config.zebra.maxFoodLevel);
        }
        else
        {
            age = 0;
            foodLevel = (int) (config.zebra.newbornFoodLevelFraction * config.zebra.maxFoodLevel);
        }
    }

    @Override
    protected Animal createOffspring(Location location, boolean inheritedInfection, boolean inheritedImmunity)
    {
        OffspringHealthState healthState = inheritHealthState(inheritedInfection, inheritedImmunity);
        return new Zebra(false, field, location, healthState.isInfected(), healthState.isImmune());
    }
}
