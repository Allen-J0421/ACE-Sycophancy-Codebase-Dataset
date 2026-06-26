/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Gender enumeration defining all possible genders in the simulation.
 *
 * @version 2022.03.02
 */
public enum Gender {
    MALE,
    FEMALE;

    private static final Gender[] VALUES = values();

    /**
     * Returns a random Gender from all possible choices.
     *
     * @return A Gender.
     */
    public static Gender getRandom() {
        return VALUES[Randomizer.getRandom().nextInt(VALUES.length)];
    }
}
