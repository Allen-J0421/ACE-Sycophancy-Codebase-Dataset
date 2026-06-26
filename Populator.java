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

    private static final double DEFAULT_ANIMAL_CREATION_PROBABILITY = 0.05;
    private static final double DEFAULT_PLANT_CREATION_PROBABILITY = 0.04;

    private static final PopulationRule[] POPULATION_RULES = {
            new PopulationRule(Lion.class, Color.RED, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Lion::createDefault),
            new PopulationRule(Zebra.class, Color.BLUE, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Zebra::createDefault),
            new PopulationRule(Vulture.class, Color.ORANGE, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Vulture::createDefault),
            new PopulationRule(Grass.class, Color.GREEN, DEFAULT_PLANT_CREATION_PROBABILITY,
                    Grass::createDefault),
            new PopulationRule(Goat.class, Color.PINK, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Goat::createDefault),
            new PopulationRule(Elephant.class, Color.GRAY, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Elephant::createDefault),
            new PopulationRule(Cheetah.class, Color.MAGENTA, DEFAULT_ANIMAL_CREATION_PROBABILITY,
                    Cheetah::createDefault),
            new PopulationRule(PoisonBerry.class, Color.BLACK, DEFAULT_PLANT_CREATION_PROBABILITY,
                    PoisonBerry::createDefault)
    };

    /**
     * Constructor for the populator.
     *
     * @param view A given SimulatorView.
     */
    public Populator(SimulatorView view) {
        for (PopulationRule rule : POPULATION_RULES) {
            view.setColor(rule.getOrganismClass(), rule.getColor());
        }
    }

    /**
     * Randomly populate the field with organisms.
     */
    public void populate(List<Organism> organisms, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateLocation(organisms, field, rand, new Location(row, col));
            }
        }
    }

    /**
     * Try each population rule in order for a single location.
     *
     * @param organisms The organisms list to add to.
     * @param field The field to populate.
     * @param rand The random source.
     * @param location The location being populated.
     */
    private void populateLocation(List<Organism> organisms, Field field, Random rand, Location location) {
        for (PopulationRule rule : POPULATION_RULES) {
            if (rand.nextDouble() <= rule.getCreationProbability()) {
                organisms.add(rule.create(field, location));
                return;
            }
        }
    }

    /**
     * Factory for creating organisms at a specific location.
     */
    private interface OrganismFactory {
        Organism create(Field field, Location location);
    }

    /**
     * Configuration for one type of organism to place in the field.
     */
    private static final class PopulationRule {
        private final Class<?> organismClass;
        private final Color color;
        private final double creationProbability;
        private final OrganismFactory factory;

        PopulationRule(Class<?> organismClass, Color color, double creationProbability,
                       OrganismFactory factory) {
            this.organismClass = organismClass;
            this.color = color;
            this.creationProbability = creationProbability;
            this.factory = factory;
        }

        Class<?> getOrganismClass() {
            return organismClass;
        }

        Color getColor() {
            return color;
        }

        double getCreationProbability() {
            return creationProbability;
        }

        Organism create(Field field, Location location) {
            return factory.create(field, location);
        }
    }
}
