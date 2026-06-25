package field;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle the collection of the counts of each species.
 * Implemented as a singleton.
 *
 */
public class FieldStatistics {
    private Field field;
    private Map<String, Integer> statistics;

    private static FieldStatistics instance;

    /**
     * @return the instance
     */
    public static FieldStatistics getInstance() {
        if (instance == null) {
            instance = new FieldStatistics();
        }
        return instance;
    }

    /**
     * Constructor for FieldStatistics.
     */
    private FieldStatistics() {
        statistics = new HashMap<>();
        field = Field.getInstance();

        reset();
    }

    /**
     * Reset and count each species in the Field.
     */
    public void update() {
        reset();

        for (int i=0; i<field.getRows(); i++) {
            for (int j=0; j<field.getCols(); j++) {
                Entity e = field.getBlockAt(i, j).getEntity();
                if (e != null) {
                    Integer pop = statistics.get(e.getID());
                    if (pop == null) {
                        statistics.put(e.getID(), 1);
                    } else {
                        statistics.put(e.getID(), pop + 1);
                    }
                }
            }
        }
    }

    /**
     * Output the counts to STDOUT.
     */
    public void print() {
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            System.out.print(entry.getKey() + " " + entry.getValue() + "; ");
        }
        System.out.println();
    }

    /**
     * Construct a string containing the populations and their counts.
     * @return the string containing the populations and their counts.
     */
    public String getDetails() {
        String populationDetails = "";
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            populationDetails += entry.getKey() + " " + entry.getValue() + "\n";
        }
        return populationDetails;
    }

    /**
     * @param id id of the species
     * @return the count for that species
     */
    public int getPopulationCount(String id) {
        return statistics.get(id);
    }

    /**
     * Clear the map and input all of the species.
     * This is to ensure that species with 0 alive entities
     * are still displayed.
     */
    public void reset() {
        statistics.clear();

        statistics.put("grass", 0);
        statistics.put("tall_grass", 0);
        statistics.put("cactus", 0);
        statistics.put("frost_flower", 0);
        statistics.put("elk", 0);
        statistics.put("slime", 0);
        statistics.put("wolf", 0);
        statistics.put("gnome", 0);
        statistics.put("dragon", 0);
        statistics.put("phoenix", 0);
        statistics.put("diseased_animal", 0);
        statistics.put("diseased_plant", 0);
    }

    /**
     * Get the fieldStatistics map
     * @return the map containing species and their counts
     */
    public Map<String, Integer> getStatistics() {
        return statistics;
    }
}
