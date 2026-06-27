import java.util.ArrayList;
import java.util.Random;

/**
 * Generator populates a field with the entities defined in a {@link SimulationConfig}.
 * Adding a new species requires only updating the config — this class is unchanged.
 *
 * @version 6.8.2
 */
class Generator {
    private final Random rand = Randomizer.getRandom();

    /**
     * Populate a field using the default simulation configuration.
     * @param field the field to populate
     * @return list of all created simulatables
     */
    public ArrayList<Simulatable> createPlayers(Field field) {
        return createPlayers(field, SimulationConfig.defaultConfig());
    }

    /**
     * Populate a field using the given configuration.
     * For each cell the entries are tried in order; the first whose creation
     * probability is met spawns an entity in that cell (else-if semantics).
     * @param field  the field to populate
     * @param config the spawn configuration to use
     * @return list of all created simulatables
     */
    public ArrayList<Simulatable> createPlayers(Field field, SimulationConfig config) {
        ArrayList<Simulatable> simulatables = new ArrayList<>();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Disease disease = generateDisease();
                Location location = new Location(row, col);
                for (SimulationConfig.Entry entry : config.getEntries()) {
                    if (rand.nextDouble() <= entry.creationProbability) {
                        Simulatable s = entry.factory.apply(field, location);
                        if (s instanceof Animal) {
                            ((Animal) s).forceDisease(disease);
                        }
                        simulatables.add(s);
                        break;
                    }
                }
            }
        }
        return simulatables;
    }

    private Disease generateDisease() {
        if (rand.nextDouble() <= Rabies.CREATION_PROBABILITY) {
            return new Rabies();
        }
        return null;
    }
}
