package org.nlogo.extensions.dbscan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.junit.Test;
import org.nlogo.agent.Agent;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;
import org.nlogo.api.FileIO;
import org.nlogo.core.LogoList;
import org.nlogo.core.WorldDimensions;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentVariable;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoPatchVariable;
import org.nlogo.headless.HeadlessWorkspace;

/**
 * Tests DBSCAN clusterer using an existing NetLogo model and injecting agents to be clustered.
 * These tests have been written against the NetLogo API 6.1.
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 * 
 * Setup:
 * - Import all jar libraries from the app folder contained in the in NetLogo installation folder.
 * - Copy the test model (java-netlogo-clustering-test-v6.nlogo) contained in the test subfolder resources
 *   to the project directory's folder 'src/...' as specified in the TESTFILE variable below (or adapt 
 *   the path in the variable)
 * - Ensure that the dbscan extension is installed in NetLogo 
 *
 * @author Christopher Frantz <cf@christopherfrantz.org>
 * @version 0.3 (30.05.2019)
 *
 */

public class TestNetLogoDBSCAN {

    /**
     * Test model used by all tests
     */
    public static final String TESTMODEL = System.getProperty("user.dir")
            + "/src/test/resources/java-netlogo-clustering-test-v6.nlogo";

    /**
     * Produces additional output helpful for debugging tests.
     */
    public static final boolean debugOutput = false;

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelFromJava() {

        Random random = new Random(4522);

        final String AGENTS = "AGENTS";
        final String VARIABLE = "WEALTH";

        HeadlessWorkspace ws = HeadlessWorkspace.newInstance();

        try {
            ws.openString(FileIO.url2String("file://" + TESTMODEL));
        } catch (Exception e1) {
            fail("Problems when opening NetLogo model file: " + e1.getMessage());
        }

        World world = ws.world();

        LogoListBuilder b = new LogoListBuilder();
        AgentSet breed = world.getBreed(AGENTS);

        int i = 0;
        while (i < 100) {
            Turtle turtle = world.createTurtle(breed);
            b.add(turtle);
            i++;
        }

        Collection<Agent> l2 = new ArrayList<>();

        LogoList l1 = b.toLogoList();

        for (int j = 0; j < l1.size(); j++) {
            Turtle t = ((Turtle) l1.get(j));

            try {
                t.setBreedVariable(VARIABLE, random.nextInt(1000));
            } catch (AgentException e1) {
                fail("Error setting variable value: " + e1);
            }

            if (debugOutput) {
                System.out.print("ID: " + t.id() + ", Wealth: ");
            }

            try {
                Object obj = t.getBreedVariable(VARIABLE);
                if (debugOutput) {
                System.out.println(obj);
                }
            } catch (AgentException e) {
                fail("Exception when attempting to retrieve agent cluster variable value: " + e);
            }

            l2.add(t);
        }

        // Setting up hyperparameters
        int minCluster = 3;
        double maxDistance = 5;

        ArrayList<ArrayList<Agent>> result = null;

        try {
            DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(l2, minCluster, maxDistance, new DistanceMetricNetLogoAgentVariable(VARIABLE));
            result = clusterer.performClustering();
        } catch (DBSCANClusteringException e) {
            fail("Exception during clustering operation: " + e);
        }

        LogoList res = LogoList.fromJava(result);

        if (debugOutput) {
            System.out.println("Resulting clusters: " + res);
        }

        assertEquals("Number of Clusters equals", 12, res.size());
    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelFromJavaWithUnknownClusteringVariable() {

        Random random = new Random(4522);

        final String AGENTS = "AGENTS";
        final String VARIABLE = "WEALTH";

        HeadlessWorkspace ws = HeadlessWorkspace.newInstance();

        try {
            ws.openString(FileIO.url2String("file://" + TESTMODEL));
        } catch (Exception e1) {
            fail("Problems when opening NetLogo model file: " + e1.getMessage());
        }

        World world = ws.world();

        LogoListBuilder b = new LogoListBuilder();
        AgentSet breed = world.getBreed(AGENTS);

        int i = 0;
        while (i < 100) {
            Turtle turtle = world.createTurtle(breed);
            b.add(turtle);
            i++;
        }

        Collection<Agent> l2 = new ArrayList<>();

        LogoList l1 = b.toLogoList();

        for (int j = 0; j < l1.size(); j++) {
            Turtle t = ((Turtle) l1.get(j));

            try {
                t.setBreedVariable(VARIABLE, random.nextInt(1000));
            } catch (AgentException e1) {
                fail("Error setting variable value: " + e1);
            }

            if (debugOutput) {
                System.out.print("ID: " + t.id() + ", Wealth: ");
            }

            try {
                Object obj = t.getBreedVariable(VARIABLE);
                if (debugOutput) {
                    System.out.println(obj);
                }
            } catch (AgentException e) {
                fail("Exception when attempting to retrieve agent cluster variable value: " + e);
            }

            l2.add(t);
        }

        // Setting up hyperparameters
        int minCluster = 3;
        double maxDistance = 5;

        try {
            DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(l2, minCluster, maxDistance, new DistanceMetricNetLogoAgentVariable(VARIABLE + "2"));
            clusterer.performClustering();
            fail("Clustering should not succeed.");
        } catch (DBSCANClusteringException e) {
            assertEquals("Execution fails due to incorrect turtle variable name",
                    "AGENTS breed does not own variable WEALTH2", 
                    e.getMessage());
        }
    }

    @Test
    public void testClusteringOfPatchesByVariablesInExistingNetLogoModelFromJava() {

        Random random = new Random(4522);

        HeadlessWorkspace ws = HeadlessWorkspace.newInstance();

        try {
            ws.openString(FileIO.url2String("file://" + TESTMODEL));
        } catch (Exception e1) {
            fail("Problems when opening NetLogo model file: " + e1);
        }

        // Variable defined in model file
        final String VARIABLE = "RESOURCE";

        // Setting up world
        World world = ws.world();

        world.createPatches(new WorldDimensions(0, 16, 0, 16));
        
        int variableIdx = world.patchesOwnIndexOf(VARIABLE);
        
        ArrayList<Agent> clusterInput = new ArrayList<>();
        for (int i = 0; i < world.patches().count(); i++) {
            try {
                world.patches().getByIndex(i).setPatchVariable(variableIdx, new Integer(random.nextInt(1500)));
            } catch (AgentException e) {
                fail("Exception thrown while setting up patch variable: " + e);
            }
            clusterInput.add(world.patches().getByIndex(i));
        }

        // Debug output
        if (debugOutput) {
            System.out.println("Input: ");
            clusterInput.forEach(elem -> {
                try {
                    System.out.println("Patch " + elem.toString() + ": " + elem.getPatchVariable(variableIdx).toString());
                } catch (AgentException e1) {
                    fail("Exception when iterating over patches and accessing cluster variable: " + e1);
                }
            });
        }

        assertEquals("Input patches successfully retrieved", 289, clusterInput.size());

        // Setting up hyperparameters
        int minCluster = 3;
        double maxDistance = 5;

        ArrayList<ArrayList<Agent>> result = null;

        try {
            DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(clusterInput, minCluster, maxDistance, new DistanceMetricNetLogoPatchVariable(variableIdx));
            result = clusterer.performClustering();
        } catch (DBSCANClusteringException e) {
            fail("Error during clustering operation: " + e);
        }

        LogoList res = LogoList.fromJava(result);

        if (debugOutput) {
            System.out.println("Resulting clusters: " + res);
            System.out.println("Clusters count: " + res.size());
        }

        assertEquals("Number of Clusters equals", 48, res.size());
    }

    @Test
    public void testClusteringOfPatchesByVariablesInExistingNetLogoModelFromJavaWithUnknownClusteringVariable() {

        Random random = new Random(4522);

        HeadlessWorkspace ws = HeadlessWorkspace.newInstance();

        try {
            ws.openString(FileIO.url2String("file://" + TESTMODEL));
        } catch (Exception e1) {
            fail("Problems when opening NetLogo model file: " + e1);
        }

        // Variable defined in model file
        final String VARIABLE = "RESOURCE";

        // Setting up world
        World world = ws.world();

        world.createPatches(new WorldDimensions(0, 16, 0, 16));
        
        int variableIdx = world.patchesOwnIndexOf(VARIABLE);
        
        ArrayList<Agent> clusterInput = new ArrayList<>();
        for (int i = 0; i < world.patches().count(); i++) {
            try {
                world.patches().getByIndex(i).setPatchVariable(variableIdx, new Integer(random.nextInt(1500)));
            } catch (AgentException e) {
                fail("Exception thrown while setting up patch variable: " + e);
            }
            clusterInput.add(world.patches().getByIndex(i));
        }
        
        // Debug output
        if (debugOutput) {
            System.out.println("Input: ");
            clusterInput.forEach(elem -> {
                try {
                    System.out.println("Patch " + elem.toString() + ": " + elem.getPatchVariable(variableIdx).toString());
                } catch (AgentException e1) {
                    fail("Exception when iterating over patches and accessing cluster variable: " + e1);
                }
            });
        }
        
        assertEquals("Input patches successfully retrieved", 289, clusterInput.size());
        
        // Setting up hyperparameters
        int minCluster = 3;
        double maxDistance = 5;

        try {
            // Initialise with invalid variable index
            DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(clusterInput, minCluster, maxDistance, new DistanceMetricNetLogoPatchVariable(variableIdx + 1));
            clusterer.performClustering();
            fail("Clustering should not succeed.");
        } catch (DBSCANClusteringException e) {
            assertEquals("Execution fails due to incorrect patch variable index",
                    "java.lang.ArrayIndexOutOfBoundsException when accessing patch variable 6. Message: 6", 
                    e.getMessage());
        }
    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadless() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Setting up entities
          workspace.command("setup");
          // Run clustering
          workspace.command("set clusters dbscan:cluster-by-location agents 3 3");
          if (debugOutput) {
              // Print clusters
              System.out.println(workspace.report("length clusters"));
          }
          assertEquals("Number of clusters expected", 7.0, workspace.report("length clusters"));
          workspace.dispose();
        } catch(Exception ex) {
          fail("Clustering was not successful (but should have been). Exception: " + ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessMissingInput() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Setting up entities
          workspace.command("setup");
          // Run clustering with empty agent input set
          workspace.command("set clusters dbscan:cluster-by-location agents 3 3");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
            assertEquals("Invalid input data", 
                  "Extension exception: Clustering input data (e.g., turtles, patches) has not been provided (or is empty).", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessInvalidInputAgentSet() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Setting up entities
          workspace.command("setup");
          // Run clustering with invalid agent input set (variable not defined)
          workspace.command("set clusters dbscan:cluster-by-location agentz 3 3");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
            assertEquals("Invalid input data", 
                  "Nothing named AGENTZ has been defined.", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessMissingHyperParameters() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering without specifying hyperparameters
          workspace.command("set clusters dbscan:cluster-by-location agents");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
                  "DBSCAN:CLUSTER-BY-LOCATION expected 3 inputs, an agentset, a number and a number.", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessInvalidMinClusterMembers() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with invalid minimum cluster members (< 2)
          workspace.command("set clusters dbscan:cluster-by-location agents 1 1");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
              "Extension exception: Minimum number of cluster elements cannot be smaller than 2.", 
              ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessMaxDistance0() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with max distance set to 0
          workspace.command("set clusters dbscan:cluster-by-location agents 2 0");
          assertEquals("Number of clusters expected", 0.0, workspace.report("length clusters"));
          workspace.dispose();
        } catch(Exception ex) {
          fail("Maximum distance of 0 should be permissible input. Exception: " + ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByLocationInExistingNetLogoModelHeadlessInvalidMaxDistance() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with negative maximum distance
          workspace.command("set clusters dbscan:cluster-by-location agents 2 -1");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
                  "Extension exception: Maximum distance of cluster variable values cannot be negative.", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessMissingClusterVariable() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Setting up entities
          workspace.command("setup");
          // Run clustering
          workspace.command("set clusters dbscan:cluster-by-variable agents 3 2");
          if (debugOutput) {
              // Print clusters
              System.out.println(workspace.report("length clusters"));
          }
          fail("Clustering should not succeed without cluster variable.");
          workspace.dispose();
        } catch(Exception ex) {
          assertEquals("Indicate parameter signature for cluster-by-variable", "DBSCAN:CLUSTER-BY-VARIABLE expected 4 inputs, an agentset, a string, a number and a number.", ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadless() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Setting up entities
          workspace.command("setup");
          // Run clustering
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\" 3 2");
          if (debugOutput) {
              // Print clusters
              System.out.println(workspace.report("length clusters"));
          }
          assertEquals("Number of clusters expected", 5.0, workspace.report("length clusters"));
          workspace.dispose();
        } catch(Exception ex) {
          fail("Clustering was not successful (but should have been). Exception: " + ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessMissingInput() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Setting up entities
          workspace.command("setup");
          // Run clustering with empty agent input set
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\" 3 3");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
                  "Extension exception: Clustering input data (e.g., turtles, patches) has not been provided (or is empty).", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariableInExistingNetLogoModelHeadlessInvalidInputAgentSet() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parameterise agent number
          workspace.command("pre-setup");
          // Setting up entities
          workspace.command("setup");
          // Run clustering with invalid agent input set (variable not defined)
          workspace.command("set clusters dbscan:cluster-by-variable agentz \"wealth\" 3 3");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
            assertEquals("Invalid input data", 
                  "Nothing named AGENTZ has been defined.", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessMissingHyperParameters() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parametrise agent numbers
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering without specifying hyperparameters
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\"");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
                  "DBSCAN:CLUSTER-BY-VARIABLE expected 4 inputs, an agentset, a string, a number and a number.", 
                  ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessInvalidMinClusterMembers() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parametrise agent numbers
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with invalid minimum cluster members (< 2)
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\" 1 1");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
              "Extension exception: Minimum number of cluster elements cannot be smaller than 2.", 
              ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessMaxDistance0() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parametrise agent numbers
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with max distance set to 0
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\" 2 0");
          assertEquals("Number of clusters expected", 11.0, workspace.report("length clusters"));
          workspace.dispose();
        } catch(Exception ex) {
          fail("Maximum distance of 0 should be permissible input. Exception: " + ex.getMessage());
        }

    }

    @Test
    public void testClusteringOfTurtlesByVariablesInExistingNetLogoModelHeadlessInvalidMaxDistance() {

        HeadlessWorkspace workspace =
            HeadlessWorkspace.newInstance() ;
        try {
          // Load model
          workspace.open(TESTMODEL, false);
          // Parametrise agent numbers
          workspace.command("pre-setup");
          // Initialise agents
          workspace.command("setup");
          // Run clustering with negative maximum distance
          workspace.command("set clusters dbscan:cluster-by-variable agents \"wealth\" 2 -1");
          workspace.dispose();
          fail("Execution should not reach here.");
        } catch(Exception ex) {
          assertEquals("Invalid input data", 
                  "Extension exception: Maximum distance of cluster variable values cannot be negative.", 
                  ex.getMessage());
        }

    }
    
}
