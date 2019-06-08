package org.nlogo.extensions.dbscan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.nlogo.agent.Agent;
import org.nlogo.agent.Patch;
import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.WorldDimensions;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentVariable;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoPatchLocation;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoPatchVariable;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentLocationBox;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentLocationHorizontalCylinder;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentLocationTorus;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoAgentLocationVerticalCylinder;

/**
 * NetLogo extension for clustering based on DBSCAN by agent/patch variables or coordinates.
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 * 
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.4 (08.06.2019)
 *
 */
public class DBSCANExtension extends DefaultClassManager {

    @Override
    public void load(PrimitiveManager primitiveManager) throws ExtensionException {
        primitiveManager.addPrimitive("cluster-by-variable", new DbscanNetLogoVariableClusterer());
        primitiveManager.addPrimitive("cluster-by-location", new DbscanNetLogoCoordinateClusterer());
    }

    /**
     * Clusters agents (turtles, patches) based on variables.
     * 
     * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
     */
    public static class DbscanNetLogoVariableClusterer implements Reporter {

        @Override
        public Syntax getSyntax() {
            // Inputs: values to be clustered, property to be clustered on, minimum number of elements, maximum distance
            int[] input = new int[] {Syntax.AgentsetType(), Syntax.StringType(), Syntax.NumberType(), Syntax.NumberType()}; 
            int ret = Syntax.ListType();
            return SyntaxJ.reporterSyntax(input, ret);
        }

        @Override
        public Object report(final Argument[] args, final Context ctx)
                throws ExtensionException, LogoException {

            AgentSet inputValues = null;
            String field = null;
            int minNumberOfElements = Integer.MIN_VALUE; // default to invalid value
            double maxDistance = Integer.MIN_VALUE; // default to invalid value

            try {
                inputValues = args[0].getAgentSet();
                field = args[1].getString();
                minNumberOfElements = args[2].getIntValue();
                maxDistance = args[3].getDoubleValue();
            } catch (Exception e) {
                throw new ExtensionException(e.getMessage());
            }

            if (inputValues == null || inputValues.isEmpty()) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_INPUT_DATA);
            }

