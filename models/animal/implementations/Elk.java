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
 * Elk data model.
 *
 */
public class Elk extends Animal {
    public Elk(Location location) {
        super("elk", location, new int[] {165, 42, 42});
        
        // Constants
        STARTING_ENERGY = 50;
        MAX_ENERGY = 100;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        ENERGY_LOSS = 1;
        ENERGY_EFFICIENCY = 0;
        BREEDING_PROBABILITY = 0.3;
        MAX_CHILDREN = 2;
        
        // State
        energy = STARTING_ENERGY;
        movementBehaviour = new RandomMovement(this);
        breedingBehaviour = new DefaultBreeding(this);
        aliveAbility = new BlankAbility();
        onDeathAbility = new BlankAbility(); 
        eats = Arrays.asList("grass", "tall_grass");
    }

    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        if (entity instanceof Elk) {
            Elk elk = (Elk)entity;
            if (!this.sex.equals(elk.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Elk newElk = new Elk(location);
                    field.setObjectAt(location, newElk);
                    newActors.add(newElk);
                    energy -= 50;
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
