package safari;

/**
 * A single registry entry for population seeding.
 */
record SpawnRule(
    ActorKind kind,
    double probability
)
{
}
