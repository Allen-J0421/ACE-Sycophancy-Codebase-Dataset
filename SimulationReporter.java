/**
 * Bridges the simulation engine and the display layer.
 * Implements {@link SimulationObserver} so it receives raw step events from the
 * engine; on each event it aggregates population and disease statistics into a
 * {@link FieldStats} snapshot, then hands the snapshot to the {@link SimulationDisplay}
 * for rendering. Viability queries are answered from the same stats object.
 *
 * @version 2022.03.02
 */
public class SimulationReporter implements SimulationObserver
{
    private final SimulationDisplay display;
    private final FieldStats stats;

    /**
     * @param display The display that will render each step after stats are computed.
     */
    public SimulationReporter(SimulationDisplay display)
    {
        this.display = display;
        this.stats = new FieldStats();
    }

    /**
     * Aggregate population and disease counts for the current field state,
     * then forward the results to the display for rendering.
     */
    @Override
    public void onStep(int step, Environment environment, Field field)
    {
        stats.reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if (actor != null) {
                    stats.incrementCount(actor.getClass());
                    if (actor instanceof Organism && ((Organism) actor).isDiseased()) {
                        stats.incrementDiseasedCount();
                    }
                }
            }
        }
        stats.countFinished();
        display.render(step, environment, field, stats);
    }

    /**
     * Returns true if the population is still viable.
     * Uses the counts from the most recent {@link #onStep} call, so no extra
     * field scan is needed.
     */
    @Override
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
}
