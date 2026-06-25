package controller;

import java.util.List;
import java.util.Random;

import field.Actor;
import field.Block;
import field.Field;
import field.Location;
import models.animal.AnimalFactory;
import models.plant.Plant;
import models.plant.PlantFactory;
import models.plant.implementations.Cactus;
import models.plant.implementations.FrostFlower;
import utils.RandomCollection;
import utils.Randomizer;

/**
 * Handles initialising the entities/actors on the field.
 *
 */
public class SimulatorInitializer {
    private Field field;
    private List<Actor> actors;
    private Random random;
    private RandomCollection<String> randomCollection;

    /**
     * Constructor for SimulatorInitializer. Set the entities and their corresponding
     * weight to being put on a block in the field.
     * @param actors This is where all new actors are added once they are initialised.
     */
    public SimulatorInitializer(List<Actor> actors) {
        random = Randomizer.getRandom();
        randomCollection = new RandomCollection<>(random);
        field = Field.getInstance();
        this.actors = actors;

        randomCollection.add(500, null);
        randomCollection.add(50, "grass");
        randomCollection.add(50, "tall_grass");
        randomCollection.add(1, "cactus");
        randomCollection.add(1, "frost_flower");
        randomCollection.add(5, "elk");
        randomCollection.add(5, "slime");
        randomCollection.add(1, "wolf");
        randomCollection.add(1, "gnome");
        randomCollection.add(0.1, "dragon");
        randomCollection.add(1, "phoenix");
        randomCollection.add(5, "diseased_plant");
    }

    /**
     * Populate the field with actors. Where the temperature is very hot, cacti have a higher
     * probability of being spawned. Where the temperature is very cold, frost flowers have
     * a higher chance of being spawned.
     */
    public void populate() {
        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) {
                Block block = field.getBlockAt(i, j);
                Location location = new Location(i, j);

                if (Math.abs(block.getTemperature() - 70) < 10) {
                    if (random.nextDouble() < 0.4) {
                        Plant a = new Cactus(location);
                        block.setEntity(a);
                        actors.add(a);
                        continue;
                    }
                } 

                else if (Math.abs(block.getTemperature() - 0) < 2) {
                    if (random.nextDouble() < 0.4) {
                        Plant a = new FrostFlower(location);
                        block.setEntity(a);
                        actors.add(a);
                        continue;
                    }
                }

                String actorString = randomCollection.next();

                Actor a = AnimalFactory.get(actorString, location);

                if (a == null) {
                    a = PlantFactory.get(actorString, location);
                }

                if (a != null) {
                    block.setEntity(a);
                    actors.add(a);
                }
            }
        }
    }
}
