package views;

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import field.Entity;
import field.FieldStatistics;
import field.Location;
import models.animal.AnimalFactory;
import models.plant.PlantFactory;

/**
 * Data model for a single slice of the pie chart
 */
class Slice {
    double value;
    Color color;

    public Slice(double value, Color color) {
        this.value = value;
        this.color = color;
    }
}

/**
 * The Pie Chart.
 * Adapted from http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/DrawingaPieChart.htm
 */
class MyPieChart extends JComponent {
    private List<Slice> slices;

    MyPieChart(List<Slice> slices) {
        this.slices = slices;
    }

    public void paint(Graphics g) {
        drawPie((Graphics2D) g, getBounds(), slices);
    }

    void drawPie(Graphics2D g, Rectangle area, List<Slice> slices) {
        double total = 0.0D;
        for (int i = 0; i < slices.size(); i++) {
            total += slices.get(i).value;
        }

        double curValue = 0.0D;
        int startAngle = 0;
        for (int i = 0; i < slices.size(); i++) {
            startAngle = (int) (curValue * 360 / total);
            int arcAngle = (int) (slices.get(i).value * 360 / total);

            g.setColor(slices.get(i).color);
            g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
            curValue += slices.get(i).value;
        }
    }
}

/**
 * The Frame holding the Pie Chart.
 *
 */
public class PieChart extends JFrame {
    private List<Slice> slices;
    private FieldStatistics fieldStatistics;
    private MyPieChart pieChart;

    /**
     * Constructor for PieChart
     */
    public PieChart() {
        setTitle("Pie Chart");
        

        fieldStatistics = FieldStatistics.getInstance();
        slices = new ArrayList<>();

        updateSlices();

        pieChart = new MyPieChart(slices);
        getContentPane().add(pieChart, BorderLayout.CENTER);

        pack();

        setSize(300, 300);

        // https://stackoverflow.com/questions/21169487/how-to-position-jframe-to-top-right-of-screen-upon-start
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - getWidth();
        int y = 0;
        setLocation(x, y);

        setVisible(true);
    }

    /**
     * Update the values for each slice of the pie chart.
     */
    private void updateSlices() {
        slices.clear();
        Map<String, Integer> m = fieldStatistics.getStatistics();

        for (String s : m.keySet()) {
            Location temp = new Location(0, 0);
            Entity e = AnimalFactory.get(s, temp);

            if (e == null) {
                e = PlantFactory.get(s, temp);
            }

            if (e == null) {
                System.out.println("Invalid species in PieChart updateSlices");
                continue;
            }

            int[] rgb = e.getRgbColour();
            Color color = new Color(rgb[0], rgb[1], rgb[2]);

            slices.add(new Slice(m.get(s), color));
        }
    }

    /**
     * Repaint the pie chart.
     */
    public void showStatus() {
        if(!isVisible()) {
            setVisible(true);
        }

        updateSlices();
        pieChart.repaint();
    }
}
