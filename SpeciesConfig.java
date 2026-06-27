import java.util.List;

/**
 * Immutable, data-object configuration of a species' simulation parameters.
 *
 * A single record covers both animals and plants; parameters that do not apply
 * to a given kind of organism (e.g. {@code huntProbability} or {@code prey}
 * for a plant) simply take their defaults. Instances are meant to be defined as
 * data in {@link SpeciesRegistry} and loaded by the species classes, keeping
 * the tuning numbers out of the simulation logic.
 *
 * @version 2022.03.01
 */
public record SpeciesConfig(
    int breedingAge,
    int maxAge,
    double breedingProbability,
    int maxLitterSize,
    int foodValue,
    int startingFoodLevel,
    double huntProbability,
    boolean nocturnal,
    List<String> prey
) {
    /**
     * Canonical constructor, taking a defensive, immutable copy of the prey list.
     */
    public SpeciesConfig {
        prey = List.copyOf(prey);
    }

    /**
     * @return A new builder for assembling a SpeciesConfig.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Fluent builder so each species reads clearly where it is defined, and so
     * a kind of organism can set only the parameters relevant to it.
     */
    public static final class Builder
    {
        private int breedingAge;
        private int maxAge;
        private double breedingProbability;
        private int maxLitterSize;
        private int foodValue;
        private int startingFoodLevel;
        private double huntProbability;
        private boolean nocturnal = false;
        private List<String> prey = List.of();

        public Builder breedingAge(int v) { this.breedingAge = v; return this; }
        public Builder maxAge(int v) { this.maxAge = v; return this; }
        public Builder breedingProbability(double v) { this.breedingProbability = v; return this; }
        public Builder maxLitterSize(int v) { this.maxLitterSize = v; return this; }
        public Builder foodValue(int v) { this.foodValue = v; return this; }
        public Builder startingFoodLevel(int v) { this.startingFoodLevel = v; return this; }
        public Builder huntProbability(double v) { this.huntProbability = v; return this; }
        public Builder nocturnal(boolean v) { this.nocturnal = v; return this; }
        public Builder prey(String... species) { this.prey = List.of(species); return this; }

        public SpeciesConfig build()
        {
            return new SpeciesConfig(breedingAge, maxAge, breedingProbability,
                maxLitterSize, foodValue, startingFoodLevel, huntProbability,
                nocturnal, prey);
        }
    }
}
