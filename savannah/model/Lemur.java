package savannah.model;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

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
    public Lemur(SimulationContext context, boolean randomAge, Location location, boolean isInfected, boolean isImmune)
    {
        super(context, location, randomAge, isInfected, isImmune, SpeciesType.LEMUR);
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
    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        this(new SimulationContext(field, SimulationConfig.DEFAULT), randomAge, location, isInfected, isImmune);
    }

    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune, SimulationConfig config)
    {
        this(new SimulationContext(field, config), randomAge, location, isInfected, isImmune);
    }
}
