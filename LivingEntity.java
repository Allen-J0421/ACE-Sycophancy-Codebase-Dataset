import java.util.List;

/**
 * Common lifecycle interface for all entities in the simulation.
 * Allows animals and plants to be managed in a single polymorphic collection.
 * Default implementations are no-ops so that each subtype only overrides
 * the behaviours that apply to it.
 */
public interface LivingEntity {
    void act(List<LivingEntity> newEntities, int time);
    boolean isAlive();
    void setDead();

    /** Apply the named weather condition to this entity (e.g. "fog", "rain"). */
    default void applyWeatherEffect(String weather) {}

    /** Reset any weather state carried by this entity. */
    default void resetWeatherEffects() {}

    /** Returns true if this entity is killed by flood events (Ant, Rat). */
    default boolean isFloodVulnerable() { return false; }

    /** Returns true if this entity is killed by drought events (all plants). */
    default boolean isDroughtVulnerable() { return false; }

    /** Potentially give this entity a disease. */
    default void giveDisease() {}

    /** Clear any disease state carried by this entity. */
    default void resetDisease() {}
}
