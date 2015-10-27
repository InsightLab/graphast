package org.graphast.importer;

import java.util.Random;

import org.graphast.model.Graph;

public class CostGenerator {

	public static int[] generateSyntheticEdgesCosts(int distance) {

		Random random = new Random();

		int minSpeed, maxSpeed;
		int[] syntheticCosts = new int[96];
		
		for(int i=0; i<24; i++) {
			minSpeed = 14;
			maxSpeed = 17;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);
			
		}

		for(int i=24; i<28; i++) {
			minSpeed = 6;
			maxSpeed = 9;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=28; i<36; i++) {
			minSpeed = 1;
			maxSpeed = 4;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=36; i<44; i++) {
			minSpeed = 6;
			maxSpeed = 9;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=44; i<56; i++) {
			minSpeed = 1;
			maxSpeed = 4;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=56; i<64; i++) {
			minSpeed = 14;
			maxSpeed = 17;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=64; i<68; i++) {
			minSpeed = 6;
			maxSpeed = 9;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=68; i<80; i++) {
			minSpeed = 1;
			maxSpeed = 4;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=80; i<88; i++) {
			minSpeed = 6;
			maxSpeed = 9;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		for(int i=88; i<96; i++) {
			minSpeed = 14;
			maxSpeed = 17;

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		return syntheticCosts;
	}

	public static void generateAllSyntheticEdgesCosts(Graph graph) {
		for (int i = 0; i < graph.getNumberOfEdges(); i++) {
			
			graph.setEdgeCosts(i, CostGenerator.generateSyntheticEdgesCosts(graph.getEdge(i).getDistance()));

		}
	}

}
