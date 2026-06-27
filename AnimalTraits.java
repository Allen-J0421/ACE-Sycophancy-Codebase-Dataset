import java.util.HashSet;

/**
 * An immutable bundle of the fixed, species-level characteristics shared by
 * every individual of an Animal subclass: its breeding parameters, lifespan,
 * health, activity period and its diet/kill lists.
 *
 * Each Animal subclass declares a single static AnimalTraits instance and hands
 * it to the Animal base class, which reads all of its species constants through
 * it. This replaces the block of per-class constants and trivial accessor
 * methods that used to be copy-pasted into every subclass.
 *
 * @version 2022.03.02
 */
public class AnimalTraits
{
    // The age at which the animal can start to breed.
    private final int breedingAge;
    // The age to which the animal can live.
    private final int maxAge;
    // The likelihood of the animal breeding.
    private final double breedingProbability;
    // The maximum number of births.
    private final int maxLitterSize;
    // The max health of the animal.
    private final int maxHealth;
    // Whether the animal is diurnal (true) or nocturnal (false).
    private final boolean diurnal;
    // The classes this animal will eat.
    private final HashSet<Class> foodSources;
    // The classes this animal will kill.
    private final HashSet<Class> killable;

    private AnimalTraits(Builder builder)
    {
        this.breedingAge = builder.breedingAge;
        this.maxAge = builder.maxAge;
        this.breedingProbability = builder.breedingProbability;
        this.maxLitterSize = builder.maxLitterSize;
        this.maxHealth = builder.maxHealth;
        this.diurnal = builder.diurnal;
        this.foodSources = builder.foodSources;
        this.killable = builder.killable;
    }

    protected int getBreedingAge() { return breedingAge; }

    protected int getMaxAge() { return maxAge; }

    protected double getBreedingProbability() { return breedingProbability; }

    protected int getMaxLitterSize() { return maxLitterSize; }

    protected int getMaxHealth() { return maxHealth; }

    protected boolean isDiurnal() { return diurnal; }

    protected HashSet<Class> getFoodSources() { return foodSources; }

    protected HashSet<Class> getKillable() { return killable; }

    /**
     * A fluent builder so a subclass can declare its traits readably in one
     * place, e.g.
     * <pre>
     *   new AnimalTraits.Builder()
     *       .breedingAge(15).maxAge(70).breedingProbability(0.125)
     *       .maxLitterSize(4).maxHealth(40).diurnal(true)
     *       .foodSources(Monkey.class)
     *       .killable(Monkey.class, Plant.class)
     *       .build();
     * </pre>
     */
    public static class Builder
    {
        private int breedingAge;
        private int maxAge;
        private double breedingProbability;
        private int maxLitterSize;
        private int maxHealth;
        private boolean diurnal;
        private final HashSet<Class> foodSources = new HashSet<>();
        private final HashSet<Class> killable = new HashSet<>();

        public Builder breedingAge(int breedingAge)
        {
            this.breedingAge = breedingAge;
            return this;
        }

        public Builder maxAge(int maxAge)
        {
            this.maxAge = maxAge;
            return this;
        }

        public Builder breedingProbability(double breedingProbability)
        {
            this.breedingProbability = breedingProbability;
            return this;
        }

        public Builder maxLitterSize(int maxLitterSize)
        {
            this.maxLitterSize = maxLitterSize;
            return this;
        }

        public Builder maxHealth(int maxHealth)
        {
            this.maxHealth = maxHealth;
            return this;
        }

        public Builder diurnal(boolean diurnal)
        {
            this.diurnal = diurnal;
            return this;
        }

        public Builder foodSources(Class... classes)
        {
            for (Class c : classes) {
                foodSources.add(c);
            }
            return this;
        }

        public Builder killable(Class... classes)
        {
            for (Class c : classes) {
                killable.add(c);
            }
            return this;
        }

        public AnimalTraits build()
        {
            return new AnimalTraits(this);
        }
    }
}
