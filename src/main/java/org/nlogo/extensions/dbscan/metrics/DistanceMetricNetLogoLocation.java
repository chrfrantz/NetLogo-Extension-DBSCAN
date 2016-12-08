package org.nlogo.extensions.dbscan.metrics;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.nlogo.agent.Agent;
import org.nlogo.agent.Turtle;
import org.nlogo.api.AgentException;

public class DistanceMetricNetLogoLocation implements DistanceMetric<Turtle>{

	private static final String X_COORDINATE = "XCOR";
	private static final String Y_COORDINATE = "YCOR";
	
	public DistanceMetricNetLogoLocation() {
	}
	
	@Override
	public double calculateDistance(Turtle val1, Turtle val2) throws DBSCANClusteringException {
		
		try {
			Double val1XNum = Double.parseDouble(val1.getTurtleOrLinkVariable(X_COORDINATE).toString());
			Double val1YNum = Double.parseDouble(val1.getTurtleOrLinkVariable(Y_COORDINATE).toString());
			Double val2XNum = Double.parseDouble(val2.getTurtleOrLinkVariable(X_COORDINATE).toString());
			Double val2YNum = Double.parseDouble(val2.getTurtleOrLinkVariable(Y_COORDINATE).toString());
			
			return StrictMath.sqrt(StrictMath.pow(val1YNum - val2YNum, 2.0) + StrictMath.pow(val1XNum - val2XNum, 2.0));
		} catch (NumberFormatException e) {
			throw new DBSCANClusteringException(e.getMessage());
		}
	}

}
