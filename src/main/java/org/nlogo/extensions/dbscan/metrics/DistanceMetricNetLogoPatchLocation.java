package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.api.Agent;
import org.nlogo.api.AgentVariableNumbers;

/**
 * Distance metric implementation for patch locations.
 * 
 * While clustering patches by location does not really make sense,
 * perhaps future NetLogo features (e.g., sparse patch matrices) may warrant
 * the use.
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 *
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.5 (06.08.2025)
 *
 */
public class DistanceMetricNetLogoPatchLocation implements DistanceMetric<Agent>{

    public DistanceMetricNetLogoPatchLocation() {}

    @Override
    public double calculateDistance(final Agent val1, final Agent val2) throws DBSCANClusteringException {

        try {
            return StrictMath.sqrt(
                    StrictMath.pow((double)val1.getVariable(AgentVariableNumbers.VAR_PXCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_PXCOR), 2.0) +
                    StrictMath.pow((double)val1.getVariable(AgentVariableNumbers.VAR_PYCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_PYCOR), 2.0)
                    );
        } catch (NumberFormatException e) {
            throw new DBSCANClusteringException(e.getMessage() + "; Patch Coordinates: " +
                    "Patch1 x variable: " + val1.getVariable(AgentVariableNumbers.VAR_PXCOR) + ", " + 
                    "Patch2 x variable: " + val2.getVariable(AgentVariableNumbers.VAR_PXCOR) + ", " + 
                    "Patch1 y variable: " + val1.getVariable(AgentVariableNumbers.VAR_PYCOR) + ", " + 
                    "Patch2 y variable: " + val2.getVariable(AgentVariableNumbers.VAR_PYCOR) + "."
                    );
        }
    }

}
