import javax.swing.*;
import java.awt.*;


public class SimulatorView extends JFrame implements SimulationObserver {

	private static final Color EMPTY_COLOR = Color.white;

	private final String STEP_PREFIX = "Step: ";
	private final String POPULATION_PREFIX = "Population: ";
	private final String DAYCYCLE_PREFIX = "Day cycle: ";
	private final String CLIMATE_PREFIX = "Season: ";
	private final String WEATHER_PREFIX = "Weather: ";
	private final String INFECTION_PREFIX = "Infection: ";
	private final String HUMIDITY_PREFIX = "Humidity: ";
	private JLabel stepLabel, populationLabel, dayCycleLabel, climateLabel, infectLabel, weatherLabel, humidityLabel;
	private FieldView fieldView;

	private FieldStats stats;


	public SimulatorView(int height, int width) {
		stats = new FieldStats();

		setTitle("Predator-Prey Simulation");
		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
		dayCycleLabel = new JLabel(DAYCYCLE_PREFIX, JLabel.CENTER);
		climateLabel = new JLabel(CLIMATE_PREFIX, JLabel.CENTER);
		weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
		infectLabel = new JLabel(INFECTION_PREFIX, JLabel.CENTER);
		humidityLabel = new JLabel(HUMIDITY_PREFIX, JLabel.CENTER);

		setLocation(100, 50);

		fieldView = new FieldView(height, width);

		Container contents = getContentPane();

		JPanel infoPane = new JPanel(new FlowLayout());
		JPanel detailPane = new JPanel(new BorderLayout());

		infoPane.add(stepLabel);
		infoPane.add(dayCycleLabel);
		infoPane.add(climateLabel);
		infoPane.add(weatherLabel);
		infoPane.add(humidityLabel);
		infoPane.add(infectLabel);
		contents.add(infoPane, BorderLayout.NORTH);
		contents.add(fieldView, BorderLayout.CENTER);
		contents.add(detailPane, BorderLayout.SOUTH);
		detailPane.add(populationLabel, BorderLayout.CENTER);

		pack();
		setVisible(true);
	}


	@Override
	public void onSimulationStateChanged(SimulationSnapshot snapshot) {
		if (!isVisible()) {
			setVisible(true);
		}

		FieldEnvironment field = snapshot.getField();
		Climate climate = snapshot.getClimate();
		stepLabel.setText(STEP_PREFIX + snapshot.getStep());
		dayCycleLabel.setText(DAYCYCLE_PREFIX + snapshot.getTimeCycle() + " ");
		this.climateLabel.setText(CLIMATE_PREFIX + climate.getCurrentSeason());
		this.weatherLabel.setText(WEATHER_PREFIX + climate.getCurrentWeather());
		this.infectLabel.setText(INFECTION_PREFIX + snapshot.getSickPercentage() + "%");
		this.humidityLabel.setText(HUMIDITY_PREFIX + climate.getHumidity() + "%");
		stats.reset();

		fieldView.preparePaint();

		field.streamLocations().forEach(location -> drawLocation(field, climate, location));
		stats.countFinished();

		populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
		fieldView.repaint();
	}


	private void drawLocation(FieldEnvironment field, Climate climate, Location location) {
		Animal animal = field.getAnimalAt(location);
		Plant plant = field.getPlantAt(location);
		if (animal != null) {
			stats.incrementCount(animal.getClass());
			fieldView.drawMark(location.getCol(), location.getRow(), animal.getObjectColor(climate));
		} else if (plant != null) {
			fieldView.drawMark(location.getCol(), location.getRow(), plant.getObjectColor(climate));
		} else {
			fieldView.drawMark(location.getCol(), location.getRow(), EMPTY_COLOR);
		}
	}


	private class FieldView extends JPanel {
		private final int GRID_VIEW_SCALING_FACTOR = 5;
		Dimension size;
		private int gridWidth, gridHeight;
		private int xScale, yScale;
		private Graphics g;
		private Image fieldImage;


		public FieldView(int height, int width) {
			gridHeight = height;
			gridWidth = width;
			size = new Dimension(0, 0);
		}


		public Dimension getPreferredSize() {
			return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
					gridHeight * GRID_VIEW_SCALING_FACTOR);
		}


		public void preparePaint() {
			if (!size.equals(getSize())) {
				size = getSize();
				fieldImage = fieldView.createImage(size.width, size.height);
				g = fieldImage.getGraphics();

				xScale = size.width / gridWidth;
				if (xScale < 1) {
					xScale = GRID_VIEW_SCALING_FACTOR;
				}
				yScale = size.height / gridHeight;
				if (yScale < 1) {
					yScale = GRID_VIEW_SCALING_FACTOR;
				}
			}
		}


		public void drawMark(int x, int y, Color color) {
			g.setColor(color);
			g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
			g.drawRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
		}


		public void paintComponent(Graphics g) {
			if (fieldImage != null) {
				Dimension currentSize = getSize();
				if (size.equals(currentSize)) {
					g.drawImage(fieldImage, 0, 0, null);
				} else {

					g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
				}
			}
		}
	}
}
