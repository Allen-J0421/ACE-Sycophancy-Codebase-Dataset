import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Populates the field with an initial distribution of organisms and
 * registers species colours with the view.
 *
 * @version 2022.03.02
 */
public class Populator {

    // Probability that a given grid cell contains each species.
    // A single random draw per cell is compared against cumulative thresholds,
    // so each stated probability is exact (not conditional on prior species).
    private static final double LION_CREATION_PROBABILITY         = 0.05;
    private static final double ZEBRA_CREATION_PROBABILITY        = 0.05;
    private static final double VULTURE_CREATION_PROBABILITY      = 0.05;
    private static final double GRASS_CREATION_PROBABILITY        = 0.04;
    private static final double GOAT_CREATION_PROBABILITY         = 0.05;
    private static final double ELEPHANT_CREATION_PROBABILITY     = 0.05;
    private static final double CHEETAH_CREATION_PROBABILITY      = 0.05;
    private static final double POISON_BERRIES_CREATION_PROBABILITY = 0.04;

    /**
     * Constructor for the populator. Registers species colours with the view.
     *
     * @param view The simulation view to register colours with.
     */
    public Populator(SimulatorView view) {
        view.setColor(Lion.class,        Color.RED);
        view.setColor(Cheetah.class,     Color.MAGENTA);
        view.setColor(Vulture.class,     Color.ORANGE);
        view.setColor(Zebra.class,       Color.BLUE);
        view.setColor(Goat.class,        Color.PINK);
        view.setColor(Elephant.class,    Color.GRAY);
        view.setColor(Grass.class,       Color.GREEN);
        view.setColor(PoisonBerry.class, Color.BLACK);
    }

    /**
     * Randomly populate the field with organisms.
     * Each cell gets at most one organism, chosen by a single random draw
     * compared against cumulative probability thresholds.
     *
     * @param organisms The list to add newly created organisms to.
     * @param field     The field to populate.
     */
    public void populate(List<Organism> organisms, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                double r = rand.nextDouble();
                double cumulative = 0.0;

                if (r < (cumulative += LION_CREATION_PROBABILITY)) {
                    organisms.add(Lion.spawn(field, location));
                } else if (r < (cumulative += ZEBRA_CREATION_PROBABILITY)) {
                    organisms.add(Zebra.spawn(field, location));
                } else if (r < (cumulative += VULTURE_CREATION_PROBABILITY)) {
                    organisms.add(Vulture.spawn(field, location));
                } else if (r < (cumulative += GRASS_CREATION_PROBABILITY)) {
                    organisms.add(Grass.spawn(field, location));
                } else if (r < (cumulative += GOAT_CREATION_PROBABILITY)) {
                    organisms.add(Goat.spawn(field, location));
                } else if (r < (cumulative += ELEPHANT_CREATION_PROBABILITY)) {
                    organisms.add(Elephant.spawn(field, location));
                } else if (r < (cumulative += CHEETAH_CREATION_PROBABILITY)) {
                    organisms.add(Cheetah.spawn(field, location));
                } else if (r < (cumulative += POISON_BERRIES_CREATION_PROBABILITY)) {
                    organisms.add(PoisonBerry.spawn(field, location));
                }
                // else: leave the cell empty
            }
        }
    }
}
