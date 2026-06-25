package models.animal.implementations;

import java.util.Arrays;
import java.util.List;

import field.Actor;
import field.Entity;
import field.Location;
import models.animal.Animal;
import models.animal.behaviour.abilities.BlankAbility;
import models.animal.behaviour.abilities.FreezeSurroundings;
import models.animal.behaviour.breeding.DefaultBreeding;
import models.animal.behaviour.movement.RandomMovement;

/**
 * Gnome data model.
 *
 */
public class Gnome extends Animal {
    public Gnome(Location location) {
        super("gnome", location, new int[] {0, 0, 255});

        // Magic
        STARTING_ENERGY = 100;
        MAX_ENERGY = 100;
        BREEDING_ENERGY = (int)(0.5f * MAX_ENERGY);
        ENERGY_EFFICIENCY = 0.8;
        OPTIMAL_TEMPERATURE = 0;
        BREEDING_PROBABILITY = 0.1;
        
        // State
        energy = STARTING_ENERGY;
        movementBehaviour = new RandomMovement(this);
        breedingBehaviour = new DefaultBreeding(this);
        aliveAbility = new BlankAbility();
        onDeathAbility = new BlankAbility();
        onDeathAbility = new FreezeSurroundings(onDeathAbility, this, 50);
        eats = Arrays.asList("slime", "frost_flower");
    }

    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        if (entity instanceof Gnome) {
            Gnome gnome = (Gnome)entity;
            if (!this.sex.equals(gnome.sex) && rand.nextDouble() <= BREEDING_PROBABILITY) {
                Location loc = breedingBehaviour.act(location);
                if (loc != null) {
                    Gnome a = new Gnome(location);
                    field.setObjectAt(location, a);
                    newActors.add(a);
                    energy -= (int)(MAX_ENERGY * 0.5);
                }
            }
        } else {
            System.out.println("ERROR - breeding with different species");
        }
    }
}
