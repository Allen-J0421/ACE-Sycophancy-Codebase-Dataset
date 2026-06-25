package models.plant.implementations;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.Plant;
import models.plant.behaviour.breeding.bfs.ZigZagBfsBreeding;

/**
 * Tall Grass data model.
 *
 */
public class TallGrass extends Plant {
    public TallGrass(Location location) {
        // https://www.lifewire.com/what-color-is-chartreuse-1077383
        super("tall_grass", location, new int[] {209, 226, 49});

        // Changing some default parameters
        BREEDING_PROBABILITY = 0.06;
        ENERGY_EFFICIENCY = 0.4;

        breedingBehaviour = new ZigZagBfsBreeding(location.getRow(), location.getCol());
    }

    /**
     * Breed according to breeding logic, enact its behaviours and grow.
     */
    @Override
    public void act(List<Actor> newActors) {
        //if (field.getDayState() == DayState.NIGHT) return;
        Location location = breedingBehaviour.act(newActors, BREEDING_PROBABILITY);
        if (location != null) {
            TallGrass offspring = new TallGrass(location);
            field.setObjectAt(location, offspring);
            newActors.add(offspring);
        }
        aliveBehaviour.act();
        grow();
    }
}
