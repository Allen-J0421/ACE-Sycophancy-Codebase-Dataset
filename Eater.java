/**
 * Represents an entity that can consume other entities.
 * Each method returns the food value gained from consuming that prey type,
 * or -1 if this eater does not consume that type.
 * Default implementations return -1 (no interaction).
 */
public interface Eater {
    default int eatAnt(Ant ant)         { return -1; }
    default int eatRat(Rat rat)         { return -1; }
    default int eatSnake(Snake snake)   { return -1; }
    default int eatGrass(Grass grass)   { return -1; }
    default int eatAcacia(Acacia acacia){ return -1; }
    default boolean tramplesPlants()    { return false; }
}
