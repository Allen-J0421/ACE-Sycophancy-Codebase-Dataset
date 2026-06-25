package models.plant.behaviour.breeding;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.Plant;

/**
 * Breed randomly in any direction. (WIP).
 *
 */
public class RandomBreeding extends BreedingBehaviour {
    public RandomBreeding(Plant plant) {
    }

    @Override
    public Location act(List<Actor> newActors, double breeding_probability) {
        return null;
        
    }
}
