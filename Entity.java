import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An entity that acts in the simulation.
 *
 * @version 2022.03.02
 */
public interface Entity {

    /**
     * Method called for this entity at every step in the simulation.
     *
     * @param newEntities Newborn entities at this step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    void act(List<Organism> newOrganisms, Weather weather, TimeOfDay time);

}
