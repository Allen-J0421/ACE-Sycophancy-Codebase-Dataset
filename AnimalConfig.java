import java.util.ArrayList;
import java.util.Arrays;

/**
 * Immutable, instance-based configuration of an animal species' characteristics.
 *
 * Replaces the per-subclass {@code static final} constants and the matching
 * abstract getter hooks that used to be duplicated across every Animal
 * subclass. Each animal instance carries its own config, built with the
 * fluent {@link Builder} so that a species reads clearly at its call site.
 *
 * @version 2022.03.01
 */
public final class AnimalConfig
{
    // The age at which the animal can start to breed.
    private final int breedingAge;
    // The age to which the animal can live.
    private final int maxAge;
    // The likelihood of the animal breeding when eligible.
    private final double breedingProbability;
    // The maximum number of young produced in a single birth.
    private final int maxLitterSize;
    // The food value gained by a predator that eats this animal.
    private final int foodValue;
    // The food level a randomly-aged animal starts life with.
    private final int startingFoodLevel;
    // The probability of a successful hunt (used by hunters that gate eating).
    private final double huntProbability;
    // Whether the animal is nocturnal (acts at night) or not.
    private final boolean nocturnal;
    // The class names of the species this animal eats.
    private final ArrayList<String> prey;

    private AnimalConfig(Builder builder)
    {
        this.breedingAge = builder.breedingAge;
        this.maxAge = builder.maxAge;
        this.breedingProbability = builder.breedingProbability;
        this.maxLitterSize = builder.maxLitterSize;
        this.foodValue = builder.foodValue;
        this.startingFoodLevel = builder.startingFoodLevel;
        this.huntProbability = builder.huntProbability;
        this.nocturnal = builder.nocturnal;
        this.prey = builder.prey;
    }

    public int getBreedingAge() { return breedingAge; }
    public int getMaxAge() { return maxAge; }
    public double getBreedingProbability() { return breedingProbability; }
    public int getMaxLitterSize() { return maxLitterSize; }
    public int getFoodValue() { return foodValue; }
    public int getStartingFoodLevel() { return startingFoodLevel; }
    public double getHuntProbability() { return huntProbability; }
    public boolean isNocturnal() { return nocturnal; }
    public ArrayList<String> getPrey() { return prey; }

    /**
     * @return A new builder for assembling an AnimalConfig.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Fluent builder for AnimalConfig. Naming each characteristic at the call
     * site avoids the error-prone positional argument list a nine-field
     * constructor would otherwise require.
     */
    public static class Builder
    {
        private int breedingAge;
        private int maxAge;
        private double breedingProbability;
        private int maxLitterSize;
        private int foodValue;
        private int startingFoodLevel;
        private double huntProbability;
        private boolean nocturnal = false;
        private ArrayList<String> prey = new ArrayList<>();

        public Builder breedingAge(int v) { this.breedingAge = v; return this; }
        public Builder maxAge(int v) { this.maxAge = v; return this; }
        public Builder breedingProbability(double v) { this.breedingProbability = v; return this; }
        public Builder maxLitterSize(int v) { this.maxLitterSize = v; return this; }
        public Builder foodValue(int v) { this.foodValue = v; return this; }
        public Builder startingFoodLevel(int v) { this.startingFoodLevel = v; return this; }
        public Builder huntProbability(double v) { this.huntProbability = v; return this; }
        public Builder nocturnal(boolean v) { this.nocturnal = v; return this; }
        public Builder prey(String... species) { this.prey = new ArrayList<>(Arrays.asList(species)); return this; }

        public AnimalConfig build() { return new AnimalConfig(this); }
    }
}
