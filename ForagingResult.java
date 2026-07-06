/**
 * Immutable record of a single forage attempt.
 * ForagingService computes one of these; Consumer reads and applies it.
 */
class ForagingResult
{
    static final ForagingResult NO_FOOD = new ForagingResult(null, 0, false, null, false);

    /** Where the consumer should move. Null means no food was found. */
    final Location foodLocation;

    /** Sustenance to add to the consumer's current level (when clampSustenanceToMax is false). */
    final int sustenanceDelta;

    /**
     * When true the consumer's sustenance should be set to its own maximum rather than
     * incremented — applies to the prey-overfull case.
     */
    final boolean clampSustenanceToMax;

    /** Non-null when eating prey produced a leftover carcass. */
    final Carcass generatedCarcass;

    /** True when the consumer ate a diseased carcass. */
    final boolean contractedDisease;

    ForagingResult(Location foodLocation, int sustenanceDelta, boolean clampSustenanceToMax,
                   Carcass generatedCarcass, boolean contractedDisease)
    {
        this.foodLocation        = foodLocation;
        this.sustenanceDelta     = sustenanceDelta;
        this.clampSustenanceToMax = clampSustenanceToMax;
        this.generatedCarcass    = generatedCarcass;
        this.contractedDisease   = contractedDisease;
    }

    boolean found() { return foodLocation != null; }
}
