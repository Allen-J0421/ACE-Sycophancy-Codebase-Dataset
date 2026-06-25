package models.animal.behaviour.movement;

import field.Field;
import models.animal.Animal;

/**
 * Determines how the animal moves. Animals could, for example have
 * pathfinding behaviour, move randomly, or even just multiple squares.
 *
 */
public abstract class MovementBehaviour {
    protected Animal animal;
    protected Field field;

    /**
     * Constructor for MovementBehaviour
     * @param animal the animal to be moved
     */
    public MovementBehaviour(Animal animal) {
        this.animal = animal;
        field = Field.getInstance();
    }

    /**
     * Move the animal to a new Block according to logic defined.
     */
    public abstract void act();
}
