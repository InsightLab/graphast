package org.graphast.query.route.osr;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.GraphBounds;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BoundsRouteTest {
	private static GraphBounds graphMonaco;

	@BeforeClass
	public static void setup(){
		graphMonaco = new GraphGenerator().generateMonaco();
	}

	@Test
	public void saveLoad() {
		BoundsRoute br = new BoundsRoute(graphMonaco, (short)0);
		br.createBounds();
		br.save();
		
		BoundsRoute br2 = new BoundsRoute(graphMonaco, (short)0); 
		br2.load();
		
		assertEquals(br.getBounds().get(0l), br2.getBounds().get(0l));
		assertEquals(br.getBounds().get(1l), br2.getBounds().get(1l));
	}
	
}
