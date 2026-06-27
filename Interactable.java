/**
 * Represents an entity that can participate in predator-prey interactions.
 * When an Eater attempts to consume this entity, acceptInteraction is called.
 * The return value encodes the outcome:
 *   > 0  — consumed as food; caller should call setFoodLevel with this value
 *   == 0 — trampled (killed, no food gained)
 *   < 0  — no interaction
 */
public interface Interactable {
    boolean isAlive();
    int acceptInteraction(Eater eater);
}
