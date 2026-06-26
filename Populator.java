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

    private final List<SpeciesDefinition> speciesDefinitions;

    /**
     * Constructor for the populator.
     *
     * @param speciesCatalog Catalog of available species.
     */
    public Populator(SpeciesCatalog speciesCatalog) {
        this.speciesDefinitions = speciesCatalog.getDefinitions();
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
        for (SpeciesDefinition speciesDefinition : speciesDefinitions) {
            if (rand.nextDouble() <= speciesDefinition.getCreationProbability()) {
                organisms.add(speciesDefinition.create(true, field, location));
                return;
            }
        }
    }
}
