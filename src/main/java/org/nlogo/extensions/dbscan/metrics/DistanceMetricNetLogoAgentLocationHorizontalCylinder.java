package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.api.Agent;
import org.nlogo.api.AgentVariableNumbers;

/**
 * Distance metric implementation for agent locations for horizontal cylinder topology.
 * (y-wrapping enabled, x-wrapping disabled)
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 *
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.4 (08.06.2019)
 *
 */
public class DistanceMetricNetLogoAgentLocationHorizontalCylinder implements DistanceMetric<Agent>{

    private final int height;

    public DistanceMetricNetLogoAgentLocationHorizontalCylinder(final int height) {
        this.height = height;
    }

    @Override
    public double calculateDistance(Agent val1, Agent val2) throws DBSCANClusteringException {

        try {
            return StrictMath.sqrt(
                    StrictMath.pow((double)val1.getVariable(AgentVariableNumbers.VAR_XCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_XCOR), 2) + 
                    StrictMath.pow(StrictMath.min(StrictMath.abs((double)val1.getVariable(AgentVariableNumbers.VAR_YCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_YCOR)), 
                            height - StrictMath.abs((double)val1.getVariable(AgentVariableNumbers.VAR_YCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_YCOR))), 2)
                    );
        } catch (NumberFormatException e) {
            throw new DBSCANClusteringException(e.getMessage() + "; Agent Coordinates: " +
                    "Agent1 x variable: " + val1.getVariable(AgentVariableNumbers.VAR_XCOR) + ", " + 
                    "Agent2 x variable: " + val2.getVariable(AgentVariableNumbers.VAR_XCOR) + ", " + 
                    "Agent1 y variable: " + val1.getVariable(AgentVariableNumbers.VAR_YCOR) + ", " + 
                    "Agent2 y variable: " + val2.getVariable(AgentVariableNumbers.VAR_YCOR) + "." + 
                    System.getProperty("line.separator") + 
                    "World topology: horizonal cylinder"
                    );
        }
    }

}
