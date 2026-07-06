import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Populates a field with actors according to fixed creation probabilities.
 * Each cell is checked against each species in order; the first probability
 * that passes claims the cell (consuming exactly one RNG call per species
 * checked, preserving the original cascade behaviour).
 */
public class FieldPopulator
{
    private static final double GRASSHOPPER_CREATION_PROBABILITY   = 0.15;
    private static final double HARVESTER_ANT_CREATION_PROBABILITY = 0.25;
    private static final double TERMITE_CREATION_PROBABILITY       = 0.21;
    private static final double IMPALA_CREATION_PROBABILITY        = 0.15;
    private static final double PANGOLIN_CREATION_PROBABILITY      = 0.125;
    private static final double AARDVARK_CREATION_PROBABILITY      = 0.12;
    private static final double MONGOOSE_CREATION_PROBABILITY      = 0.12;
    private static final double STAR_GRASS_CREATION_PROBABILITY    = 0.05;
    private static final double RED_OAT_GRASS_CREATION_PROBABILITY = 0.04;
    private static final double ACACIA_CREATION_PROBABILITY        = 0.04;

    private static class Entry
    {
        final double probability;
        final BiFunction<Field, Location, Actor> creator;

        Entry(double probability, BiFunction<Field, Location, Actor> creator)
        {
            this.probability = probability;
            this.creator = creator;
        }
    }

    private final List<Entry> entries;

    public FieldPopulator()
    {
        entries = new ArrayList<>();
        entries.add(new Entry(GRASSHOPPER_CREATION_PROBABILITY,   (f, l) -> new Grasshopper(true, f, l)));
        entries.add(new Entry(HARVESTER_ANT_CREATION_PROBABILITY, (f, l) -> new HarvesterAnt(true, f, l)));
        entries.add(new Entry(TERMITE_CREATION_PROBABILITY,       (f, l) -> new Termite(true, f, l)));
        entries.add(new Entry(IMPALA_CREATION_PROBABILITY,        (f, l) -> new Impala(true, f, l)));
        entries.add(new Entry(PANGOLIN_CREATION_PROBABILITY,      (f, l) -> new Pangolin(true, f, l)));
        entries.add(new Entry(AARDVARK_CREATION_PROBABILITY,      (f, l) -> new Aardvark(true, f, l)));
        entries.add(new Entry(MONGOOSE_CREATION_PROBABILITY,      (f, l) -> new Mongoose(true, f, l)));
        entries.add(new Entry(STAR_GRASS_CREATION_PROBABILITY,    (f, l) -> new StarGrass(f, l)));
        entries.add(new Entry(RED_OAT_GRASS_CREATION_PROBABILITY, (f, l) -> new RedOatGrass(f, l)));
        entries.add(new Entry(ACACIA_CREATION_PROBABILITY,        (f, l) -> new Acacia(f, l)));
    }

    /**
     * Clear the field and place actors in each cell according to the registered
     * creation probabilities.
     *
     * @param field  The field to populate.
     * @param actors The list to receive the newly created actors.
     */
    public void populate(Field field, List<Actor> actors)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (Entry entry : entries) {
                    if (rand.nextDouble() <= entry.probability) {
                        actors.add(entry.creator.apply(field, location));
                        break;
                    }
                }
            }
        }
    }
}
