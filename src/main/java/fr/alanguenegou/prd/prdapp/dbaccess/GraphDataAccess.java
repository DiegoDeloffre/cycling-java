package fr.alanguenegou.prd.prdapp.dbaccess;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;
import java.util.List;
import java.util.Map;

/**
 * The class managing the Graph database access used to populate a {@link Graph} instance
 * @author GUENEGOU A.
 * @version 1.00
 */
public class GraphDataAccess {

    /**
     * A logger instance to log infos into the console
     */
    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    /**
     * An instance of a JdbcTemplate used to connect to a database
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * The class constructor with connexion to graph data source and initialization of a jdbcTemplate
     * (database parameters must be adapted to the local datasource setup)
     */
    public GraphDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/prd");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    /**
     * Prints the number of rows in the graph data source
     */
    public void printNumOfRows() {

        var sql = "SELECT COUNT(*) FROM archive_osmwaysection";
        try {
            Integer numOfRows = jdbcTemplate.queryForObject(sql, Integer.class);
            log.info("La connexion à la base de données du graphe a fonctionné");
            System.out.format("Il y a %d lignes dans les données du graphe de Tours mises à ma disposition%n", numOfRows);

        } catch (NullPointerException nullPointerException) {
            log.error("La connexion à la base de données du graphe a échoué");
            nullPointerException.printStackTrace();
        }

    }


    /**
     * Computes the danger value of a specific section
     * @param layout The layout type of the section
     * @param length The length of the section
     * @return The danger value
     */
    public Double getDangerValue(String layout, Double length) {
        int coefficient;
        if (layout == null) {
            coefficient = 1;
        }
        else {
            switch (layout) {
                case "chemin dedie uni":
                case "chemin dedie bi":
                case "chemin service site propre uni":
                case "chemin service site propre bi":
                case "piste uni":
                case "piste bi":
                case "piste trottoir bi":
                case "piste trottoir uni":
                case "voie verte bi":
                case "voie verte uni":
                    coefficient = 5;
                    break;

                case "cheminement trottoir uni":
                case "cheminement trottoir bi":
                case "autre chemin velo uni":
                case "autre chemin velo bi":
                case "bande uni":
                case "bande bi":
                case "voie bus uni":
                case "voie bus bi":
                    coefficient = 3;
                    break;

                case "chaucidou":
                case "cheminement uni":
                case "cheminement bi":
                case "DSC bande":
                    coefficient = 2;
                    break;

                case "DSC":
                case "bicycle_no":
                    coefficient = 1;
                    break;

                default:
                    log.error("Le type d'aménagement n'est pas reconnu");
                    coefficient = Integer.MAX_VALUE;
                    break;
            }
        }
        return length / coefficient;
    }


    /**
     * Populates a new {@link Graph} instance according to the graph database
     * @return The populated Graph instance
     */
    public Graph populateGraph() {
        Graph graph = new Graph();
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Début du remplissage de l'objet graphe de Tours...");

        var sql = "SELECT DISTINCT ON(section_id)\n" +
                "    section_id, nodes[1] as node_start,nodes[ST_Npoints(geometry)] as node_end, segment_key, nodes, way_id,\n" +
                "facility_right as amenagement_D, facility_left as amenagement_G,  ST_X(ST_StartPoint(geometry)) as Xstart, ST_Y(ST_StartPoint(geometry))\n" +
                "as Ystart, ST_X(ST_EndPoint(geometry)) as Xend, ST_Y(ST_EndPoint(geometry)) as Yend,\n" +
                "st_length(geometry::geography)\n" +
                "    \n" +
                "FROM archive_osmwaysection, archive_sectionsegments\n" +
                "WHERE array[split_part(segment_key, '-', 1)::bigint, split_part(segment_key, '-', 2)::bigint] <@ archive_osmwaysection.nodes\n" +
                "ORDER BY section_id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        int numberOfNodes = 0; // number of nodes created

        // iterates through all rows of graphDataSource
        for (Map<String, Object> row : rows) {
            Double length = (Double)row.get("st_length");

            // checks if nodeStart is already in graph. If not, adds it
            long nodeStart = ((Long) row.get("node_end"));
            if (graph.isNotInGraph(nodeStart)){
                numberOfNodes++;
                graph.addNode(nodeStart);
                if ((Double) row.get("Xstart") == 0.0 || (Double)row.get("Ystart") == 0.0){
                    System.out.println("Problème start");
                }
                graph.getNodeById(nodeStart).setCoordX((Double) row.get("Xstart"));
                graph.getNodeById(nodeStart).setCoordY((Double) row.get("Ystart"));
            }

            // checks if nodeEnd is already in graph. If not, adds it
            long nodeEnd = ((Long) row.get("node_start"));
            if (graph.isNotInGraph(nodeEnd)){
                numberOfNodes++;
                graph.addNode(nodeEnd);
                if ((Double) row.get("Xend") == 0.0 || (Double)row.get("Yend") == 0.0){
                    System.out.println("Problème end");
                }
                graph.getNodeById(nodeEnd).setCoordX((Double) row.get("Xend"));
                graph.getNodeById(nodeEnd).setCoordY((Double) row.get("Yend"));


            }

            // populates sections Map
            long sectionID = (Long) row.get("section_id");

            // converts amenagements string into danger value
            Double dangerD = getDangerValue((String)row.get("amenagement_D"), length);
            Double dangerG = getDangerValue((String)row.get("amenagement_G"), length);

            Node startNode = graph.getNodeById(nodeStart);
            Node endNode = graph.getNodeById(nodeEnd);

            graph.setLinkBetweenNodes(startNode, endNode, sectionID, length, dangerD, dangerG, true);
        }

        watch.stop();
        log.info("     Fin du remplissage de l'objet graphe de Tours, effectué en {} secondes", watch.getTotalTimeSeconds());
        log.info("     Itération faite sur {} lignes", rows.size());
        log.info("     {} noeuds ont été créés", numberOfNodes);

        return graph;
    }
}
