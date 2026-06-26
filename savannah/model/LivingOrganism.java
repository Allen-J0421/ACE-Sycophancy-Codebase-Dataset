package savannah.model;

import java.util.List;
import java.util.Random;

import savannah.engine.SimulationContext;

/**
 * A class representing the shared characteristics between Living Organisms.
 *
 * @version 26/02/2022
 */
public abstract class LivingOrganism
{
    // The species of this organism.
    protected final SpeciesType speciesType;
    // Whether the organism is alive or not.
    protected boolean alive;
    // The organism's position in the field.
    protected Location location;
    // The food value of itself - how much the hunter's food level increases when eating it.
    protected int foodValue;
    // Shared simulation state.
    protected final SimulationContext context;
    
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();
    
    /**
     * Creates a new organism using the shared simulation context.
     *
     * @param context Shared simulation context.
     * @param location The location within the field.
     * @param speciesType The species being created.
     */
    protected LivingOrganism(SimulationContext context, Location location, SpeciesType speciesType) 
    {
        this.context = context;
        alive = true;
        this.speciesType = speciesType;
        setLocation(location);
    }
    
    /**
     * Increments the organism's age.
     */
    protected abstract void incrementAge();
    
    /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newOrganisms A list to receive newly born organsims.
     */
    protected abstract void act(List<LivingOrganism> newOrganisms);
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Return the organism's species.
     *
     * @return The organism's species.
     */
    public SpeciesType getSpeciesType()
    {
        return speciesType;
    }
    
    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected abstract void setDead();
    
    /**
     * When called, the organism gets eaten and returns the
     * food value of which the organism eating it increases
     * 
     * @return The food value of the organism being eaten.
     */
    protected abstract int beEaten();
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the organism at the new location in the given field.
     * 
     * @param newLocation The organism's new location.
     */
    protected abstract void setLocation(Location newLocation);
    
    /**
     * Check whether or not this Organism is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newOrganisms A list to return newly born Organisms.
     */
    protected abstract void populate(List<LivingOrganism> newOrganisms);
    
    /**
     * Return the organism's field.
     * 
     * @return The organism's field.
     */
    protected Field getField()
    {
        return context.getField();
    }

    /**
     * Return the shared simulation configuration.
     *
     * @return The simulation configuration.
     */
    protected savannah.config.SimulationConfig getConfig()
    {
        return context.getConfig();
    }

    /**
     * Return the shared simulation context.
     *
     * @return The simulation context.
     */
    protected SimulationContext getContext()
    {
        return context;
    }
}
