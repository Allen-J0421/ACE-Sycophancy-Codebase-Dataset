import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Populates the field with organisms according to a collection of spawn rules.
 * The standard species are registered by default, but callers may register
 * further species dynamically via {@link #register} before populating, so new
 * species can be added without editing this class.
 *
 * @version 2022.03.02
 */
public class Populator {

    /**
     * Creates a new organism of a particular species at a field location.
     */
    @FunctionalInterface
    public interface OrganismFactory {
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

    // The view that displays the simulation; used to register species colours.
    private final SimulationView view;
    // The registered spawn rules, evaluated in registration order for each grid
    // cell: the first rule whose probability check passes claims the cell.
    private final List<SpawnRule> spawnRules = new ArrayList<>();

    /**
     * Constructor for the populator. Registers the standard set of species.
     *
     * @param view A given SimulationView.
     */
    public Populator(SimulationView view) {
        this.view = view;
        registerDefaultSpecies();
    }

    /**
     * Register a species to be seeded into the field. Rules are evaluated in
     * the order they are registered, and the species' colour is registered with
     * the view at the same time. Registering further species before calling
     * {@link #populate} lets new species be added without changing this class.
     *
     * @param probability The chance of this species appearing in any given cell.
     * @param species The species' Class object (used for its display colour).
     * @param color The colour used to draw the species.
     * @param factory How to create an instance at a field location.
     */
    public void register(double probability, Class<?> species, Color color, OrganismFactory factory) {
        addRule(probability, species, color, factory);
    }

    /**
     * Add a spawn rule and register its species' colour with the view. Private
     * so it can safely be called during construction.
     */
    private void addRule(double probability, Class<?> species, Color color, OrganismFactory factory) {
        spawnRules.add(new SpawnRule(probability, species, color, factory));
        view.setColor(species, color);
    }

    /**
     * Register the standard set of species. Order and probabilities are
     * preserved from the original simulation.
     */
    private void registerDefaultSpecies() {
        addRule(0.05, Lion.class,        Color.RED,     (f, l) -> new Lion(19, true, f, l));
        addRule(0.05, Zebra.class,       Color.BLUE,    (f, l) -> new Zebra(5, true, f, l));
        addRule(0.05, Vulture.class,     Color.ORANGE,  (f, l) -> new Vulture(40, true, f, l));
        addRule(0.04, Grass.class,       Color.GREEN,   (f, l) -> new Grass(1, 1, true, f, l));
        addRule(0.05, Goat.class,        Color.PINK,    (f, l) -> new Goat(5, true, f, l));
        addRule(0.05, Elephant.class,    Color.GRAY,    (f, l) -> new Elephant(5, true, f, l));
        addRule(0.05, Cheetah.class,     Color.MAGENTA, (f, l) -> new Cheetah(19, true, f, l));
        addRule(0.04, PoisonBerry.class, Color.BLACK,   (f, l) -> new PoisonBerry(2, 1, true, f, l));
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
                for (SpawnRule rule : spawnRules) {
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
