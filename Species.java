/**
 * Encapsulates the species-specific configuration used when populating the
 * field. Each species knows its own creation probability and how to create a
 * new instance of itself, keeping spawn configuration alongside the species it
 * describes instead of in parallel arrays and an if-else chain in the Simulator.
 *
 * The declaration order defines the order species are considered during
 * population.
 *
 * @version (27/06/2026)
 */
public enum Species
{
    DODO(0.5, (field, location, infected) -> new Dodo(true, field, location, infected)),
    HUMAN(0.001, (field, location, infected) -> new Human(true, field, location, infected)),
    PIG(0.06, (field, location, infected) -> new Pig(true, field, location, infected)),
    MONKEY(0.04, (field, location, infected) -> new Monkey(true, field, location, infected)),
    TORTOISE(0.008, (field, location, infected) -> new Tortoise(true, field, location, infected));

    /**
     * Creates a new animal of a particular species at a location.
     */
    @FunctionalInterface
    private interface Spawner
    {
        Animal spawn(Field<Actor> field, Location location, boolean infected);
    }

    // The probability this species is created at any given grid position.
    private final double creationProbability;
    // Factory that creates a new, randomly-aged instance of this species.
    private final Spawner spawner;

    Species(double creationProbability, Spawner spawner)
    {
        this.creationProbability = creationProbability;
        this.spawner = spawner;
    }

    /**
     * @return The probability this species is created at any given grid position.
     */
    public double getCreationProbability()
    {
        return creationProbability;
    }

    /**
     * Create a new, randomly-aged animal of this species.
     *
     * @param field    The field the animal is placed in.
     * @param location The location within the field.
     * @param infected Whether the new animal starts infected.
     * @return The newly created animal.
     */
    public Animal create(Field<Actor> field, Location location, boolean infected)
    {
        return spawner.spawn(field, location, infected);
    }
}
