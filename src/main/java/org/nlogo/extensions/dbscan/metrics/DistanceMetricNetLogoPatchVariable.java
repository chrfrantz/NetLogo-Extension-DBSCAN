package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;

/**
 * Distance metric implementation for patch variables.
 *
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.3 (28.05.2019)
 *
 */
public class DistanceMetricNetLogoPatchVariable implements DistanceMetric<Agent>{

    private Integer field = null;

    public DistanceMetricNetLogoPatchVariable(Integer field) {
        this.field = field;
    }
    
    @Override
    public double calculateDistance(Agent val1, Agent val2) throws DBSCANClusteringException {
        try {
            // Access patch variables by index
            Double val1Num = Double.parseDouble(val1.getPatchVariable(field).toString());
            Double val2Num = Double.parseDouble(val2.getPatchVariable(field).toString());
            return Math.abs(val1Num - val2Num);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | AgentException e) {
            throw new DBSCANClusteringException(e.getClass().getName() + 
            		" when accessing patch variable " + field + ". Message: " + e.getMessage());
        }
    }

}
