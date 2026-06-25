package views;

import java.awt.*;
import java.awt.event.*;
import java.util.ConcurrentModificationException;
import java.util.Set;

import javax.swing.*;

import controller.Simulator;
import controller.WeatherState;

import field.Entity;
import field.Field;
import field.FieldStatistics;
import field.Location;
import models.animal.AnimalFactory;
import models.plant.PlantFactory;
import utils.ThreadRunner;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "= Population = \n\n";
    private JLabel stepLabel, infoLabel;
    private JTextArea population;
    private FieldView fieldView;

    private Simulator simulator;

    private Field field;
    private WeatherState weatherState;
    private FieldStatistics fieldStatistics;

    private ThreadRunner threadRunner;

    private int rows, cols;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int rows, int cols, Simulator simulator)
    {
        setTitle("Modelling Population Dynamics with Stochastic Processes");
        setLocation(0, 0);

        this.rows = rows;
        this.cols = cols;
        this.simulator = simulator;

        field = Field.getInstance();
        weatherState = WeatherState.getInstance();
        fieldStatistics = FieldStatistics.getInstance();

        fieldView = new FieldView(rows, cols);

        threadRunner = new ThreadRunner(simulator);

        makeFrame();
    }

    /**
     * Make the frame of the view.
     */
    private void makeFrame() {

        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JTextArea(POPULATION_PREFIX, 20, 20);

        JPanel editPane = new JPanel();
        editPane.setLayout(new BoxLayout(editPane, BoxLayout.PAGE_AXIS));

        JButton startSimulation = new JButton("Start");
        editPane.add(startSimulation);

        JButton stopSimulation = new JButton("Stop");
        editPane.add(stopSimulation);

        JButton reset = new JButton("Reset");
        editPane.add(reset);

        JButton oneStep = new JButton("Simulate One Step");
        editPane.add(oneStep);

        JButton generateWeather = new JButton("(Re)generate weather randomly");
        editPane.add(generateWeather);

        startSimulation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                threadRunner.startRun();
            }
        });

        stopSimulation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                threadRunner.stop();
            }
        });

        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                threadRunner.stop();
                simulator.reset();
            }
        });

        oneStep.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (threadRunner.getRunning()) {
                    return;
                }
                
                simulator.simulateStep();
            }
        });

        generateWeather.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.generateWeather();
            }
        });

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        };

        addWindowListener(exitListener);

        JPanel populationPanel = new JPanel();

        populationPanel.setLayout(new GridLayout(1, 2));

        JPanel legendPanel = new JPanel();

        makeLegend(legendPanel);

        populationPanel.add(population);
        populationPanel.add(legendPanel);
        
        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);

        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(populationPanel, BorderLayout.SOUTH);
        contents.add(editPane, BorderLayout.EAST);

        pack();
        setVisible(true);
    }

    /**
     * Make the legend panel
     * @param legendPanel the legend panel
     */
    private void makeLegend(JPanel legendPanel) {
        Set<String> strings = fieldStatistics.getStatistics().keySet();

        BoxLayout flowLayout = new BoxLayout(legendPanel, BoxLayout.PAGE_AXIS);
        legendPanel.setLayout(flowLayout);
        
        for (String s : strings) {
            Location temp = new Location(0, 0);
            Entity e = AnimalFactory.get(s, temp);

            if (e == null) {
                e = PlantFactory.get(s, temp);
            }

            if (e == null) {
                System.out.println("Invalid species in PieChart makeLegend");
                continue;
            }
            
            JLabel label = new JLabel(e.getID());
            int[] rgb = e.getRgbColour();
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            label.setOpaque(true);
            label.setBackground(color);

            String[] whiteNames = {"wolf", "phoenix", "cactus", "elk", "slime", "gnome", "dragon"};

            for (String c : whiteNames) {
                if (e.getID().equals(c)) {
                    label.setForeground(Color.white);
                    break;
                }
            }

            legendPanel.add(label);
        }
    }


    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     */
    public void showStatus()
    {
        int step = field.getStep();

        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step + " " + field.getDayState() + " " + weatherState.getWeather());
        // stats.reset();
        
        fieldView.preparePaint(fieldView);

        for(int row = 0; row < field.getRows(); row++) {
            for(int col = 0; col < field.getCols(); col++) {
                Entity entity = field.getBlockAt(row, col).getEntity();
                if(entity != null) {
                    //stats.incrementCount(animal.getClass());
                    int[] rgbColour = entity.getRgbColour();
                    //System.out.println(rgbColour[0]);
                    Color c = new Color(rgbColour[0], rgbColour[1], rgbColour[2]);
                    fieldView.drawMark(col, row, c);
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        //stats.countFinished();
        
        population.setText(POPULATION_PREFIX + fieldStatistics.getDetails());
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    // public boolean isViable(Field field)
    // {
    //     return stats.isViable(field);
    // }
}
