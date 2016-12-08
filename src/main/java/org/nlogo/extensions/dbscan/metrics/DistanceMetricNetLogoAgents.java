package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;

public class DistanceMetricNetLogoAgents implements DistanceMetric<Agent>{

	private String field = null;
	
	public DistanceMetricNetLogoAgents(String field) {
		this.field = field;
	}
	
	@Override
	public double calculateDistance(Agent val1, Agent val2) throws DBSCANClusteringException {
		try {
			// Check on turtle level first ...
			Double val1Num = Double.parseDouble(val1.getTurtleOrLinkVariable(field).toString());
			Double val2Num = Double.parseDouble(val2.getTurtleOrLinkVariable(field).toString());
			return Math.abs(val1Num - val2Num);
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				// ... before looking at breed level
				Double val1Num = Double.parseDouble(val1.getBreedVariable(field).toString());
				Double val2Num = Double.parseDouble(val2.getBreedVariable(field).toString());
				return Math.abs(val1Num - val2Num);
			} catch (NumberFormatException | AgentException e1) {
	        	throw new DBSCANClusteringException(e1.getMessage());
			}
        } catch (NumberFormatException | AgentException e) {
        	throw new DBSCANClusteringException(e.getMessage());
		}
	}

}
