import javax.swing.*;
import java.awt.*;


public final class SimulationStatusPanel {

	private static final String STEP_PREFIX = "Step: ";
	private static final String POPULATION_PREFIX = "Population: ";
	private static final String DAYCYCLE_PREFIX = "Day cycle: ";
	private static final String CLIMATE_PREFIX = "Season: ";
	private static final String WEATHER_PREFIX = "Weather: ";
	private static final String INFECTION_PREFIX = "Infection: ";
	private static final String HUMIDITY_PREFIX = "Humidity: ";

	private final JPanel statusPanel;
	private final JPanel populationPanel;
	private final JLabel stepLabel;
	private final JLabel populationLabel;
	private final JLabel dayCycleLabel;
	private final JLabel climateLabel;
	private final JLabel infectLabel;
	private final JLabel weatherLabel;
	private final JLabel humidityLabel;


	public SimulationStatusPanel() {
		statusPanel = new JPanel(new FlowLayout());
		populationPanel = new JPanel(new BorderLayout());

		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
		dayCycleLabel = new JLabel(DAYCYCLE_PREFIX, JLabel.CENTER);
		climateLabel = new JLabel(CLIMATE_PREFIX, JLabel.CENTER);
		weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
		infectLabel = new JLabel(INFECTION_PREFIX, JLabel.CENTER);
		humidityLabel = new JLabel(HUMIDITY_PREFIX, JLabel.CENTER);

		statusPanel.add(stepLabel);
		statusPanel.add(dayCycleLabel);
		statusPanel.add(climateLabel);
		statusPanel.add(weatherLabel);
		statusPanel.add(humidityLabel);
		statusPanel.add(infectLabel);
		populationPanel.add(populationLabel, BorderLayout.CENTER);
	}


	public JPanel getStatusPanel() {
		return statusPanel;
	}


	public JPanel getPopulationPanel() {
		return populationPanel;
	}


	public void updateStatus(int step, TimeCycle timeCycle, Climate climate, int infectionPercentage) {
		stepLabel.setText(STEP_PREFIX + step);
		dayCycleLabel.setText(DAYCYCLE_PREFIX + timeCycle + " ");
		climateLabel.setText(CLIMATE_PREFIX + climate.getCurrentSeason());
		weatherLabel.setText(WEATHER_PREFIX + climate.getCurrentWeather());
		infectLabel.setText(INFECTION_PREFIX + infectionPercentage + "%");
		humidityLabel.setText(HUMIDITY_PREFIX + climate.getHumidity() + "%");
	}


	public void updatePopulation(String populationDetails) {
		populationLabel.setText(POPULATION_PREFIX + populationDetails);
	}
}
