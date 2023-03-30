package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Graph;


import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.*;

/**
 * The class that represents a user trip retrieved from the user data database
 * @author GUENEGOU A.
 * @version 1.00
 */
public class Trip {

    /**
     * The ID of this Trip
     */
    @Getter @Setter private int id;

    /**
     * A list of section IDs that represents this Trip in terms of sections (a section is two linked nodes)
     */
    @Getter @Setter
    private LinkedList<Long> sections = new LinkedList<>();

    /**
     * A list of nodes that represent this Trip in terms of nodes
     */
    @Getter @Setter
    private LinkedList<Node> trip = new LinkedList<>();

    /**
     * A pair of numerical values that represents the weights of distance and danger for this particular user Trip
     */
    @Getter @Setter
    private Pair<Double, Double> deducedWeightsValues;


    /**
     * The class constructor
     * @param id The ID of this new Trip
     * @param trip The list of nodes that constitute this Trip
     */
    public Trip(int id, LinkedList<Node> trip) {
        this.setId(id);
        this.setTrip(trip);
    }


    /**
     * Adds a specific node to this Trip
     * @param node The node that has to be added
     */
    public void addNode(Node node){
        trip.add(node);
    }


    /**
     * Computes the distance and danger values of this entire Trip
     * @return The distance and danger values
     */
    public Pair<Double, Double> getTripValues() {
        double totalDistance = 0;
        double totalDanger = 0;

        // for each node in the trip, gets the values of the section between the node and the next one in the trip
        for (int i = 0; i < trip.size()-1; i++) {
            totalDistance += trip.get(i).getAdjacentNodes().get(trip.get(i+1)).getValue0();
            totalDanger += trip.get(i).getAdjacentNodes().get(trip.get(i+1)).getValue1();
        }
        return Pair.with(totalDistance, totalDanger);
    }


    /**
     * Gets the starting node of this Trip
     * @return The starting node
     */
    public Node getStartNode() {
        return trip.get(0);
    }


    /**
     * Gets the ending node of this Trip
     * @return The ending node
     */
    public Node getEndNode() {
        return trip.get(trip.size()-1);
    }

    /**
     * Compares this real user trip to its calculated version
     * @param graph The graph in which the trip is located
     * @return The percent variation from the closest label in pareto front to this user trip
     */
    public Double compareTripWithCalculatedVersion(Graph graph) {
        Pair<Double, Double> tripValues = getTripValues();

        // HashMap<DistanceWeight, Pair<DistanceValue, DangerValue>>
        HashMap<Double, Pair<Double, Double>> calculatedLabels = graph.calculateLabelsForManyLinearCombinations(getStartNode(), getEndNode(), Graph.WITH_INITIAL_DANGER_VALUE);

        // computedDifferences is Hashmap<computedDifference, distanceWeight>
        HashMap<Double, Double> computedDifferences = new HashMap<>();

        // iterates through every label of the artificial pareto front
        for (Map.Entry<Double, Pair<Double, Double>> calculatedLabel : calculatedLabels.entrySet()) {
            Double calculatedDistance = calculatedLabel.getValue().getValue0();
            Double calculatedDanger = calculatedLabel.getValue().getValue1();

            // use of euclidean distance
            Double userTripToCalculatedLabelEuclideanDistance = Math.sqrt(
                    Math.pow(calculatedDistance - tripValues.getValue0(), 2)
                            + Math.pow(calculatedDanger - tripValues.getValue1(), 2));

            computedDifferences.put(userTripToCalculatedLabelEuclideanDistance, calculatedLabel.getKey());
        }

        // determine the closest label (of pareto front) to real user trip
        Double lengthFromClosestLabel = Collections.min(computedDifferences.keySet());

        /*
        we deduced the distance weight that had been used in this trip, so we can set the deducedWeightsValues attribute of the trip
         */
        Double deducedTripDistanceWeight = computedDifferences.get(lengthFromClosestLabel);
        setDeducedWeightsValues(Pair.with(deducedTripDistanceWeight, 1-deducedTripDistanceWeight));

        /*
        following part is about computing the percent variation from the closest label to the user trip label
        we normalize the coordinate system with the extreme linear combinations labels (0.999,0.001) and (0.001,0.999)
        /!\ (defined by the constant Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS).
         */
        double closestLabelDistance = calculatedLabels
                .get(computedDifferences.get(lengthFromClosestLabel))
                .getValue0();

        double closestLabelDanger = calculatedLabels
                .get(computedDifferences.get(lengthFromClosestLabel))
                .getValue1();

        double tripDistanceWeight = getDeducedWeightsValues().getValue0();
        double tripDangerWeight = getDeducedWeightsValues().getValue1();

        double userCost = tripDistanceWeight * tripValues.getValue0() / calculatedLabels.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0()
                + tripDangerWeight * tripValues.getValue1() / calculatedLabels.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1();

        double closestLabelCost = tripDistanceWeight * closestLabelDistance / calculatedLabels.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0()
                + tripDangerWeight * closestLabelDanger / calculatedLabels.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1();

        if (Double.isNaN(userCost) || Double.isInfinite(userCost)) {
            return 0.0;
        }

        if (getId() <= 45){
            System.out.println("Id = " + getId() + ", difference = " + ((userCost - closestLabelCost) / closestLabelCost * 100) + ", userCost : " + userCost + ", closestLabelCost : " + closestLabelCost);

        }
        return (userCost - closestLabelCost) / closestLabelCost * 100;
    }

