package models.animal.implementations;

import java.util.Arrays;
import java.util.List;

import field.Actor;
import field.Entity;
import field.Location;
import models.animal.Animal;
import models.animal.behaviour.abilities.BlankAbility;
import models.animal.behaviour.abilities.FireSurroundings;
import models.animal.behaviour.breeding.BreedingBehaviour;
import models.animal.behaviour.breeding.DefaultBreeding;
import models.animal.behaviour.movement.RandomMovement;

/**
 * Dragon data model.
 *
 */
public class Dragon extends Animal {
    private BreedingBehaviour breedingBehaviour;

    /**
     * Constructor for Dragon
     * @param location Dragon's initial location
     */
    public Dragon(Location location) {
        super("dragon", location, new int[] {255, 0, 0});

        // Constants
        STARTING_ENERGY = 500;
        MAX_ENERGY = 1000;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        ENERGY_EFFICIENCY = 0;
        
        // State
        energy = STARTING_ENERGY;
        movementBehaviour = new RandomMovement(this);
        breedingBehaviour = new DefaultBreeding(this);
        aliveAbility = new BlankAbility();
        aliveAbility = new FireSurroundings(aliveAbility, this, 9);
        onDeathAbility = new BlankAbility(); 
        eats = Arrays.asList("wolf");
    }

    /**
     * Breeding behaviour
     */
    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        if (entity instanceof Dragon) {
            Dragon dragon = (Dragon)entity;
            if (!this.sex.equals(dragon.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Dragon a = new Dragon(location);
                    field.setObjectAt(location, a);
                    newActors.add(a);
                    energy -= 200;
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
