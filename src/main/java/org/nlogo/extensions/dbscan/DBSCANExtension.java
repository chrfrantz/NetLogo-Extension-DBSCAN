package org.nlogo.extensions.dbscan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.nlogo.agent.Agent;
import org.nlogo.agent.Turtle;
import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.Syntax;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgents;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoLocation;

/**
 * NetLogo extension for clustering based on DBSCAN by agent variables or coordinates.
 * 
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 *
 */
public class DBSCANExtension extends DefaultClassManager {

    @Override
    public void load(PrimitiveManager primitiveManager) throws ExtensionException {
        primitiveManager.addPrimitive("cluster-by-variable", new DbscanNetlogoVariableClusterer());
        primitiveManager.addPrimitive("cluster-by-location", new DbscanNetlogoCoordinateClusterer());
    }

    /**
     * Clusters based on agent variables
     * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
     */
    public static class DbscanNetlogoVariableClusterer extends DefaultReporter {

        @Override
        public Syntax getSyntax() {
            //Inputs: values to be clustered, property to be clustered on, minimum number of elements, maximum distance
            int[] input = new int[] {Syntax.AgentsetType(), Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType()}; 
            int ret = Syntax.ListType();
            return Syntax.reporterSyntax(input, ret);
        }

        @Override
        public Object report(Argument[] args, Context ctx)
                throws ExtensionException, LogoException {

            AgentSet inputValues = null;
            String field = null;
            int minNumberOfElements = -1;
            double maxDistance = -1;
            LogoListBuilder list = new LogoListBuilder();

            try {
                inputValues = args[0].getAgentSet();
                field = args[1].getString();
                minNumberOfElements = args[2].getIntValue();
                maxDistance = args[3].getDoubleValue();
            } catch (Exception e) {
                throw new ExtensionException(e.getMessage());
            }

            if (minNumberOfElements == -1) {
                throw new ExtensionException("Minimum number of cluster elements has not been defined.");
            }

            if (maxDistance == -1) {
                throw new ExtensionException("Maximum distance of cluster variable has not been defined.");
            }

            // Convert input agentset to collection
            Collection<Agent> inputCollection = new ArrayList<>();
            Iterator<?> it = inputValues.agents().iterator();
            while (it.hasNext()) {
                inputCollection.add((Agent) it.next());
            }

            // Perform clustering
            ArrayList<ArrayList<Agent>> tmpList = null;

            try {
                DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(inputCollection, minNumberOfElements, maxDistance, new DistanceMetricNetLogoAgents(field.toUpperCase())); 
                tmpList = clusterer.performClustering();
            } catch (DBSCANClusteringException e) {
                throw new ExtensionException(e);
            }

            // Convert generated lists of clusters to nested LogoList
            for (ArrayList<Agent> intList: tmpList) {
                LogoListBuilder internalBuilder = new LogoListBuilder();
                for (Agent agent: intList) {
                    internalBuilder.add(agent);
                }
                list.add(internalBuilder.toLogoList());
            }
            return list.toLogoList();
        }
    }

    /**
     * Clusters based on coordinates
     * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
     *
     */
    public static class DbscanNetlogoCoordinateClusterer extends DefaultReporter {

        @Override
        public Syntax getSyntax() {
            //Inputs: values to be clustered, minimum number of elements, maximum distance
            int[] input = new int[] {Syntax.AgentsetType(), Syntax.NumberType(), Syntax.NumberType()}; 
            int ret = Syntax.ListType();
            return Syntax.reporterSyntax(input, ret);
        }

        @Override
        public Object report(final Argument[] args, final Context ctx)
                throws ExtensionException, LogoException {

            AgentSet inputValues = null;
            int minNumberOfElements = -1;
            double maxDistance = -1;
            LogoListBuilder list = new LogoListBuilder();

            try {
                inputValues = args[0].getAgentSet();
                minNumberOfElements = args[1].getIntValue();
                maxDistance = args[2].getDoubleValue();
            } catch (Exception e) {
                throw new ExtensionException(e.getMessage());
            }

            if (minNumberOfElements == -1) {
                throw new ExtensionException("Minimum number of cluster elements has not been defined.");
            }

            if (maxDistance == -1) {
                throw new ExtensionException("Maximum distance of cluster variable has not been defined.");
            }

            // Convert input agentset to collection
            Collection<Turtle> inputCollection = new ArrayList<>();
            Iterator<?> it = inputValues.agents().iterator();
            while (it.hasNext()) {
                inputCollection.add((Turtle) it.next());
            }

            // Perform clustering
            ArrayList<ArrayList<Turtle>> tmpList = null;

            try {
                DBSCANClusterer<Turtle> clusterer = new DBSCANClusterer<Turtle>(inputCollection, minNumberOfElements, maxDistance, new DistanceMetricNetLogoLocation()); 
                tmpList = clusterer.performClustering();
            } catch (DBSCANClusteringException e) {
                throw new ExtensionException(e);
            }

            // Convert generated lists of clusters to nested LogoList
            for (ArrayList<Turtle> intList: tmpList) {
                LogoListBuilder internalBuilder = new LogoListBuilder();
                for (Turtle agent: intList) {
                    internalBuilder.add(agent);
                }
                list.add(internalBuilder.toLogoList());
            }
            return list.toLogoList();
        }
    }

}
