package fr.alanguenegou.prd.prdapp;

import fr.alanguenegou.prd.prdapp.controller.ProblemSolver;
import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.dbaccess.UserDataDataAccess;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import fr.alanguenegou.prd.prdapp.view.Visualization;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * The launcher of this application
 * @author GUENEGOU A.
 * @version 1.00
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PrdappApplication {
    /**
     * Create a file with all the Nodes in the graph and their coordinates
     * @param graph to create file from
     */
    public void createNodeFile(Graph graph){
        try {
            FileWriter fw = new FileWriter("tours_noeuds.csv");
            PrintWriter pw = new PrintWriter(fw);
            graph.getNodes().forEach((key,value) -> {
                pw.print(value.getId());
                pw.print("\t");
                pw.print(value.getCoordX());
                pw.print("\t");
                pw.println(value.getCoordY());
            });
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a File with all the arcs in the graph, their distance and danger value
     * @param graph to create file from
     */
    public void createArcFile(Graph graph){
        try {
            FileWriter fw = new FileWriter("tours_arcs.csv");
            PrintWriter pw = new PrintWriter(fw);
            graph.getNodes().forEach((key,value) ->
                value.getAdjacentNodes().forEach((id,triplet) -> {
                    pw.print(key);
                    pw.print("\t");
                    pw.print(id.getId());
                    pw.print("\t");
                    pw.print(triplet.getValue0());
                    pw.print("\t");
                    pw.println(triplet.getValue1());
                })
            );
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The classic main method launching the app
     * @param args null
     */
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PrdappApplication.class);
        builder.headless(false);

        // connects to GraphDataSource and populates the graph object
        GraphDataAccess graphDataAccess = new GraphDataAccess();
        Graph graph = graphDataAccess.populateGraph();

        // connects to UserDataDataSource and populates the userData object
        UserDataDataAccess userDataDataAccess = new UserDataDataAccess();
        UserData userData = userDataDataAccess.populateUserData(graph);

        // create a visualization instance to display the graph
        Visualization visualization = new Visualization();

        // creates a problem solver instance that controls the app dialog
        ProblemSolver problemSolver = new ProblemSolver(graph, userData, graphDataAccess, visualization);
        problemSolver.launchProblemSolving();
    }
}
