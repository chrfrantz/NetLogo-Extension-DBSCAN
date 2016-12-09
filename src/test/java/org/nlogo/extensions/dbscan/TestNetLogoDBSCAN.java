package org.nlogo.extensions.dbscan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.junit.Test;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;
import org.nlogo.api.LogoList;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.extensions.dbscan.metrics.DistanceMetricNetLogoTurtles;
import org.nlogo.headless.HeadlessWorkspace;

/**
 * Tests DBSCAN clusterer using an existing model and injecting agents to be clustered.
 * 
 * @author Christopher Frantz <cf@christopherfrantz.org>
 *
 */

public class TestNetLogoDBSCAN {
	
	@Test
	public void testClusteringInExistingNetLogoModelFromJava() {
		
		final String TESTFILE = System.getProperty("user.dir") + 
				"/src/test/resources/java-netlogo-clustering-test-v5.nlogo";
		
		Random random = new Random(4522);
		
		final String AGENTS = "AGENTS";
		final String VARIABLE = "WEALTH";
		
		HeadlessWorkspace ws = HeadlessWorkspace.newInstance();
		
		try {
			ws.openString(org.nlogo.util.Utils.url2String("file://" + TESTFILE)); //System.getProperty("user.dir") + "/test/resources/java-netlogo-test.nlogo"));
		} catch (Exception e1) {
			fail("Problems when opening NetLogo model file: " + e1.getMessage());
		}
		
		World world = ws.world();
		
		LogoListBuilder b = new LogoListBuilder();
		AgentSet breed = world.getBreed(AGENTS);
		
		int i = 0;
		while (i < 100) {
			Turtle turtle = world.createTurtle(breed);
			b.add(turtle);
			i++;
		}
		
		Collection<Turtle> l2 = new ArrayList<>();
		
		LogoList l1 = b.toLogoList();
		
		for (int j=0; j<l1.size(); j++) {
			Turtle t = ((Turtle)l1.get(j));
			
			try {
				t.setBreedVariable(VARIABLE, random.nextInt(1000));
			} catch (AgentException e1) {
				fail("Error setting variable value: " + e1);
			}
			
			System.out.print("ID: " + t.id + ", Wealth: ");
			
			try {
				System.out.println(t.getBreedVariable(VARIABLE));
			} catch (AgentException e) {
				fail("Error getting variable value: " + e);
			}
			
			l2.add(t);
		}
		
		int minCluster = 3;
		double maxDistance = 5;
		
		ArrayList<ArrayList<Turtle>> result = null;
		
		try {
			DBSCANClusterer<Turtle> clusterer = new DBSCANClusterer<Turtle>(l2, minCluster, maxDistance, new DistanceMetricNetLogoTurtles(VARIABLE));
			result = clusterer.performClustering();
		} catch (DBSCANClusteringException e) {
			fail("Error during clustering operation: " + e);
		}
		
		LogoList res = LogoList.fromJava(result);
		
		System.out.println("Resulting clusters: " + res);
		
		assertEquals("Number of Clusters equals", 12, res.size());
	}

}
