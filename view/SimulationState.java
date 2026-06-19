package view;

import model.Climate;
import model.Field;
import model.TimeCycle;


/**
 * An immutable snapshot of the simulation at one step, published to every
 * {@link SimulationView}. Bundling the data into one value keeps the view
 * contract stable as the simulation grows new things to report.
 */
public record SimulationState(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage) {
}
