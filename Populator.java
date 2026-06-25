import java.awt.*;
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
                if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(19, true, field, location);
                    organisms.add(lion);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(5, true, field, location);
                    organisms.add(zebra);
                }
                else if(rand.nextDouble() <= VULTURE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Vulture vulture = new Vulture(40, true, field, location);
                    organisms.add(vulture);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(1, 1, true, field, location);
                    organisms.add(grass);
                }
                else if(rand.nextDouble() <= GOAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Goat goat = new Goat(5, true, field, location);
                    organisms.add(goat);
                }
                else if(rand.nextDouble() <= ELEPHANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Elephant elephant = new Elephant(5, true, field, location);
                    organisms.add(elephant);
                }
                else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cheetah cheetah = new Cheetah(19, true, field, location);
                    organisms.add(cheetah);
                }
                else if(rand.nextDouble() <= POISON_BERRIES_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    PoisonBerry berry = new PoisonBerry(2, 1, true, field, location);
                    organisms.add(berry);
                }
                // else leave the location empty.
            }
        }
    }
}