            if (field == null || field.isEmpty()) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_CLUSTER_VARIABLE);
            }

            if (minNumberOfElements == Integer.MIN_VALUE) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_MINIMUM_NUMBER_OF_ELEMENTS);
            }

            if (minNumberOfElements <= 1) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_INVALID_MINIMUM_NUMBER_OF_ELEMENTS);
            }

            if (maxDistance == Integer.MIN_VALUE) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_MAXIMUM_DISTANCE_OF_ELEMENTS);
            }

            if (maxDistance < 0) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_NEGATIVE_MAXIMUM_DISTANCE_OF_ELEMENTS);
            }

            // Convert input AgentSet to Collection
            Iterator<?> it = inputValues.agents().iterator();

            // Check for patches
            boolean patches = false;
            int fieldIndex = -1;
            Object first = it.hasNext() ? it.next() : null;
            if (first == null) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_NULL_INPUT_DATA);
            }
            if (first.getClass().equals(Patch.class)) {
                patches = true;
                // if you modify the assignment of fieldIndex (and want to maintain NetLogo 5 compatibility), 
                // review the corresponding token in the maven-replacer plugin in pom-v5.xml
                fieldIndex = ctx.world().program().patchesOwn().toList().indexOf(field.toUpperCase());
                if (fieldIndex <= -1) {
                    switch (fieldIndex) {
                        // Default return value if not found
                        case -1: throw new ExtensionException(DBSCANExtensionErrors.errorVariableCouldNotBeFound(field.toUpperCase()));
                        // Consider indicative for NetLogo 5. fieldIndex will be modified accordingly in build process
                        case -2: throw new ExtensionException(DBSCANExtensionErrors.ERROR_CLUSTER_PATCHES_NOT_SUPPORTED_IN_NETLOGO_5);
                        // should never happen, but let's deal with it gracefully
                        default: throw new ExtensionException(DBSCANExtensionErrors.errorVariableCouldNotBeFound(field.toUpperCase()));
                    }
                }
            }

            // Add all agents/patches (including first element) to collection
            Collection<Agent> inputCollection = new ArrayList<>();
            inputCollection.add((Agent)first);
            while (it.hasNext()) {
                inputCollection.add((Agent) it.next());
            }

            // Perform clustering
            ArrayList<ArrayList<Agent>> tmpList = null;

            try {
                DBSCANClusterer<Agent> clusterer = new DBSCANClusterer<Agent>(
                        inputCollection, minNumberOfElements, maxDistance, 
                        (patches ? 
                            // For patches, use patch-specific access methods 
                            new DistanceMetricNetLogoPatchVariable(fieldIndex) : 
                            // ... treat all others as non-patch agents. 
                            new DistanceMetricNetLogoAgentVariable(field.toUpperCase()))); 
                // will at least return empty list (not be null)
                tmpList = clusterer.performClustering();
            } catch (DBSCANClusteringException e) {
                throw new ExtensionException(e);
            }

            // Convert generated lists of clusters to nested LogoList
            LogoListBuilder list = new LogoListBuilder();

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
     * Clusters agents (turtles, patches) based on coordinates.
     * 
     * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
     *
     */
    public static class DbscanNetLogoCoordinateClusterer implements Reporter {

        @Override
        public Syntax getSyntax() {
            // Inputs: values to be clustered, minimum number of elements, maximum distance
            int[] input = new int[] {Syntax.AgentsetType(), Syntax.NumberType(), Syntax.NumberType()};
            int ret = Syntax.ListType();
            return SyntaxJ.reporterSyntax(input, ret);
        }

        @Override
        public Object report(final Argument[] args, final Context ctx)
                throws ExtensionException, LogoException {

            AgentSet inputValues = null;
            int minNumberOfElements = Integer.MIN_VALUE;
            double maxDistance = Integer.MIN_VALUE;

            try {
                inputValues = args[0].getAgentSet();
                minNumberOfElements = args[1].getIntValue();
                maxDistance = args[2].getDoubleValue();
            } catch (Exception e) {
                throw new ExtensionException(e.getMessage());
            }

            if (inputValues == null || inputValues.isEmpty()) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_INPUT_DATA);
            }

            if (minNumberOfElements == Integer.MIN_VALUE) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_MINIMUM_NUMBER_OF_ELEMENTS);
            }

            if (minNumberOfElements <= 1) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_INVALID_MINIMUM_NUMBER_OF_ELEMENTS);
            }

            if (maxDistance == Integer.MIN_VALUE) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MISSING_MAXIMUM_DISTANCE_OF_ELEMENTS);
            }

            if (maxDistance < 0) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_NEGATIVE_MAXIMUM_DISTANCE_OF_ELEMENTS);
            }

            if (ctx == null || ctx.world() == null) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_MODEL_CONTEXT_INACCESSIBLE);
            }

            // Convert input AgentSet to Collection
            Iterator<?> it = inputValues.agents().iterator();

            // Check for patches
            boolean patches = false;
            Object first = it.hasNext() ? it.next() : null;
            if (first == null) {
                throw new ExtensionException(DBSCANExtensionErrors.ERROR_NULL_INPUT_DATA);
            }
            if (first.getClass().equals(Patch.class)) {
                patches = true;
            }

            // Add all agents/patches (including first element) to collection
            Collection<org.nlogo.api.Agent> inputCollection = new ArrayList<>();
            inputCollection.add((org.nlogo.api.Agent)first);
            while (it.hasNext()) {
                inputCollection.add((org.nlogo.api.Agent) it.next());
            }

            // Perform clustering
            ArrayList<ArrayList<org.nlogo.api.Agent>> tmpList = null;
            
            // Retrieve world dimensions to determine topology - access to world() has been checked above
            WorldDimensions dim = ctx.world().getDimensions();

            try {
                DBSCANClusterer<org.nlogo.api.Agent> clusterer = new DBSCANClusterer<org.nlogo.api.Agent>(
                        inputCollection, minNumberOfElements, maxDistance, 
                        ( patches ? 
                            // For patches, use patch-specific coordinate fields
                            new DistanceMetricNetLogoPatchLocation() :
                            // ... treat all others as non-patch agents, and consider topologies
                            // Torus
                            (dim.wrappingAllowedInX() && dim.wrappingAllowedInY() ? new DistanceMetricNetLogoAgentLocationTorus(dim.width(), dim.height()) :
                            // Vertical Cylinder
                            (dim.wrappingAllowedInX() ? new DistanceMetricNetLogoAgentLocationVerticalCylinder(dim.width()) :
                            // Horizontal Cylinder
                            (dim.wrappingAllowedInY() ? new DistanceMetricNetLogoAgentLocationHorizontalCylinder(dim.height()) :
                            // Box
                            new DistanceMetricNetLogoAgentLocationBox())))
                        )
                    );
                // will at least return empty list (not be null)
                tmpList = clusterer.performClustering();
            } catch (DBSCANClusteringException e) {
                throw new ExtensionException(e);
            }

            // Convert generated lists of clusters to nested LogoList
            LogoListBuilder list = new LogoListBuilder();

            for (ArrayList<org.nlogo.api.Agent> intList: tmpList) {
                LogoListBuilder internalBuilder = new LogoListBuilder();
                for (org.nlogo.api.Agent agent: intList) {
                    internalBuilder.add(agent);
                }
                list.add(internalBuilder.toLogoList());
            }
            return list.toLogoList();
        }
    }

}
