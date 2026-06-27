import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Describes which species (and non-animal entities) are spawned during field
 * initialisation, and how to create them.  Each registered entry is tried in
 * order; the first one whose creation probability is met wins for that cell
 * (matching the original else-if logic in Generator).
 *
 * To add a new species, call {@link #add} with its creation probability and a
 * factory lambda — no other class needs to change.
 */
class SimulationConfig {

    /** One entry in the spawn registry. */
    static class Entry {
        final double creationProbability;
        final BiFunction<Field, Location, Simulatable> factory;

        Entry(double creationProbability, BiFunction<Field, Location, Simulatable> factory) {
            this.creationProbability = creationProbability;
            this.factory = factory;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    /**
     * Register a spawnable entity type.
     * @param prob    probability that this entity fills a cell (checked only if
     *                all prior entries in the list were skipped)
     * @param factory (field, location) -> new instance with randomAge=true
     * @return this, for chaining
     */
    SimulationConfig add(double prob, BiFunction<Field, Location, Simulatable> factory) {
        entries.add(new Entry(prob, factory));
        return this;
    }

    List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Returns the standard configuration used by the simulation. */
    static SimulationConfig defaultConfig() {
        return new SimulationConfig()
            .add(Racoon.POPULATION_CONTROLS.getCreationProbability(),
                    (f, loc) -> new Racoon(true, f, loc))
            .add(Hawk.POPULATION_CONTROLS.getCreationProbability(),
                    (f, loc) -> new Hawk(true, f, loc))
            .add(Mouse.POPULATION_CONTROLS.getCreationProbability(),
                    (f, loc) -> new Mouse(true, f, loc))
            .add(Rat.POPULATION_CONTROLS.getCreationProbability(),
                    (f, loc) -> new Rat(true, f, loc))
            .add(Squirrel.POPULATION_CONTROLS.getCreationProbability(),
                    (f, loc) -> new Squirrel(true, f, loc))
            .add(Grass.CREATION_PROBABILITY,
                    (f, loc) -> new Grass(f, loc));
    }
}
