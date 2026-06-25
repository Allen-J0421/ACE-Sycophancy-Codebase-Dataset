package models.animal.behaviour.breeding;

import field.Field;
import field.Location;
import models.animal.Animal;

/**
 * Defines how the animal should breed.
 *
 */
public abstract class BreedingBehaviour {
    protected Field field;
    protected Animal animal;
    
    /**
     * Constructor for BreedingBehaviour
     * @param animal The animal to be bred.
     */
    public BreedingBehaviour(Animal animal) {
        field = Field.getInstance();
        this.animal = animal;
    }

    /**
     * Enact the breeding behaviour.
     * @param location The location of the animal to be bred.
     * @return A free location on an adjacent square. Where to create the offspring.
     */
    public abstract Location act(Location location); 
}
