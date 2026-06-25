package models.animal.implementations;

import java.util.Arrays;
import java.util.List;

import field.Actor;
import field.Entity;
import field.Location;
import models.animal.Animal;
import models.animal.behaviour.abilities.BlankAbility;
import models.animal.behaviour.abilities.FireSurroundings;
import models.animal.behaviour.abilities.Teleport;
import models.animal.behaviour.breeding.DefaultBreeding;
import models.animal.behaviour.movement.RandomMovement;

/**
 * Phoenix data model.
 *
 */
public class Phoenix extends Animal {
    public Phoenix(Location location) {
        super("phoenix", location, new int[] {255,69,0});

        // Magic
        STARTING_ENERGY = 200;
        MAX_ENERGY = 300;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        ENERGY_EFFICIENCY = 0;
        
        // State
        energy = STARTING_ENERGY;
        movementBehaviour = new RandomMovement(this);
        breedingBehaviour = new DefaultBreeding(this);
        aliveAbility = new BlankAbility();
        onDeathAbility = new BlankAbility();
        onDeathAbility = new Teleport(onDeathAbility, this);
        onDeathAbility = new FireSurroundings(onDeathAbility, this, 50); 
        eats = Arrays.asList("elk");
    }

    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        if (entity instanceof Phoenix) {
            Phoenix e = (Phoenix)entity;
            if (!this.sex.equals(e.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Phoenix a = new Phoenix(location);
                    field.setObjectAt(location, a);
                    newActors.add(a);
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
