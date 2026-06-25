package models.animal.behaviour.breeding;

/**
 * Enumeration containing the possible sexes.
 * They are implemented as booleans for easy checking of
 * opposite sex.
 *
 */
public enum Sex {
    MALE(true), FEMALE(false);

    private final boolean bool;
    Sex(boolean bool) {
        this.bool = bool;
    }

    public boolean getBool() {
        return bool;
    }
}
