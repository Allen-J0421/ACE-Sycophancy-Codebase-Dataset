package simulation;

import java.util.List;

import configuration.Configuration;

/**
 * A simple model of a grass plant.
 * Grass plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Grass extends Plant
{
    private static final Configuration.PlantTuning TUNING = Configuration.defaults().species().grass();

    /**
     * Create a grass. A grass is created as a newborn age 0
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(SimulationContext context, Location location) {
        super(context, location);
    }

    /**
     * This is what the grass does most of the time:
     * it grows and reproduces offspring into adjacent locations
     * @param newGrass A list to return newly produced grasses.
     */
    public void act() {
        if(isAlive()) {
            growPlants(TUNING.getReproducingProbability(),
                    TUNING.getMaxOffspringSize());
        }
    }

    /**
     * Create a new grass offspring.
     */
    protected Plant createOffspring(SimulationContext context, Location location) {
        return new Grass(context, location);
    }
}
