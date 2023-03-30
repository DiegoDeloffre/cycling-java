package fr.alanguenegou.prd.prdapp.controller;

import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import fr.alanguenegou.prd.prdapp.userdata.Trip;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import fr.alanguenegou.prd.prdapp.view.Dialog;
import fr.alanguenegou.prd.prdapp.view.Visualization;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;

/**
 * The typical controller class of the app
 * @author GUENEGOU A.
 * @version 1.00
 */
public class ProblemSolver {
    /**
     * A logger instance to log infos in the console
     */
    private final static Logger log = LoggerFactory.getLogger(ProblemSolver.class);

    /**
     * An instance of the Graph class
     */
    @Getter @Setter
    private Graph graph;

    /**
     * An instance of the UserData class
     */
    @Getter @Setter
    private UserData userData;

    /**
     * An instance of the GraphDataAccess class
     */
    @Getter @Setter
    private GraphDataAccess graphDataAccess;

    /**
     * An instance of the Visualization class
     */
    @Getter @Setter
    private Visualization visualization;

    /**
     * An instance of the Dialog class
     */
    @Getter @Setter
    private Dialog dialog = new Dialog();

    /**
     * Shortest distance to check if a section is close to another one
     * Distance set to the average length of all the sections
     */
    private final double thresholdDistance = 67.0;

    /**
     * Total length of sections that can be improved
     */
    private final double lengthToModify = 150.0;

    /**
     * The class constructor
     * @param graph The {@link Graph} instance that is going to be used
     * @param userData The {@link UserData} instance that is going to be used
     * @param graphDataAccess The instance of a {@link GraphDataAccess}
     * @param visualization The instance of a {@link Visualization}
     */
    public ProblemSolver(Graph graph, UserData userData, GraphDataAccess graphDataAccess, Visualization visualization) {
        this.graph = graph;
        this.userData = userData;
        this.graphDataAccess = graphDataAccess;
        this.visualization = visualization;
    }

    /**
     * Check if the users follow the path provided by the itinerary algorithm
     */
    private void solveFirstProblem() {
        Collection<Trip> trips = userData.getTrips().values();
        double globalDifference = 0.0;
        int[] distribution = new int[11];
        int profileIterator = 0;

        // for each trip in the user data, we compute the difference between the real trip path
        // and its computed version calculated by the shortest-path algorithm
        for (Trip trip : trips) {
            double difference = trip.compareTripWithCalculatedVersion(graph);
            globalDifference += difference;
            profileIterator++;
            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{}% -> {} trajets ont été traités jusqu'à présent sur un total de {}",
                        Math.round(((double)profileIterator)/userData.getTrips().size()*100), profileIterator, userData.getTrips().size());
            }

            // analyse de la distribution entre 0 et 100% d'écart
            /*
            // fill distribution array
            if (difference >= 0.0 && difference < 10.0)
                distribution[0]++;
            else if (difference >= 10.0 && difference < 20.0)
                distribution[1]++;
            else if (difference >= 20.0 && difference < 30.0)
                distribution[2]++;
            else if (difference >= 30.0 && difference < 40.0)
                distribution[3]++;
            else if (difference >= 40.0 && difference < 50.0)
                distribution[4]++;
            else if (difference >= 50.0 && difference < 60.0)
                distribution[5]++;
            else if (difference >= 60.0 && difference < 70.0)
                distribution[6]++;
            else if (difference >= 70.0 && difference < 80.0)
                distribution[7]++;
            else if (difference >= 80.0 && difference < 90.0)
                distribution[8]++;
            else if (difference >= 90.0 && difference <= 100.0)
                distribution[9]++;
            else
                distribution[10]++;
             */

