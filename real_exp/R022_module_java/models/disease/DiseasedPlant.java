package models.disease;

import java.util.List;
import java.util.Random;

import field.Actor;
import field.Entity;
import field.Location;
import models.plant.Plant;

/**
 * Model for diseased plant.
 *
 */
public class DiseasedPlant extends Plant {
    private Random random;
    
    private int death_counter;
    private int time_till_death;

    private int MIN_TIME = 1, MAX_TIME = 20;
    private double INFECT_PROBABILITY = 0.04;

    /**
     * Constructor for DiseasedPlant. Initialise the death counter and limit.
     * @param location the initial location of the plant
     */
    public DiseasedPlant(Location location) {
        super("diseased_plant", location, new int[] {255, 192, 203});
        random = new Random();
        
        death_counter = 0;
        time_till_death = random.nextInt(MAX_TIME-MIN_TIME)+MIN_TIME;
        
    }

    /**
     * Chance to infect nearby plants. Die in a specified time.
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

            if (e instanceof Plant) {
                if (random.nextDouble() < INFECT_PROBABILITY) {
                    Location toReplace = e.getLocation();
                    e.setDead();
                    DiseasedPlant a = new DiseasedPlant(toReplace);
                    field.setObjectAt(toReplace, a);
                    newActors.add(a);
                }
            }
        }   
    }
}
