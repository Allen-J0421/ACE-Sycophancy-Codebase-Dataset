package models.animal.implementations;

import java.util.Arrays;
import java.util.List;

import field.Actor;
import field.Entity;
import field.Location;
import models.animal.Animal;
import models.animal.behaviour.abilities.BlankAbility;
import models.animal.behaviour.breeding.DefaultBreeding;
import models.animal.behaviour.movement.RandomMovement;

/**
 * Wolf data model.
 *
 */
public class Wolf extends Animal {
    public Wolf(Location location) {
        super("wolf", location, new int[] {0,0,0});

        // Magic
        STARTING_ENERGY = 200;
        MAX_ENERGY = 200;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        ENERGY_EFFICIENCY = 0;
        BREEDING_PROBABILITY = 0.1;
        MAX_CHILDREN = 1;
        
        // State
        energy = STARTING_ENERGY;
        movementBehaviour = new RandomMovement(this);
        breedingBehaviour = new DefaultBreeding(this);
        aliveAbility = new BlankAbility();
        onDeathAbility = new BlankAbility(); 
        eats = Arrays.asList("elk", "slime");
    }

    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        if (entity instanceof Wolf) {
            Wolf e = (Wolf)entity;
            if (!this.sex.equals(e.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Wolf a = new Wolf(location);
                    field.setObjectAt(location, a);
                    newActors.add(a);
                    energy -= 100;
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
