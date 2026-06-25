package models.plant.behaviour.breeding;

import java.util.List;
import java.util.Random;

import field.Actor;
import field.Field;
import field.Location;

/**
 * Logic for breeding between plants.
 *
 */
public abstract class BreedingBehaviour {
    protected Field field;
    protected Random rand;

    public BreedingBehaviour() {
        field = Field.getInstance();
        rand = new Random();
    }

    /**
     * Compute where a new plant should be born.
     * @param newActors where to add new plants
     * @param breeding_probability the probability of breeding
     * @return the location where to create the new plant
     */
    public abstract Location act(List<Actor> newActors, double breeding_probability);
}
