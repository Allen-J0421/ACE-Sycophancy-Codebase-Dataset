import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Swing-backed display for the simulation.
 *
 * @version 2022.03.02
 */
public class SwingSimulationView extends JFrame implements SimulationDisplay
{
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final Map<Species, Color> colors;
    private final FieldStats stats;
    private final SimulationRulesEngine rules;
    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel infoLabel;
    private final FieldView fieldView;

    public SwingSimulationView(int height, int width, SimulationRulesEngine rules)
    {
        this.rules = rules;
        stats = new FieldStats(rules);
        colors = new LinkedHashMap<>();

        setTitle("Animal Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(0, 1));

        JButton sunnyButton = new JButton("Set Weather to Sunny");
        sunnyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { setSunny(); }
            });
        toolbar.add(sunnyButton);

        JButton rainyButton = new JButton("Set Weather to Rainy");
        rainyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { setRainy(); }
            });
        toolbar.add(rainyButton);

        JButton foggyButton = new JButton("Set Weather to Foggy");
        foggyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { setFoggy(); }
            });
        toolbar.add(foggyButton);

        setLocation(100, 50);
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(toolbar, BorderLayout.WEST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    public void setColor(Species species, DisplayColor color)
    {
        colors.put(species, new Color(color.getRed(), color.getGreen(), color.getBlue()));
    }

    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    public void showStatus(int step, FieldSnapshot snapshot)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        fieldView.preparePaint();

        for(int row = 0; row < snapshot.getDepth(); row++) {
            for(int col = 0; col < snapshot.getWidth(); col++) {
                Species species = snapshot.getSpeciesAt(row, col);
                if(species != null) {
                    fieldView.drawMark(col, row, getColor(species));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(snapshot, step));
        fieldView.repaint();
    }

    public boolean isViable(FieldSnapshot snapshot)
    {
        return stats.isViable(snapshot);
    }

    private void setSunny()
    {
        rules.setWeather("Sunny");
    }

    private void setRainy()
    {
        rules.setWeather("Rainy");
    }

    private void setFoggy()
    {
        rules.setWeather("Foggy");
    }

    private Color getColor(Species species)
    {
        Color color = colors.get(species);
        return color == null ? UNKNOWN_COLOR : color;
    }

    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        private Dimension size;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        public void preparePaint()
        {
            if(! size.equals(getSize())) {
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    g.drawImage(fieldImage, 0, 0,
                                currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