    /**
     * Deduces the distance and danger weights chosen by the user who did this trip and sets the proper values of this trip
     * @param graph The Tours graph instance
     * @return The HashMap containing the artificial pareto front of the trip shortest-path computation
     */
    public HashMap<Double, Pair<Double, Double>> setTripWeightsThanksToComparison(Graph graph) {
        Pair<Double, Double> tripValues = getTripValues();

        // HashMap<DistanceWeight, Pair<DistanceValue, DangerValue>>
        HashMap<Double, Pair<Double, Double>> calculatedLabels = graph.calculateLabelsForManyLinearCombinations(getStartNode(), getEndNode(), Graph.WITH_INITIAL_DANGER_VALUE);

        // computedDifferences is Hashmap<computedDifference, distanceWeight>
        HashMap<Double, Double> computedDifferences = new HashMap<>();

        // iterates through every label of the artificial pareto front
        for (Map.Entry<Double, Pair<Double, Double>> calculatedLabel : calculatedLabels.entrySet()) {
            Double calculatedDistance = calculatedLabel.getValue().getValue0();
            Double calculatedDanger = calculatedLabel.getValue().getValue1();

            // use of euclidean distance
            Double userTripToCalculatedLabelEuclideanDistance = Math.sqrt(
                    Math.pow(calculatedDistance - tripValues.getValue0(), 2)
                    + Math.pow(calculatedDanger - tripValues.getValue1(), 2));

            computedDifferences.put(userTripToCalculatedLabelEuclideanDistance, calculatedLabel.getKey());
        }

        // determine the closest label (of pareto front) to real user trip
        Double lengthFromClosestLabel = Collections.min(computedDifferences.keySet());

        // we deduced the distance weight that had been used in this trip, so we can set the deducedWeightsValues attribute of the trip
        Double deducedTripDistanceWeight = computedDifferences.get(lengthFromClosestLabel);
        setDeducedWeightsValues(Pair.with(deducedTripDistanceWeight, 1-deducedTripDistanceWeight));

        return calculatedLabels;
    }

    /**
     * Checks if a specific node is followed by another specific node in this trip
     * @param nodeStart The first node
     * @param nodeEnd The second node that may follow the first one
     * @return True if the section represented by these two nodes is in this trip
     */
    public boolean sectionIsInTrip(Node nodeStart, Node nodeEnd) {
        try {
            if(trip.get(trip.indexOf(nodeStart)+1).equals(nodeEnd))
                return true;
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return false;
        }
        return false;
    }
}
