package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.api.Agent;
import org.nlogo.api.AgentVariableNumbers;

/**
 * Distance metric implementation for agent locations.
 *
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.3 (28.05.2019)
 *
 */
public class DistanceMetricNetLogoAgentLocation implements DistanceMetric<Agent>{

    public DistanceMetricNetLogoAgentLocation() {}

    @Override
    public double calculateDistance(Agent val1, Agent val2) throws DBSCANClusteringException {

        try {
            return StrictMath.sqrt(StrictMath.pow((double)val1.getVariable(AgentVariableNumbers.VAR_YCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_YCOR), 2.0) + 
        			StrictMath.pow((double)val1.getVariable(AgentVariableNumbers.VAR_XCOR) - (double)val2.getVariable(AgentVariableNumbers.VAR_XCOR), 2.0));
        } catch (NumberFormatException e) {
            throw new DBSCANClusteringException(e.getMessage() + "; Agent Coordinates: " +
            		"Agent1 x variable: " + val1.getVariable(AgentVariableNumbers.VAR_XCOR) + ", " + 
            		"Agent2 x variable: " + val2.getVariable(AgentVariableNumbers.VAR_XCOR) + ", " + 
            		"Agent1 y variable: " + val1.getVariable(AgentVariableNumbers.VAR_YCOR) + ", " + 
            		"Agent2 y variable: " + val2.getVariable(AgentVariableNumbers.VAR_YCOR) + "."
            		);
        }
    }

}
