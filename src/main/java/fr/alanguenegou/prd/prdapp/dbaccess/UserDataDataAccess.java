package fr.alanguenegou.prd.prdapp.dbaccess;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;
import java.sql.Timestamp;
import java.util.*;

/**
 * The class managing the user data database access used to populate a {@link UserData} instance
 * @author GUENEGOU A.
 * @version 1.00
 */
public class UserDataDataAccess {

    /**
     * A logger instance to log infos into the console
     */
    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    /**
     * An instance of a JdbcTemplate used to connect to a database
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * The class constructor with database connexion
     * (database parameters must be adapted to the local datasource setup)
     */
    public UserDataDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/prd");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    /**
     * Prints the number of rows in the user data database
     */
    public void printNumOfRows() {
        var sql = "SELECT COUNT(*) FROM traces_splitted_areaid_7";
        try {
            Integer numOfRows = jdbcTemplate.queryForObject(sql, Integer.class);
            log.info("La connexion à la base de données du graphe a fonctionné");
            System.out.format("Il y a %d lignes dans les données utilisateur mises à ma disposition%n", numOfRows);

        } catch (NullPointerException nullPointerException) {
            System.out.println("La connexion à la base de données du graphe a échoué :");
            nullPointerException.printStackTrace();
        }
    }


    /**
     * Populates the UserData instance according to the user data database
     * @param graph The {@link Graph} instance where to find sections details of Tours
     * @return The populated UserData instance
     */

