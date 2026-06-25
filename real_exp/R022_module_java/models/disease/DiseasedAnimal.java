package models.disease;

import java.util.List;
import java.util.Random;

import field.Actor;
import field.Entity;
import field.Location;
import models.animal.Animal;
import models.animal.behaviour.movement.RandomMovement;

/**
 * Model for a diseased animal.
 *
 */
public class DiseasedAnimal extends Animal {
    private Random random;

    private int death_counter;
    private int time_till_death;

    private int MIN_TIME = 10, MAX_TIME = 50;
    private double INFECT_PROBABILITY = 0.005;

    /**
     * Constructor for DiseasedAnimal
     * @param location the initial location
     */
    public DiseasedAnimal(Location location) {
        super("diseased_animal", location, new int[] {255, 192, 203});
        
        random = new Random();
        death_counter = 0;
        time_till_death = random.nextInt(MAX_TIME - MIN_TIME) + MIN_TIME;

        movementBehaviour = new RandomMovement(this);
    }

    /**
     * Diseased animals don't breed
     */
    @Override
    protected void breed(List<Actor> newActors, Entity entity) {
        // do nothing
    }

    /**
     * Chance to infect surrounding animals. Die in a given time.
     */
    @Override
    public void act(List<Actor> newActors) {
        death_counter++;

        if (death_counter >= time_till_death) {
            die();
            return;
        }

        List<Location> locations = field.getAdjacentLocations(location);

        for (Location location : locations) {
            Entity e = field.getBlockAt(location).getEntity();

            if (e == null) {
                continue;
            }

            if (e instanceof Animal) {
                if (random.nextDouble() < INFECT_PROBABILITY) {
                    Location toReplace = e.getLocation();
                    e.setDead();
                    DiseasedAnimal a = new DiseasedAnimal(toReplace);
                    field.setObjectAt(toReplace, a);
                    newActors.add(a);
                }
            }
        }

        movementBehaviour.act();
    }
}
