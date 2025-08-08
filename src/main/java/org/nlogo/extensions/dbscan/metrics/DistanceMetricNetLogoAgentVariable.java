package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;

/**
 * Distance metric implementation for agent variables.
 * 
 * Website: https://github.com/chrfrantz/NetLogo-Extension-DBSCAN
 *
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 * @version 0.5 (06.08.2025)
 *
 */
public class DistanceMetricNetLogoAgentVariable implements DistanceMetric<Agent>{

    private final String field;

    public DistanceMetricNetLogoAgentVariable(final String field) {
        // null check is done prior to instantiation 
        // field variable has to be upper case; ensured in calling code
        this.field = field;
    }

    @Override
    public double calculateDistance(final Agent val1, final Agent val2) throws DBSCANClusteringException {
        try {
            // Check on turtle level first ...
            Double val1Num = Double.parseDouble(val1.getTurtleOrLinkVariable(field).toString());
            Double val2Num = Double.parseDouble(val2.getTurtleOrLinkVariable(field).toString());
            return Math.abs(val1Num - val2Num);
        } catch (ArrayIndexOutOfBoundsException | AgentException | NumberFormatException e) {
            try {
                // ... before looking at breed level
                Double val1Num = Double.parseDouble(val1.getBreedVariable(field).toString());
                Double val2Num = Double.parseDouble(val2.getBreedVariable(field).toString());
                return Math.abs(val1Num - val2Num);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | AgentException e1) {
                throw new DBSCANClusteringException(e1.getMessage());
            }
        }
    }

}
