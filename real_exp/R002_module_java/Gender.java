import java.util.Random;

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

    /**
     * Returns a random Gender from all possible choices.
     *
     * @return A Gender.
     */
    public static Gender getRandom() {
        Gender[] values = Gender.values();
        int randomIndex = new Random().nextInt(values.length);
        return values[randomIndex];
    }
}
