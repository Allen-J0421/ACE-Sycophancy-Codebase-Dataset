package simulation;

import java.util.List;

import configuration.Configuration;

/**
 * A simple model of an acacia plant.
 * Acacia plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Acacia extends Plant
{
    private static final Configuration.PlantTuning TUNING = Configuration.defaults().species().acacia();

    /**
     * Create an acacia. An acacia is created as a newborn with age 0.
     *
     * @param context The simulation context currently occupied.
     * @param location The location within the field.
     */
    public Acacia(SimulationContext context, Location location)
    {
        super(context, location);
    }

    /**
     * This is what the acacia does most of the time:
     * it grows and reproduces offspring into adjacent locations
     * @param newAcacias A list to return newly produced acacia.
     */
    public void act() {
        if(isAlive()) {
            growPlants(TUNING.getReproducingProbability(),
                    TUNING.getMaxOffspringSize());
        }
    }

    /**
     * Create a new acacia offspring.
     */
    protected Plant createOffspring(SimulationContext context, Location location) {
        return new Acacia(context, location);
    }
}
