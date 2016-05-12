package org.graphast.model;

import static org.junit.Assert.assertEquals;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.contraction.CHGraph;
import org.graphast.model.contraction.CHNode;
import org.junit.BeforeClass;
import org.junit.Test;

public class CHGraphTest {

	private static CHGraph graphHopperExample;
	
	@BeforeClass
	public static void setup(){
		
		graphHopperExample = new GraphGenerator().generateExampleCH();
		
	}
	
	@Test
	public void getNodeExternalIDTest() {
		
		assertEquals(0, graphHopperExample.getNode(0).getExternalId());
		assertEquals(8, graphHopperExample.getNode(8).getExternalId());
		assertEquals(9, graphHopperExample.getNode(9).getExternalId());
		
	}

	@Test
	public void findShortcutTest() {
		
		CHNode n = graphHopperExample.getNode(0);
		
//		graphHopperExample.findShortcut(n);
		
		graphHopperExample.prepareNodes();
		
		graphHopperExample.contractNodes();
		
		graphHopperExample.getNumberOfNodes();
		graphHopperExample.getNumberOfEdges();
//		
//		graphHopperExample.getEdge(0);
//		
//		graphHopperExample.calculatePriority(n);

	}
	
}
