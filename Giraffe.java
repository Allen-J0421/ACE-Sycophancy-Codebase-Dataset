package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A simple model of a Giraffe.
 * Giraffes age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Giraffe extends Prey
{
    /**
     * Create a new Giraffe. A Giraffe may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Giraffe will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Giraffe(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(randomAge, field, location, isInfected, isImmune, SimulationConfig.DEFAULT);
    }

    /**
     * Create a new Giraffe. A Giraffe may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Giraffe will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     * @param config Shared simulation configuration.
     */
    public Giraffe(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        super(field, location, isInfected, isImmune, config);
        
        breedingAge = config.giraffe.breedingAge;
        maxAge = config.giraffe.maxAge;
        breedingProbability = config.giraffe.breedingProbability;
        maxLitterSize = config.giraffe.maxLitterSize;
        maxFoodLevel = config.giraffe.maxFoodLevel;
        foodValue = config.giraffe.foodValue;
        movementProbability = config.giraffe.movementProbability;
        
        if (randomAge) 
        {
            age = rand.nextInt(config.giraffe.maxAge);
            foodLevel = rand.nextInt(config.giraffe.maxFoodLevel);
        }
        else
        {
            age = 0;
            foodLevel = (int) (config.giraffe.newbornFoodLevelFraction * config.giraffe.maxFoodLevel);
        }
    }

    @Override
    protected Animal createOffspring(Location location, boolean inheritedInfection, boolean inheritedImmunity)
    {
        OffspringHealthState healthState = inheritHealthState(inheritedInfection, inheritedImmunity);
        return new Giraffe(false, field, location, healthState.isInfected(), healthState.isImmune());
    }
}
