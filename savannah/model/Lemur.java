package savannah.model;

import savannah.config.SimulationConfig;

/**
 * A simple model of a Lemur.
 * Lemurs age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Lemur extends Prey
{
    /**
     * Create a new Lemur. A Lemur may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the lemur will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(randomAge, field, location, isInfected, isImmune, SimulationConfig.DEFAULT);
    }

    /**
     * Create a new Lemur. A Lemur may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the lemur will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     * @param config Shared simulation configuration.
     */
    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        super(field, location, randomAge, isInfected, isImmune, SpeciesType.LEMUR, config);
    }
}
