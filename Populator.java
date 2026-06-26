import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A populator for the field in the simulation.
 *
 * @version 2022.03.02
 */
public class Populator {

    // define fields
    private static final double ANIMAL_CREATION_PROBABILITY = 0.05;
    private static final double PLANT_CREATION_PROBABILITY = 0.04;

    private final List<SpawnRule> spawnRules;

    /**
     * Constructor for the populator.
     *
     * @param view A given SimulatorView.
     */
    public Populator(SimulatorView view) {
        spawnRules = Arrays.asList(
                new SpawnRule(Lion.class, Color.RED, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Lion(randomAge, field, location)),
                new SpawnRule(Zebra.class, Color.BLUE, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Zebra(randomAge, field, location)),
                new SpawnRule(Vulture.class, Color.ORANGE, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Vulture(randomAge, field, location)),
                new SpawnRule(Grass.class, Color.GREEN, PLANT_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Grass(randomAge, field, location)),
                new SpawnRule(Goat.class, Color.PINK, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Goat(randomAge, field, location)),
                new SpawnRule(Elephant.class, Color.GRAY, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Elephant(randomAge, field, location)),
                new SpawnRule(Cheetah.class, Color.MAGENTA, ANIMAL_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new Cheetah(randomAge, field, location)),
                new SpawnRule(PoisonBerry.class, Color.BLACK, PLANT_CREATION_PROBABILITY,
                        (randomAge, field, location) -> new PoisonBerry(randomAge, field, location))
        );

        for (SpawnRule spawnRule : spawnRules) {
            view.setColor(spawnRule.getOrganismType(), spawnRule.getColor());
        }
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    public void populate(List<Organism> organisms, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateLocation(organisms, field, row, col, rand);
            }
        }
    }

    private void populateLocation(List<Organism> organisms, Field field, int row, int col, Random rand) {
        Location location = new Location(row, col);
        for (SpawnRule spawnRule : spawnRules) {
            if (rand.nextDouble() <= spawnRule.getCreationProbability()) {
                organisms.add(spawnRule.spawn(field, location));
                return;
            }
        }
    }

    private static class SpawnRule {
        private final Class<? extends Organism> organismType;
        private final Color color;
        private final double creationProbability;
        private final OrganismFactory spawnFactory;

        private SpawnRule(Class<? extends Organism> organismType, Color color,
                          double creationProbability, OrganismFactory spawnFactory) {
            this.organismType = organismType;
            this.color = color;
            this.creationProbability = creationProbability;
            this.spawnFactory = spawnFactory;
        }

        private Class<? extends Organism> getOrganismType() {
            return organismType;
        }

        private Color getColor() {
            return color;
        }

        private double getCreationProbability() {
            return creationProbability;
        }

        private Organism spawn(Field field, Location location) {
            return spawnFactory.create(true, field, location);
        }
    }
}
