package models.animal.behaviour.abilities;

/**
 * Interface for an ability.
 * Abilities are implemented as a Decorator desing pattern.
 *
 */
public interface Ability {
    /**
     * What the ability does.
     */
    void act();
}
