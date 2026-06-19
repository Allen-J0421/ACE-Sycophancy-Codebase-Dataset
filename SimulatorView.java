import javax.swing.*;
import java.awt.*;


public class SimulatorView extends JFrame {

	private static final Color EMPTY_COLOR = Color.white;


	private final SimulationStatusPanel statusPanel;

	private final FieldView fieldView;

	private final FieldStats stats;


	public SimulatorView(int height, int width) {
		stats = new FieldStats();
		statusPanel = new SimulationStatusPanel();

		setTitle("Predator-Prey Simulation");

		setLocation(100, 50);

		fieldView = new FieldView(height, width);

		Container contents = getContentPane();

		contents.add(statusPanel.getStatusPanel(), BorderLayout.NORTH);
		contents.add(fieldView, BorderLayout.CENTER);
		contents.add(statusPanel.getPopulationPanel(), BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}


	public void showStatus(int step, TimeCycle currentTimeCycle, Field field, Climate climate, int infectionPercentage) {
		if (!isVisible()) {
			setVisible(true);
		}

		statusPanel.updateStatus(step, currentTimeCycle, climate, infectionPercentage);
		stats.clearCounts();

		fieldView.preparePaint();


		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Animal animal = field.getAnimalAt(row, col);
				Plant plant = field.getPlantAt(row, col);
				if (animal != null) {
					stats.countAnimal(animal.getClass());
					fieldView.drawMark(col, row, animal.getObjectColor(climate));
				} else if (plant != null) {
					fieldView.drawMark(col, row, plant.getObjectColor(climate));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		statusPanel.updatePopulation(stats.getPopulationDetails());
		fieldView.repaint();
	}


	public boolean isViable(Field field) {
		stats.countField(field);
		return stats.isViable();
	}


	private class FieldView extends JPanel {
		private final int GRID_VIEW_SCALING_FACTOR = 5;
		private Dimension size;
		private final int gridWidth;
		private final int gridHeight;
		private int xScale, yScale;
		private Graphics g;
		private Image fieldImage;


		public FieldView(int height, int width) {
			gridHeight = height;
			gridWidth = width;
			size = new Dimension(0, 0);
		}


		@Override
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


		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
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