            // fill distribution array
            if (difference >= 0.0 && difference < 1.0)
                distribution[0]++;
            else if (difference >= 1.0 && difference < 2.0)
                distribution[1]++;
            else if (difference >= 2.0 && difference < 3.0)
                distribution[2]++;
            else if (difference >= 3.0 && difference < 4.0)
                distribution[3]++;
            else if (difference >= 4.0 && difference < 5.0)
                distribution[4]++;
            else if (difference >= 5.0 && difference < 6.0)
                distribution[5]++;
            else if (difference >= 6.0 && difference < 7.0)
                distribution[6]++;
            else if (difference >= 7.0 && difference < 8.0)
                distribution[7]++;
            else if (difference >= 8.0 && difference < 9.0)
                distribution[8]++;
            else if (difference >= 9.0 && difference <= 10.0)
                distribution[9]++;
            else
                distribution[10]++;

        }

        globalDifference = globalDifference/trips.size();
        System.out.println(globalDifference);
        System.out.println(trips.size());

        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.format("De façon brute, les chemins calculés sont en moyenne %.2f%% différents du trajet réellement emprunté par l'utilisateur%n", globalDifference);
        System.out.format("Cette analyse est faite sur une base de %d trajets utilisateur%n", trips.size());


        System.out.println("\nDétails de la distribution des écarts (bornes en pourcentage) :");
        System.out.println("----> se référer aux histogrammes de distribution disponibles dans le rapport de projet");
        /*
        System.out.println("[ 0, 10[   : " + distribution[0]);
        System.out.println("[10, 20[  : " + distribution[1]);
        System.out.println("[20, 30[  : " + distribution[2]);
        System.out.println("[30, 40[  : " + distribution[3]);
        System.out.println("[40, 50[  : " + distribution[4]);
        System.out.println("[50, 60[  : " + distribution[5]);
        System.out.println("[60, 70[  : " + distribution[6]);
        System.out.println("[70, 80[  : " + distribution[7]);
        System.out.println("[80, 90[  : " + distribution[8]);
        System.out.println("[90, 100] : " + distribution[9]);
         */

        System.out.println("[0, 1[  : " + distribution[0]);
        System.out.println("[1, 2[  : " + distribution[1]);
        System.out.println("[2, 3[  : " + distribution[2]);
        System.out.println("[3, 4[  : " + distribution[3]);
        System.out.println("[4, 5[  : " + distribution[4]);
        System.out.println("[5, 6[  : " + distribution[5]);
        System.out.println("[6, 7[  : " + distribution[6]);
        System.out.println("[7, 8[  : " + distribution[7]);
        System.out.println("[8, 9[  : " + distribution[8]);
        System.out.println("[9, 10] : " + distribution[9]);
        System.out.println();
        System.out.format("Attention : %d trajets ont un écart supérieur à 100%%%n", distribution[10]);
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Modify a section in the graph and compute results for close users trips
     */
    private void solveSecondProblem() {

        // modifies the section in the Tours graph
        HashMap<Long, Integer> sectionsToModify = new HashMap<>();
        long sectionIdToModify = 9098460L;
        sectionsToModify.put(sectionIdToModify, 5);
        //sectionsToModify.put(sectionToModify[0], 5);
        graph.modifyGraph(sectionsToModify);

        // Opti calculs
        // get nodes from section to modify

        // Get coordinates from start and end node of the modified section
        Node start = graph.getNodeStartBySection(Pair.with(sectionIdToModify,true));
        Node end = graph.getNodeEndBySection(Pair.with(sectionIdToModify,true));
        double x1 = start.getCoordX();
        double y1 = start.getCoordY();
        double x2 = end.getCoordX();
        double y2 = end.getCoordY();

        ArrayList<Node> nodesCloseToSection = new ArrayList<>();
        nodesCloseToSection.add(start);
        nodesCloseToSection.add(end);

        // Get distance based on the section length
        graph.getSections().get(Pair.with(sectionIdToModify,true));
        double distanceZoneOpti = Math.max(thresholdDistance,Math.abs(Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1), 2))));
        System.out.println("Distance zone : " + distanceZoneOpti);

        // for every node in the graph, we look for the ones close to either one of the nodes
        for(Node node : graph.getNodes().values()){
            if (Math.abs(Math.sqrt(Math.pow((x2 - node.getCoordX()),2) + Math.pow((y2 - node.getCoordY()), 2))) < distanceZoneOpti){
                nodesCloseToSection.add(node);
                continue;
            }

            if (Math.abs(Math.sqrt(Math.pow((x1 - node.getCoordX()),2) + Math.pow((y1 - node.getCoordY()), 2))) < distanceZoneOpti){
                nodesCloseToSection.add(node);
            }
        }

        System.out.println("Nombre de noeuds près de la section à modifier : " + nodesCloseToSection.size());

        ArrayList<Trip> tripsToModify = new ArrayList<>();

        // for every Trip
        for (Trip trip : userData.getTrips().values()){
            // For every node in the trip
            for (Node node : trip.getTrip()){
                // If the node is in the list of nodesCloseToSection
                if (nodesCloseToSection.contains(node)){
                    // We add it to the list and go on to the next trip
                    tripsToModify.add(trip);
                    break;
                }
            }
        }

        HashSet<Trip> involvedTrips = new HashSet<>(tripsToModify);
        System.out.println("Nombre de trip à modifier : " + involvedTrips.size());

        computeImprovements(involvedTrips);


    }

    /**
     * Modify the graph with the best sections to be modified and compute results for every user trip
     * All sections modified must be inferior to the distanceToModify
     * @param distanceToModify total distance of sections that can be improved
     */
    public void solveThirdProblem(Double distanceToModify){
        System.out.println("Debut third problem");

        // Compute the most taken sections
        HashMap<Long, Integer> mostTakenSections = getMostTakenSection();

        // Compute the sections around the most taken ones
        HashMap<Pair<Long, Boolean>, Integer> sectionsAroundMostTaken = getSectionAroundMostTaken(mostTakenSections);

        // Sort the sections by the computed score (descending order)
        LinkedHashMap<Pair<Long, Boolean>, Double> sectionsWithScoreSorted = getSectionScoresSorted(sectionsAroundMostTaken);

        // We create an empty list that will contain the sections to add
        List<Pair<Long, Boolean>> sectionsToAdd = new ArrayList<>();

        // We iterate over the sorted map with the score
        for (Map.Entry<Pair<Long, Boolean>, Double> entry : sectionsWithScoreSorted.entrySet()) {
            Pair<Long, Boolean> sectionId = entry.getKey();
            double distanceValue = graph.getDistanceBySection(sectionId);

            // If the distance of the current section is lower than the distance left to modify
            // We add this section to the list and update the distance left to modify
            if (distanceValue <= distanceToModify) {
                sectionsToAdd.add(sectionId);
                distanceToModify -= distanceValue;

                // if the distance to modify is equal to 0, we exit the loop
                if (distanceToModify == 0) {
                    break;
                }
            }
        }

        HashMap<Long, Integer> sectionsToModify = new HashMap<>();
        // We modify the danger value of every sections selected
        for (Pair<Long, Boolean> sections : sectionsToAdd){
            sectionsToModify.put(sections.getValue0(), 5);
        }

        // We modify the graph with all the modified sections
        graph.modifyGraph(sectionsToModify);

        System.out.format("Sur %d sections recensées, %d ont été modifiées pour une longueur totale de %f.%n", graph.getSections().size(), sectionsToModify.size(), (lengthToModify - distanceToModify) );

        // Recalculate all trips with the new graph and compute the improvements
        HashSet<Trip> involvedTrips = new HashSet<>(userData.getTrips().values());
        computeImprovements(involvedTrips);

        // Show modified sections
        this.visualization.addSections(sectionsToModify,graph);
        this.visualization.addGraph(graph);
        this.visualization.display();
    }

    /**
     * Display the Tours graph with or without trips
     * @param withTrips if true, add 10 random trips and 0 if false
     */
    private void displayGraph(Boolean withTrips){
        if (withTrips){
            Trip[] trips = new Trip[10];
            ArrayList<Trip> tripList = new ArrayList<>(userData.getTrips().values());
            Collections.shuffle(tripList);
            for (int i = 0; i < 10; i++){
                trips[i] = tripList.get(i);
            }
            this.visualization.addTrips(trips);
        }
        this.visualization.addGraph(graph);
        this.visualization.display();
    }

    /**
     * Launches the resolution of the problem that user chose to solve
     */
    public void launchProblemSolving() {
        System.out.println("------------------------- FILTRAGE DES DONNEES UTILISATEUR N°1 ------------------------- \n");
        int initialUserDataSize = userData.getTrips().size();
        System.out.println(initialUserDataSize);
        int[] checkTripsInfos = userData.checkTrips();
        dialog.printNumberOfUserDataNonValidTrips(checkTripsInfos[0], initialUserDataSize);
        System.out.println("------------------------- FILTRAGE DES DONNEES UTILISATEUR N°2 ------------------------- \n");
        userData.getTripsDistancesAndRemoveThoseUnderXMeters(500);
        System.out.println("---------------------------------------------------------------------------------------- \n");

        switch (dialog.getProblemChoice()) {
            case 1:
                solveFirstProblem();
                break;
            case 2:
                solveSecondProblem();
                break;
            case 3:
                solveThirdProblem(lengthToModify);
                break;
            case 4 :
                displayGraph(true);
                break;
            case 5 :
                displayGraph(false);
                break;
        }
    }

    /**
     * Calculate the new paths of trips involved and display values quantifying the improvements made
     * @param involvedTrips trips to recalculate
     */
    public void computeImprovements(HashSet<Trip> involvedTrips){
        int nbOfModifiedTrips = 0;
        double globalImprovement = 0;
        // keeps track of the progress of the numerous iterations
        int profileIterator = 0;
        log.info("Début de la phase de calculs...");
        for (Trip trip : involvedTrips) {
            profileIterator++;

            // computes the initial pareto front and also deduces the weights used for the edge values of the trip
            HashMap<Double, Pair<Double, Double>> initialParetoFront = trip.setTripWeightsThanksToComparison(graph);

            graph.prepareNewCalculation();

            // computes the pareto front with the modified edge danger values
            HashMap<Double, Pair<Double, Double>> modifiedParetoFront = graph.calculateLabelsForManyLinearCombinations(trip.getStartNode(), trip.getEndNode(), Graph.WITH_ALTERNATIVE_DANGER_VALUE);


            // initialises a set containing possibly interesting labels in a pareto front
            // with distance weight of label as map key and its future percent variation from initial value to new value as map value
            HashMap<Double, Double> distanceWeightsNearUserTrip = new HashMap<>();
            double tripDistanceWeightValue = trip.getDeducedWeightsValues().getValue0();
            if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6], 0.0);

            }

            // checks if there are modifications among labels near the user trip label one
            HashMap<Double, Double> tempModifications = new HashMap<>(distanceWeightsNearUserTrip.size());
            for (Double distanceWeight : distanceWeightsNearUserTrip.keySet()) {

                // if modified label has seen its value modified (= the graph modification  impacted the path for this linear combination of weights)
                if (!initialParetoFront.get(distanceWeight).equals(modifiedParetoFront.get(distanceWeight))) {

                    // determines the extreme Pareto front distance value for normalisation
                    double extremeDistanceLinearCombination;
                    if (initialParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0() < modifiedParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0()) {
                        extremeDistanceLinearCombination = modifiedParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0();
                    }
                    else {
                        extremeDistanceLinearCombination = initialParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]).getValue0();
                    }


                    // determines the extreme Pareto front danger value for normalisation
                    double extremeDangerLinearCombination;
                    if (initialParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1() < modifiedParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1()) {
                        extremeDangerLinearCombination = modifiedParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1();
                    }
                    else {
                        extremeDangerLinearCombination = initialParetoFront.get(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]).getValue1();
                    }

                    // computes percent variation and adds it in the previous hashmap
                    double initialLabelCost = distanceWeight * initialParetoFront.get(distanceWeight).getValue0() / extremeDistanceLinearCombination
                            + (1-distanceWeight) * initialParetoFront.get(distanceWeight).getValue1() / extremeDangerLinearCombination;

                    double newLabelCost = distanceWeight * modifiedParetoFront.get(distanceWeight).getValue0() / extremeDistanceLinearCombination
                            + (1-distanceWeight) * modifiedParetoFront.get(distanceWeight).getValue1() / extremeDangerLinearCombination;


                    tempModifications.put(distanceWeight, ((newLabelCost - initialLabelCost) / initialLabelCost * 100));

                    // log.warn("initialLabelCost = {} , newLabelCost = {} , iteration n°{}", initialLabelCost, newLabelCost, profileIterator);

                }
                else {

                    // marks the modification as useless for this distance weight in order to remove it outside the loop
                    tempModifications.put(distanceWeight, -999.0);
                }
            }

            // effectively proceeds to make the changes
            for (Map.Entry<Double, Double> modification : tempModifications.entrySet()) {
                if (modification.getValue() == -999.0) {
                    distanceWeightsNearUserTrip.remove(modification.getKey());
                } else {
                    distanceWeightsNearUserTrip.replace(modification.getKey(), modification.getValue());
                }
            }

            if (distanceWeightsNearUserTrip.size() > 0) {
                nbOfModifiedTrips++;
                globalImprovement += Collections.min(distanceWeightsNearUserTrip.values());
            }

            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{}% -> {} trajets ont été traités jusqu'à présent sur un total de {}",
                        Math.round(((double)profileIterator)/involvedTrips.size()*100), profileIterator, involvedTrips.size());
            }
        }

        globalImprovement = globalImprovement / nbOfModifiedTrips;

        System.out.println("-----------------------------------------------------------------");
        System.out.println();

        System.out.format("Sur %d trajets utilisateur recensés, %d seraient impactés par cette modification de tronçon. Soit %d %% des trajets.%n", userData.getTrips().size(), nbOfModifiedTrips, ((nbOfModifiedTrips/userData.getTrips().size())*100));
        System.out.format("Dans le cadre de ces %d trajets, la baisse moyenne du coût du trajet serait de %.3f%%.%n", nbOfModifiedTrips, globalImprovement);

        System.out.println();
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Get the sections most taken by users
     * @return the most taken sections
     */
    public HashMap<Long, Integer> getMostTakenSection(){
        HashMap<Long, Integer> sectionsTaken = new HashMap<>();

        // We iterate over all the trips
        for (Trip trip : userData.getTrips().values()){
            // for every section in the trip
            for (Long section : trip.getSections()) {
                // We update the number of trips that pass by this specific section
                if (sectionsTaken.containsKey(section)){
                    sectionsTaken.put(section,sectionsTaken.get(section) + 1);
                }else {
                    sectionsTaken.put(section,1);
                }
            }
        }

        return sectionsTaken;
    }

    /**
     * Check if a section is close to another section
     * @param sourceSection base section
     * @param targetSection section to check
     * @param distance of closeness
     * @return true if the target section is close to the ref section and false if not
     */
    public boolean isSectionClose(Pair<Node,Node> sourceSection, Pair<Node,Node> targetSection, double distance){
        // startNode refSection - startNode targetSection
        return Math.abs(Math.sqrt(Math.pow((sourceSection.getValue0().getCoordX() - targetSection.getValue0().getCoordX()), 2) +
                Math.pow((sourceSection.getValue0().getCoordY() - targetSection.getValue0().getCoordY()), 2))) < distance
                // startNode refSection - endNode targetSection
                || Math.abs(Math.sqrt(Math.pow((sourceSection.getValue0().getCoordX() - targetSection.getValue1().getCoordX()), 2) +
                Math.pow((sourceSection.getValue0().getCoordY() - targetSection.getValue1().getCoordY()), 2))) < distance
                // endNode refSection - startNode targetSection
                || Math.abs(Math.sqrt(Math.pow((sourceSection.getValue1().getCoordX() - targetSection.getValue0().getCoordX()), 2) +
                Math.pow((sourceSection.getValue1().getCoordY() - targetSection.getValue0().getCoordY()), 2))) < distance
                // endNode refSection - endNode targetSection
                || Math.abs(Math.sqrt(Math.pow((sourceSection.getValue1().getCoordX() - targetSection.getValue1().getCoordX()), 2) +
                Math.pow((sourceSection.getValue1().getCoordY() - targetSection.getValue1().getCoordY()), 2))) < distance;
    }

    /**
     * Select all the sections close to the most taken sections with a value equal to the number of times
     * a user has passed on a close section
     * @param sectionsTaken is a map of all the most taken sections (HashMap<sectionId,nbTimesThesectionIsTaken>)
     * @return A map with all the section associated with its value (number of times close sections are taken by users)
     */
    public HashMap<Pair<Long, Boolean>, Integer> getSectionAroundMostTaken(HashMap<Long, Integer> sectionsTaken){
        HashMap<Pair<Long, Boolean>, Integer> sectionsCloseToMostTakenSection = new HashMap<>();
        final int[] profileIterator = {0};
        log.info("Début de la phase de calculs...");
        // For each most taken section
        for (Map.Entry<Long, Integer> section : sectionsTaken.entrySet()){
            profileIterator[0]++;
            // we look at the nodes of this section and compute a distance around this section's nodes
            Node start = graph.getNodeStartBySection(Pair.with(section.getKey(),true));
            Node end = graph.getNodeEndBySection(Pair.with(section.getKey(),true));
            double distance = Math.max(thresholdDistance,1.5 * Math.abs(Math.sqrt(Math.pow(
                    (end.getCoordX() - start.getCoordX()),2) + Math.pow((end.getCoordY() - start.getCoordY()), 2))));

            // for all the sections in the graph
            graph.getSections().forEach((sectionId, node) -> {
                Node start2 = graph.getNodeStartBySection(sectionId);
                Node end2 = graph.getNodeEndBySection(sectionId);
                double dangerValue = graph.getDangerBySection(sectionId);
                double distanceValue = graph.getDistanceBySection(sectionId);
                double danger = dangerValue / distanceValue;

                // if the section in the graph is close to the current taken section and the security value is improvable
                if (danger != 5 && isSectionClose(Pair.with(start,end), Pair.with(start2,end2), distance)){

                    // if the graph section is already in the list, we update its value (number of times close sections are taken)
                    if (sectionsCloseToMostTakenSection.containsKey(sectionId)){
                        sectionsCloseToMostTakenSection.put(sectionId, sectionsCloseToMostTakenSection.get(sectionId) + section.getValue());

                        // else we set the value (number of times close sections are taken)
                    }else {
                        sectionsCloseToMostTakenSection.put(sectionId,section.getValue());
                    }
                }
            });
            if (Math.floorMod(profileIterator[0], 1000) == 0)   {
                log.info("{}% -> {} sections ont été traitées jusqu'à présent sur un total de {}",
                        Math.round(((double) profileIterator[0])/sectionsTaken.size()*100), profileIterator[0], sectionsTaken.size());
            }
        }

        // Map containing all section close to taken ones, with the cumulative number of times they are taken
        // ex : section 1 : taken 15 times --> sections around section 1 have been taken 15 times
        return sectionsCloseToMostTakenSection;
    }


    /**
     * Compute the score for every andidate section (sections that may be modified)
     * @param sectionsCloseToMostTakenSection Map of sections close to the most taken sections
     * @return a Map of the sections with the computed score
     */
    public LinkedHashMap<Pair<Long, Boolean>, Double> getSectionScoresSorted(HashMap<Pair<Long, Boolean>, Integer> sectionsCloseToMostTakenSection){
        HashMap<Pair<Long, Boolean>, Double> scores = new HashMap<>();

        // for each candidate section, we compute the score
        sectionsCloseToMostTakenSection.forEach((sectionId, nbPeople) ->{
            double dangerValue = graph.getDangerBySection(sectionId);
            double distanceValue = graph.getDistanceBySection(sectionId);
            double newDangerValue =  distanceValue / 5;
            double score = (nbPeople * (dangerValue - newDangerValue)) / dangerValue;
            scores.put(sectionId, score);
        });

        List<Map.Entry<Pair<Long, Boolean>, Double>> list = new ArrayList<>(scores.entrySet());

        // We sort the list by decreasing order on the Double value (score)
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // We create a new sorted map based on the list
        LinkedHashMap<Pair<Long, Boolean>, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Pair<Long, Boolean>, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

}
