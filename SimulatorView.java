import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;


public class SimulatorView extends JFrame {

	private static final Color EMPTY_COLOR = Color.white;


	private static final Color UNKNOWN_COLOR = Color.gray;

	private final String STEP_PREFIX = "Step: ";
	private final String POPULATION_PREFIX = "Population: ";
	private final String DAYCYCLE_PREFIX = "Day cycle: ";
	private final String CLIMATE_PREFIX = "Season: ";
	private final String WEATHER_PREFIX = "Weather: ";
	private final String INFECTION_PREFIX = "Infection: ";
	private final String HUMIDITY_PREFIX = "Humidity: ";
	private JLabel stepLabel, populationLabel, dayCycleLabel, climateLabel, infectLabel, weatherLabel, humidityLabel;
	private FieldView fieldView;


	private Map<Class, Color> colors;

	private FieldStats stats;


	public SimulatorView(int height, int width) {
		stats = new FieldStats();
		colors = new LinkedHashMap<>();

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


	public void showStatus(int step, TimeCycle currentTimeCycle, Field field, Climate climate, int infect) {
		if (!isVisible()) {
			setVisible(true);
		}

		stepLabel.setText(STEP_PREFIX + step);
		dayCycleLabel.setText(DAYCYCLE_PREFIX + currentTimeCycle + " ");
		this.climateLabel.setText(CLIMATE_PREFIX + climate.getCurrentSeason());
		this.weatherLabel.setText(WEATHER_PREFIX + climate.getCurrentWeather());
		this.infectLabel.setText(INFECTION_PREFIX + infect + "%");
		this.humidityLabel.setText(HUMIDITY_PREFIX + climate.getHumidity() + "%");
		stats.reset();

		fieldView.preparePaint();


		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Animal animal = field.getAnimalAt(row, col);
				Plant plant = field.getPlantAt(row, col);
				if (animal != null) {
					stats.incrementCount(animal.getClass());
					fieldView.drawMark(col, row, animal.getObjectColor(climate));
				} else if (plant != null) {
					fieldView.drawMark(col, row, plant.getObjectColor(climate));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		stats.countFinished();

		populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
		fieldView.repaint();
	}


	public boolean isViable(Field field) {
		return stats.isViable(field);
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
