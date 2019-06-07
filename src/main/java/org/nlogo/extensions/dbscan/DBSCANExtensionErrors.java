package org.nlogo.extensions.dbscan;

/**
 * Error messages for DBSCAN extension.
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 * 
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.4 (08.06.2019)
 *
 */
public final class DBSCANExtensionErrors {

    public static final String ERROR_MISSING_INPUT_DATA = "Clustering input data (e.g., turtles, patches) has not been provided (or is empty).";
    public static final String ERROR_NULL_INPUT_DATA = "Clustering input data (e.g., turtles, patches) contains null values.";
    public static final String ERROR_MISSING_CLUSTER_VARIABLE = "Cluster variable has not been specified.";
    public static final String ERROR_INVALID_MINIMUM_NUMBER_OF_ELEMENTS = "Minimum number of cluster elements cannot be smaller than 2.";
    public static final String ERROR_MISSING_MINIMUM_NUMBER_OF_ELEMENTS = "Minimum number of cluster elements has not been specified.";
    public static final String ERROR_NEGATIVE_MAXIMUM_DISTANCE_OF_ELEMENTS = "Maximum distance of cluster variable values cannot be negative.";
    public static final String ERROR_MISSING_MAXIMUM_DISTANCE_OF_ELEMENTS = "Maximum distance of cluster variable values has not been specified.";
    public static final String ERROR_MODEL_CONTEXT_INACCESSIBLE = "NetLogo model context could not be accessed.";
    public static final String ERROR_CLUSTER_PATCHES_NOT_SUPPORTED_IN_NETLOGO_5 = "Clustering of patches by variables is not supported for NetLogo 5 version of the DBSCAN extension. " +
            "Upgrade to NetLogo 6 or higher to use this feature.";

    public static final String errorVariableCouldNotBeFound(String variable) {
        return "Patch variable " + variable + " could not be found.";
    }

}
