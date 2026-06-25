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
 * Slime data model.
 *
 */
public class Slime extends Animal {
    public Slime(Location location) {
        super("slime", location, new int[] {128,0,128});

        // Magic
        STARTING_ENERGY = 50;
        MAX_ENERGY = 100;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        ENERGY_EFFICIENCY = 0;
        BREEDING_PROBABILITY = 0.2;
        
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
        if (entity instanceof Slime) {
            Slime slime = (Slime)entity;
            // !this.sex.equals(hydra.sex) && 
            if (!this.sex.equals(slime.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Slime a = new Slime(location);
                    field.setObjectAt(location, a);
                    newActors.add(a);
                    energy -= 50;
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
