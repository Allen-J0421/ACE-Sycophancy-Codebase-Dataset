import java.awt.Color;
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

    /**
     * Creates a new organism of a particular species at a field location.
     */
    @FunctionalInterface
    private interface OrganismFactory {
        Organism create(Field field, Location location);
    }

    /**
     * Describes how a species is seeded into the field: how likely it is to
     * appear in any given cell, the colour used to draw it, and how to create
     * an instance.
     */
    private static class SpawnRule {
        final double probability;
        final Class<?> species;
        final Color color;
        final OrganismFactory factory;

        SpawnRule(double probability, Class<?> species, Color color, OrganismFactory factory) {
            this.probability = probability;
            this.species = species;
            this.color = color;
            this.factory = factory;
        }
    }

    // The spawn rules, evaluated in order for each grid cell: the first rule
    // whose probability check passes claims the cell. Order and probabilities
    // are preserved from the original simulation.
    private static final List<SpawnRule> SPAWN_RULES = List.of(
        new SpawnRule(0.05, Lion.class,        Color.RED,     (f, l) -> new Lion(19, true, f, l)),
        new SpawnRule(0.05, Zebra.class,       Color.BLUE,    (f, l) -> new Zebra(5, true, f, l)),
        new SpawnRule(0.05, Vulture.class,     Color.ORANGE,  (f, l) -> new Vulture(40, true, f, l)),
        new SpawnRule(0.04, Grass.class,       Color.GREEN,   (f, l) -> new Grass(1, 1, true, f, l)),
        new SpawnRule(0.05, Goat.class,        Color.PINK,    (f, l) -> new Goat(5, true, f, l)),
        new SpawnRule(0.05, Elephant.class,    Color.GRAY,    (f, l) -> new Elephant(5, true, f, l)),
        new SpawnRule(0.05, Cheetah.class,     Color.MAGENTA, (f, l) -> new Cheetah(19, true, f, l)),
        new SpawnRule(0.04, PoisonBerry.class, Color.BLACK,   (f, l) -> new PoisonBerry(2, 1, true, f, l))
    );

    /**
     * Constructor for the populator.
     *
     * @param view A given SimulationView.
     */
    public Populator(SimulationView view) {
        // Register the display colour of each species with the view.
        for (SpawnRule rule : SPAWN_RULES) {
            view.setColor(rule.species, rule.color);
        }
    }

    /**
     * Randomly populate the field with the simulation's organisms.
     */
    public void populate(List<Entity> organisms, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                // The first rule that passes its probability check claims the cell.
                for (SpawnRule rule : SPAWN_RULES) {
                    if (rand.nextDouble() <= rule.probability) {
                        organisms.add(rule.factory.create(field, new Location(row, col)));
                        break;
                    }
                }
                // else leave the location empty.
            }
        }
    }
}
