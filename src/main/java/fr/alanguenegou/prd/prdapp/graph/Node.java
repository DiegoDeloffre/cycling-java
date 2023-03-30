package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.*;
import java.util.*;

/**
 * The class modelling a node in a graph
 * @author GUENEGOU A.
 * @version 1.00
 */
public class Node {

    /**
     * A list of all the direct predecessors of this node
     */
    @Getter @Setter
    private ArrayList<Long> predecessorNodes = new ArrayList<>();

    /**
     * The ID of this node
     */
    @Getter @Setter
    private long id;

    /**
     * Coordinates X of this node
     */
    @Getter @Setter
    private double coordX;

    /**
     * Coordinates Y of this node
     */
    @Getter @Setter
    private double coordY;

    /**
     * A list of nodes representing the computed shortest-path from a source node to this node (computation is done during some method calls)
     */
    @Getter @Setter
    private List<Node> shortestPath = new LinkedList<>();

    /**
     * The cost from a source node to this node (arbitrarily initialized to infinite positive value)
     */
    @Getter @Setter
    private Double cost = Double.MAX_VALUE;

    /**
     * The adjacent nodes of this node.
     * Mapping for the direct successors : {Node, Triplet{distance, danger, alternate danger value}}
     */
    @Getter @Setter
    Map<Node, Triplet<Double, Double, Double>> adjacentNodes = new HashMap<>();

    /**
     * Adds a neighbor to this node
     * @param destination The neighbour node
     * @param distance    The distance from this node to the neighbor node
     * @param danger      The danger value on the section between this node and the neighbour node
     */
    public void addNeighbour(Node destination, Double distance, Double danger) {
        Triplet<Double, Double, Double> triplet = Triplet.with(distance, danger, null);
        adjacentNodes.put(destination, triplet);
    }

    /**
     * The class constructor with a specific node ID
     * @param id The ID of the new node
     */
    public Node(long id) {
        this.id = id;
    }

    /**
     * Adds the ID of a predecessor to this node
     * @param nodeId The predecessor ID
     */
    public void addPredecessorNode(long nodeId) {
        predecessorNodes.add(nodeId);
    }

    /**
     * Resets the cost and path attributes of this node
     */
    public void resetDistanceAndPath() {
        setShortestPath(new LinkedList<>());
        setCost(Double.MAX_VALUE);
    }

    /**
     * Modifies the danger value of the section between this node and a successor
     * @param neighbourNode The neighbour node
     * @param newSecurityFactor The new security factor based on the factor value of the section layout type
     */
    public void modifySectionDangerValue(Node neighbourNode, int newSecurityFactor) {
        Double neighbourDistance = getAdjacentNodes().get(neighbourNode).getValue0();
        getAdjacentNodes().replace(neighbourNode, getAdjacentNodes().get(neighbourNode).setAt2(neighbourDistance / newSecurityFactor));
    }
}
