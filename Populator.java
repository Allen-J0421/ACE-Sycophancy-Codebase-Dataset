import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A populator for the field in the simulation.
 *
 * @version 2022.03.02
 */
public class Populator {

    // define fields
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.05;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.05;
    // The probability that a vulture will be created in any given grid position.
    private static final double VULTURE_CREATION_PROBABILITY = 0.05;
    // The probability that an elephant will be created in any given grid position.
    private static final double ELEPHANT_CREATION_PROBABILITY = 0.05;
    // The probability that a cheetah will be created in any given grid position.
    private static final double CHEETAH_CREATION_PROBABILITY = 0.05;
    // The probability that a goat will be created in any given grid position.
    private static final double GOAT_CREATION_PROBABILITY = 0.05;
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.04;
    // The probability that some poison berries will be created in any given grid position.
    private static final double POISON_BERRIES_CREATION_PROBABILITY = 0.04;

    /**
     * Constructor for the populator.
     *
     * @param view A given SimulatorView.
     */
    public Populator(SimulatorView view) {
        // Create a view of the state of each location in the field.
        view.setColor(Zebra.class, Color.BLUE);
        view.setColor(Lion.class, Color.RED);
        view.setColor(Vulture.class, Color.ORANGE);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Goat.class, Color.PINK);
        view.setColor(Elephant.class, Color.GRAY);
        view.setColor(Cheetah.class, Color.MAGENTA);
        view.setColor(PoisonBerry.class, Color.BLACK);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    public void populate(List<Entity> organisms, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if (maybeAddOrganism(rand, organisms, field, row, col, LION_CREATION_PROBABILITY,
                        (currentField, location) -> new Lion(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, ZEBRA_CREATION_PROBABILITY,
                        (currentField, location) -> new Zebra(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, VULTURE_CREATION_PROBABILITY,
                        (currentField, location) -> new Vulture(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, GRASS_CREATION_PROBABILITY,
                        (currentField, location) -> new Grass(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, GOAT_CREATION_PROBABILITY,
                        (currentField, location) -> new Goat(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, ELEPHANT_CREATION_PROBABILITY,
                        (currentField, location) -> new Elephant(true, currentField, location))) {
                    continue;
                }
                if (maybeAddOrganism(rand, organisms, field, row, col, CHEETAH_CREATION_PROBABILITY,
                        (currentField, location) -> new Cheetah(true, currentField, location))) {
                    continue;
                }
                maybeAddOrganism(rand, organisms, field, row, col, POISON_BERRIES_CREATION_PROBABILITY,
                        (currentField, location) -> new PoisonBerry(true, currentField, location));
                // else leave the location empty.
            }
        }
    }

    /**
     * Add a single organism to the requested location when the probability check passes.
     *
     * @return true if an organism was created and added.
     */
    private boolean maybeAddOrganism(Random rand, List<Entity> organisms, Field field, int row, int col,
                                     double probability, BiFunction<Field, Location, Entity> creator) {
        if (rand.nextDouble() <= probability) {
            organisms.add(creator.apply(field, new Location(row, col)));
            return true;
        }
        return false;
    }
}
