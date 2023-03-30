package fr.alanguenegou.prd.prdapp.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import fr.alanguenegou.prd.prdapp.userdata.Trip;
import org.javatuples.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import javax.swing.*;

/**
 * This class represents a visualization of a graph and its associated trips.
 * It extends the JFreeChart ApplicationFrame.
 */
public class Visualization extends ApplicationFrame {

    /**
     * A list of 10 colors to display trips
     */
    static Color[] colors = {
            Color.GRAY,
            Color.LIGHT_GRAY,
            Color.DARK_GRAY,
            Color.YELLOW,
            Color.ORANGE,
            Color.PINK,
            Color.MAGENTA,
            Color.CYAN,
            Color.BLUE,
            Color.GREEN
    };

    /**
     * A collection of XYSeries representing the data to be displayed in the chart.
     */
    private final XYSeriesCollection dataset;

    /**
     * A renderer for the chart.
     */
    private final XYLineAndShapeRenderer renderer;

    /**
     * The domain axis for the chart.
     */
    private final NumberAxis domain;

    /**
     * The range axis for the chart.
     */
    private final NumberAxis range;


    /**
     * Constructs a new Visualization object with default settings.
     */
    public Visualization() {
        super("Graph Visualization");
        dataset = new XYSeriesCollection();
        renderer = new XYLineAndShapeRenderer();

        // Create a scatter plot with default settings
        JFreeChart chart = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();

        // Set the renderer, dataset order, and background color for the plot
        plot.setRenderer(renderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        plot.setBackgroundPaint(Color.WHITE);

        // Set the range and domain axes for the plot
        domain = (NumberAxis) plot.getDomainAxis();
        domain.setRange(0.43, 0.8);
        domain.setVerticalTickLabels(true);
        range = (NumberAxis) plot.getRangeAxis();
        range.setRange(47.26, 47.51);

        // Hide the axis lines, tick marks, and labels
        domain.setAxisLineVisible(Boolean.FALSE);
        domain.setTickLabelsVisible(Boolean.FALSE);
        domain.setTickMarksVisible(Boolean.FALSE);
        range.setAxisLineVisible(Boolean.FALSE);
        range.setTickLabelsVisible(Boolean.FALSE);
        range.setTickMarksVisible(Boolean.FALSE);

        // Create a new ChartPanel with the chart and set some mouse options
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMouseZoomable(true);
        chartPanel.setFillZoomRectangle(true);
        setContentPane(chartPanel);
    }

    /**
     * Adds a graph to the visualization.
     *
     * @param graph the Graph to add to the visualization
     */
    public void addGraph(Graph graph) {
        XYSeries series = new XYSeries("Nodes");

        // Add each node in the graph to the series
        graph.getNodes().forEach((key,value) -> series.add(value.getCoordX(), value.getCoordY()));
        dataset.addSeries(series);

        // Customize the appearance of the nodes in the chart
        renderer.setSeriesShapesVisible(dataset.getSeriesIndex("Nodes"), true);
        renderer.setSeriesShape(dataset.getSeriesIndex("Nodes"), new Ellipse2D.Double(-1, -1, 2, 2));
        renderer.setSeriesLinesVisible(dataset.getSeriesIndex("Nodes"), false);
    }

    /**
     * Adds multiple trips to the visualization.
     * @param trips an array of Trip objects to add to the visualization
     */
    public void addTrips(Trip[] trips){
        for (int i = 0; i < trips.length; i++){
            addTrip(trips[i], colors[i % trips.length], i);
        }
    }

    /**
     * Adds a trip to the visualization.
     *
     * @param trip  the trip to add
     * @param color the color to use for the trip
     * @param index the index of the trip in the visualization
     */
    public void addTrip(Trip trip, Color color, int index) {
        // For every node in the trip
        for (int i = 0; i < trip.getTrip().size() - 1; i++) {
            // Get the current and next node and add them to a series
            XYSeries series = new XYSeries("Line" + index + "-" + i);
            Node node = trip.getTrip().get(i);
            Node node2 = trip.getTrip().get(i + 1);
            series.add(node.getCoordX(), node.getCoordY());
            series.add(node2.getCoordX(), node2.getCoordY());

            // Render a line between the two nodes
            dataset.addSeries(series);
            renderer.setSeriesPaint(dataset.getSeriesCount() - 1, color);
            renderer.setSeriesStroke(dataset.getSeriesCount() - 1, new BasicStroke(2.0f));
            renderer.setSeriesShape(dataset.getSeriesCount() - 1, new Ellipse2D.Double(-1, -1, 2, 2));
            renderer.setSeriesShapesVisible(dataset.getSeriesCount() - 1, true);
            renderer.setSeriesLinesVisible(dataset.getSeriesCount() - 1, true);
        }
    }

    /**
     * Adds multiple sections to the visualization
     * @param sections a map of sections to add to the visualization
     * @param graph the Graph associated with the sections
     */
    public void addSections(HashMap<Long, Integer> sections, Graph graph){
        // for every section in the HasMap
        sections.forEach((id,value) -> {
            // Get the start and end nodes and add them to a series
            XYSeries series = new XYSeries("Line" + id);
            Node node = graph.getNodeStartBySection(Pair.with(id,true));
            Node node2 = graph.getNodeEndBySection(Pair.with(id,true));
            series.add(node.getCoordX(), node.getCoordY());
            series.add(node2.getCoordX(), node2.getCoordY());

            // Render a line between the two nodes
            dataset.addSeries(series);
            renderer.setSeriesPaint(dataset.getSeriesCount() - 1, Color.RED);
            renderer.setSeriesStroke(dataset.getSeriesCount() - 1, new BasicStroke(2.0f));
            renderer.setSeriesShape(dataset.getSeriesCount() - 1, new Ellipse2D.Double(-1, -1, 2, 2));
            renderer.setSeriesShapesVisible(dataset.getSeriesCount() - 1, true);
            renderer.setSeriesLinesVisible(dataset.getSeriesCount() - 1, true);
        });
    }

    /**
     * Displays the visualization with a "Reset Zoom" button.
     */
    public void display() {
        // Create a reset button to go back to the original view
        JButton resetButton = new JButton("Reset Zoom");
        resetButton.addActionListener(e -> {
            domain.setRange(0.43, 0.8);
            range.setRange(47.26, 47.51);
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }
}
