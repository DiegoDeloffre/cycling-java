package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The class representing the entirety of the user data retrieved from the user data database
 * @author GUENEGOU A.
 * @version 1.00
 */
public class UserData {

    /**
     * A collection of trips representing all the user data
     */
    @Getter @Setter
    private HashMap<Integer, Trip> trips = new HashMap<>();


    /**
     * Adds a new trip to {@link UserData#trips}
     * @param tripId The ID of the trip
     * @param nodeList A list of nodes that represents the trip that has to be added
     */
    public void addTrip(int tripId, LinkedList<Node> nodeList) {
        Trip trip = new Trip(tripId, nodeList);
        trips.put(tripId, trip);
    }


    /**
     * Adds a node to a specific trip contained in {@link UserData#trips}
     * @param tripId The ID of the trip
     * @param node The node that has to be added
     */
    public void addNodeToTrip(int tripId, Node node) {
        trips.get(tripId).addNode(node);
    }


    /**
     * Adds a section to a trip contained in {@link UserData#trips} (adds its ID to a linked list)
     * @param tripId The ID of the trip that gets populated
     * @param sectionId The ID of the section that has to be added
     */
    public void addSectionToTrip(int tripId, long sectionId) {
        if (!trips.get(tripId).getSections().contains(sectionId)){
            trips.get(tripId).getSections().add(sectionId);
        }
    }


    /**
     * Checks if a specific node is not in a specific trip contained in {@link UserData#trips}
     * @param tripId The ID of the trip
     * @param node The node that has to be searched
     * @return True if the node is not in the trip
     */
    public boolean isNotInTrip(int tripId, Node node) {
        if (trips.get(tripId).getTrip().size() > 0){
            return trips.get(tripId).getTrip().getLast().getId() != node.getId();
        }else{
            return true;
        }

    }

    /**
     * Remove the X first and last steps of every trips in this UserData
     * @param numberOfNodes The number of steps that are removed from start and end of trips
     */
    public void removeXNodesFromEachTrip(int numberOfNodes) {
        ArrayList<Integer> tripsToRemove = new ArrayList<>();
        for (int i = 0; i < numberOfNodes; i++) {
            for (Trip trip : trips.values()) {

                if (trip.getTrip().size() > numberOfNodes*2) {
                    trip.getTrip().removeFirst();
                    trip.getTrip().removeLast();
                    trip.getSections().removeFirst();
                    trip.getSections().removeLast();
                }
                else {
                    tripsToRemove.add(trip.getId());
                }

            }
        }

        for (int tripId : tripsToRemove) {
            trips.remove(tripId);
        }
    }

    /**
     * Checks the validity of every trip in {@link UserData#trips} by removing the ones that don't technically fit the Tours graph (successor node not in neighbour list)
     * @return The number of trips that are not valid
     */
    public int[] checkTrips() {
        int numberOfNonValidTrips = 0;
        int numberOfProblematicNodesAtExtremities = 0;
        HashSet<Integer> tripsToRemove = new HashSet<>();
        // iterates through every trip
        for (Trip trip : trips.values()) {

            //Remove cycle trips
            if (trip.getStartNode().getId() == trip.getEndNode().getId()){
                tripsToRemove.add(trip.getId());
                numberOfNonValidTrips++;
                continue;
            }

            int tripSize = trip.getTrip().size();

            // checks if every node of the trip is in the neighbour list of the previous one
            for (int i = 1; i < tripSize; i++) {

                // if not, keeps the trip in memory
                if(!trip.getTrip().get(i-1).getAdjacentNodes().containsKey(trip.getTrip().get(i))) {
                    tripsToRemove.add(trip.getId());
                    numberOfNonValidTrips++;

                    ArrayList<Integer> tab = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, tripSize - 1, tripSize - 2, tripSize - 3, tripSize - 4, tripSize-5, tripSize-6));

                    if (tab.contains(i)) numberOfProblematicNodesAtExtremities++;

                    break;
                }
            }
        }

        // removes non valid trips
        for (int tripId : tripsToRemove) {
            trips.remove(tripId);
        }

        return new int[] {numberOfNonValidTrips, numberOfProblematicNodesAtExtremities, 6};
    }


    /**
     * Computes trip length tendencies and remove trips that have their length under a certain value
     * @param threshold Trips under this length level are removed from user data
     */
    public void getTripsDistancesAndRemoveThoseUnderXMeters(int threshold) {
        int initialTripsSize = trips.size();
        int from0To20 = 0;
        int from20To50 = 0;
        int from50To200 = 0;
        int from200To500 = 0;
        int from500To1000 = 0;
        int from1000AndAbove = 0;

        Set<Integer> tripsIdsToRemove = new HashSet<>();
        for (Trip trip : trips.values()) {
            double distance;
            if (trip.getTripValues().getValue0() == null){
                distance = 0.0;
            }else{
                distance = trip.getTripValues().getValue0();
            }

            if (0 <= distance && distance < 20)
                from0To20++;
            else if (20 <= distance && distance < 50)
                from20To50++;
            else if (50 <= distance && distance < 200)
                from50To200++;
            else if (200 <= distance && distance < 500)
                from200To500++;
            else if (500 <= distance && distance < 1000)
                from500To1000++;
            else if (1000 <= distance)
                from1000AndAbove++;

            if (distance < threshold)
                tripsIdsToRemove.add(trip.getId());

        }

        // safely removes all trips under a certain length from user data
        for (int tripId : tripsIdsToRemove) {
            trips.remove(tripId);
        }

        System.out.println("Tendances sur la longueur des trajets utilisateur : sur un total de " + initialTripsSize + " trajets conformes...");
        System.out.println("     " + from0To20 + " font entre 0 et 20 mètres");
        System.out.println("     " + from20To50 + " font entre 20 et 50 mètres");
        System.out.println("     " + from50To200 + " font entre 50 et 200 mètres");
        System.out.println("     " + from200To500 + " font entre 200 et 500 mètres");
        System.out.println("     " + from500To1000 + " font entre 500 et 1000 mètres");
        System.out.println("     " + from1000AndAbove + " font 1km et plus \n");

        System.out.println("Les trajets ayant une longueur inférieure à " + threshold + " mètres sont considérés non pertinents pour analyse " +
                "et sont donc supprimés avant traitement des données utilisateur :");
        System.out.println("Finalement, " + trips.size() + " trajets seront traités par la suite \n");
    }
}
