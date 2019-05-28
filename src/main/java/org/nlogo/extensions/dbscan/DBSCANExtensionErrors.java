package org.nlogo.extensions.dbscan;

/**
 * Error messages for DBSCAN extension.
 * 
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.3 (28.05.2019)
 *
 */
public final class DBSCANExtensionErrors {

	public static final String ERROR_MISSING_INPUT_DATA = "Clustering input data (e.g., turtles, patches) has not been provided (or is empty).";
	public static final String ERROR_MISSING_CLUSTER_VARIABLE = "Cluster variable has not been specified.";
	public static final String ERROR_MISSING_MINIMUM_NUMBER_OF_ELEMENTS = "Minimum number of cluster elements has not been specified.";
	public static final String ERROR_MISSING_MAXIMUM_DISTANCE_OF_ELEMENTS = "Maximum distance of cluster variable values has not been specified.";
	public static final String ERROR_CLUSTER_PATCHES_NOT_SUPPORTED_IN_NETLOGO_5 = "Clustering of patches by variables is not supported for NetLogo 5 version of the DBSCAN extension. " +
			"Upgrade to NetLogo 6 or higher to use this feature.";
	
	public static final String errorVariableCouldNotBeFound(String variable) {
		return "Patch variable " + variable + " could not be found.";
	}
		
}
