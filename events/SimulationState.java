package events;

import model.Climate;
import model.Field;
import model.TimeCycle;


/**
 * An immutable snapshot of the simulation at one step, carried by a
 * {@link SimulationEvent.StatusUpdated} event. Bundling the data into one value
 * keeps the event contract stable as the simulation grows new things to report.
 */
public record SimulationState(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage) {
}