    public UserData populateUserData(Graph graph) {
        UserData userData = new UserData();
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Début du remplissage de l'objet données utilisateur...");

        var sql = "SELECT clean_user_trace_id as userId, created, start_osm_node_id, end_osm_node_id, archive_sectionsegments.section_id, \n" +
                "is_following_way_direction, segment_distance\n" +
                ", ST_X(ST_StartPoint(geometry)) as Xstart, ST_Y(ST_StartPoint(geometry)) as Ystart, ST_X(ST_EndPoint(geometry)) as Xend, ST_Y(ST_EndPoint(geometry)) as Yend \n" +
                "FROM archive_cleanusertracesegment\n" +
                "LEFT JOIN archive_sectionsegments ON archive_cleanusertracesegment.segment_key = archive_sectionsegments.segment_key\n" +
                "ORDER BY userId, archive_cleanusertracesegment.id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);


        int tempTripId = 0;
        Timestamp timestamp = new Timestamp(0);
        long numberOfTrips = 0;
        long sectionIdNew = 0;
        Node startNode;
        Node endNode;
        ArrayList<Long> newNodes = new ArrayList<>();

        boolean error;

        // iterates through all rows of userDataDataSource
        for (Map<String, Object> row : rows) {
            error = false;

            Long sectionId = (Long) row.get("section_id");
            Double segmentDistance = (Double) row.get("segment_distance");
            Boolean wayDirection = (Boolean) row.get("is_following_way_direction");

            // If the trip section is not linked to an existing graph section
            if (sectionId == null){
                // We create the nodes if needed

                // If the first Node is not in the graph, we add it
                if (graph.getNodeById((Long) row.get("start_osm_node_id")) == null){
                    graph.addNode((Long) row.get("start_osm_node_id"));
                    newNodes.add((Long) row.get("start_osm_node_id"));
                    graph.getNodeById((Long) row.get("start_osm_node_id")).setCoordX((Double) row.get("Xstart"));
                    graph.getNodeById((Long) row.get("start_osm_node_id")).setCoordY((Double) row.get("Ystart"));

                }
                // If the last Node is not in the graph, we add it
                if (graph.getNodeById((Long) row.get("end_osm_node_id")) == null){
                    graph.addNode((Long) row.get("end_osm_node_id"));
                    newNodes.add((Long) row.get("end_osm_node_id"));
                    graph.getNodeById((Long) row.get("end_osm_node_id")).setCoordX((Double) row.get("Xend"));
                    graph.getNodeById((Long) row.get("end_osm_node_id")).setCoordY((Double) row.get("Yend"));
                }

                // We create a new section in the graph
                sectionIdNew++;

                startNode = graph.getNodeById((Long) row.get("start_osm_node_id"));
                endNode = graph.getNodeById((Long) row.get("end_osm_node_id"));

                // These are sections created from user trips so we do not now the amenagement type
                // the amenagement is calculated by doing distance / dangerValue and the lowest value of danger is 1
                // so here dangerValue = distance
                graph.setLinkBetweenNodes(startNode, endNode, sectionIdNew, segmentDistance, segmentDistance, segmentDistance, wayDirection);
            }else{
                // if the section already exists in the graph

                // If the first node is not in the graph, we add it
                if (graph.getNodeById((Long) row.get("start_osm_node_id")) == null){
                    graph.addNode((Long) row.get("start_osm_node_id"));
                    newNodes.add((Long) row.get("start_osm_node_id"));
                    graph.getNodeById((Long) row.get("start_osm_node_id")).setCoordX((Double) row.get("Xstart"));
                    graph.getNodeById((Long) row.get("start_osm_node_id")).setCoordY((Double) row.get("Ystart"));
                    error = true;
                }

                // If the last node is not in the graph, we add it
                if (graph.getNodeById((Long) row.get("end_osm_node_id")) == null){
                    graph.addNode((Long) row.get("end_osm_node_id"));
                    newNodes.add((Long) row.get("end_osm_node_id"));
                    graph.getNodeById((Long) row.get("end_osm_node_id")).setCoordX((Double) row.get("Xend"));
                    graph.getNodeById((Long) row.get("end_osm_node_id")).setCoordY((Double) row.get("Yend"));
                    error = true;
                }

                startNode = graph.getNodeById((Long) row.get("start_osm_node_id"));
                endNode = graph.getNodeById((Long) row.get("end_osm_node_id"));

                // if one of the Nodes was not in the original graph, we create the new sections
                if (newNodes.contains((Long) row.get("start_osm_node_id")) || newNodes.contains((Long) row.get("end_osm_node_id"))){
                    sectionIdNew++;

                    // These are sections created from user trips so we do not now the amenagement type
                    // the amenagement is calculated by doing distance / dangerValue and the lowest value of danger is one
                    // so here dangerValue = distance
                    graph.setLinkBetweenNodes(startNode, endNode, sectionIdNew, segmentDistance, segmentDistance, segmentDistance, wayDirection);
                }

            }

            // if we operate on a new Trip identified by new UserId or new trip by same user (different timestamp of creation)
            if ((int) row.get("userId") != tempTripId ||
                    ( (int) row.get("userId") == tempTripId && timestamp.compareTo((Timestamp) row.get("created")) != 0)) {
                numberOfTrips++;

                // updates trip id to the new one
                tempTripId = (int) row.get("userId");
                timestamp = (Timestamp) row.get("created");

                // creates new trip and adds it to userData
                LinkedList<Node> trip = new LinkedList<>();
                userData.addTrip((int) numberOfTrips, trip);
            }

            /*
            here operating on actual trip row
             */

            // checks if start node is already in trip. If not, adds it
            if (userData.isNotInTrip((int) numberOfTrips, startNode)){
                userData.addNodeToTrip((int) numberOfTrips, startNode);
            }

            // checks if end node is already in trip. If not, adds it
            if (userData.isNotInTrip((int) numberOfTrips, endNode)){
                userData.addNodeToTrip((int) numberOfTrips, endNode);
            }

            // if we had to create a new section, we add it to the trip with the new sectionId
            if (sectionId == null || error){
                // adds the routelink to the trip to keep track of all sections composing a trip
                userData.addSectionToTrip((int) numberOfTrips, sectionIdNew);
            }else{
                // if the section already exists in the graph, we simply add it to the trip
                userData.addSectionToTrip((int) numberOfTrips, sectionId);
            }
        }

        watch.stop();
        log.info("     Fin du remplissage de l'objet données utilisateur, effectué en {} secondes", watch.getTotalTimeSeconds());
        log.info("     Itération faite sur {} lignes", rows.size());
        log.info("     {} trajets ont été créés", numberOfTrips);
        log.info("     {} nombres de trip",     userData.getTrips().size());

        return userData;
    }
}