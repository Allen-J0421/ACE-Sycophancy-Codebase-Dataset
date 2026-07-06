import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Concrete {@link FieldPopulator} that fills the field with a random initial
 * population based on a list of per-species configuration entries.
 *
 * Each cell is checked against the entries in order; the first species whose
 * creation-probability roll succeeds is placed at that cell (matching the
 * original if/else-if cascade semantics exactly). Cells where no species rolls
 * successfully are left empty.
 *
 * The static {@link #createDefault()} factory returns a fully configured
 * instance ready to use with the standard eight species.
 *
 * @version 2022.03.02
 */
public class RandomFieldPopulator implements FieldPopulator {

    private final List<SpeciesEntry> species;

    /**
     * Construct a populator from an ordered list of species entries.
     * Entries are checked in list order — put higher-priority species first.
     *
     * @param species Ordered list of species configuration entries.
     */
    public RandomFieldPopulator(List<SpeciesEntry> species) {
        this.species = Collections.unmodifiableList(species);
    }

    /**
     * Return the species configuration list, allowing callers (e.g.
     * {@code Simulator}) to register display colors without coupling this
     * class to the view.
     *
     * @return An unmodifiable view of the species entries.
     */
    public List<SpeciesEntry> getSpecies() {
        return species;
    }

    /**
     * Create a {@code RandomFieldPopulator} configured with the standard eight
     * species and their default creation probabilities and display colors.
     *
     * @return A ready-to-use default populator.
     */
    public static RandomFieldPopulator createDefault() {
        return new RandomFieldPopulator(Arrays.asList(
            new SpeciesEntry(Lion.class,       Color.RED,     0.05,
                (field, loc) -> new Lion(19, true, field, loc)),
            new SpeciesEntry(Zebra.class,      Color.BLUE,    0.05,
                (field, loc) -> new Zebra(5, true, field, loc)),
            new SpeciesEntry(Vulture.class,    Color.ORANGE,  0.05,
                (field, loc) -> new Vulture(40, true, field, loc)),
            new SpeciesEntry(Grass.class,      Color.GREEN,   0.04,
                (field, loc) -> new Grass(1, 1, true, field, loc)),
            new SpeciesEntry(Goat.class,       Color.PINK,    0.05,
                (field, loc) -> new Goat(5, true, field, loc)),
            new SpeciesEntry(Elephant.class,   Color.GRAY,    0.05,
                (field, loc) -> new Elephant(5, true, field, loc)),
            new SpeciesEntry(Cheetah.class,    Color.MAGENTA, 0.05,
                (field, loc) -> new Cheetah(19, true, field, loc)),
            new SpeciesEntry(PoisonBerry.class, Color.BLACK,  0.04,
                (field, loc) -> new PoisonBerry(2, 1, true, field, loc))
        ));
    }

    /**
     * Clear the field and populate it with organisms.
     *
     * Iterates every cell in the grid; for each cell, walks the species list
     * and draws a fresh random number against each entry's creation probability.
     * The first species that succeeds is placed and the remaining entries are
     * skipped for that cell.
     *
     * @param organisms The list to which all newly created organisms are added.
     * @param field     The field to populate.
     */
    @Override
    public void populate(List<Entity> organisms, Field field) {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (SpeciesEntry entry : species) {
                    if (rand.nextDouble() <= entry.creationProbability) {
                        organisms.add(entry.factory.create(field, location));
                        break;
                    }
                }
            }
        }
    }
}
