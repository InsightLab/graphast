package org.graphast.importer;

import java.util.Random;

import org.graphast.model.Graph;


public class CostGenerator {

	public static int[] generateSyntheticEdgesCosts(int distance) {

		Random random = new Random();

		int minSpeed, maxSpeed;	 //Millimeters Per Millisecond (mm/ms)	
		int[] syntheticCosts = new int[96];
		
		//1:00h to 6:00h
		for(int i=0; i<24; i++) {
			minSpeed = 14; // 50km/h
			maxSpeed = 17; // 60km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);
			
		}

		//6:00h to 7:00h
		for(int i=24; i<28; i++) {
			minSpeed = 6; //21km/h
			maxSpeed = 9; //32km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//7:00h to 9:00h
		for(int i=28; i<36; i++) {
			minSpeed = 1; //3km/h
			maxSpeed = 4; //14km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//9:00h to 11:00h
		for(int i=36; i<44; i++) {
			minSpeed = 6; //21km/h
			maxSpeed = 9; //32km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//11:00h to 14:00h
		for(int i=44; i<56; i++) {
			minSpeed = 1; //3km/h
			maxSpeed = 4; //14km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//14:00h to 16:00h
		for(int i=56; i<64; i++) {
			minSpeed = 14; //50km/h
			maxSpeed = 17; //60km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//16:00h to 17:00h
		for(int i=64; i<68; i++) {
			minSpeed = 6; //21km/h
			maxSpeed = 9; //30/km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//17:00h to 20:00h
		for(int i=68; i<80; i++) {
			minSpeed = 1; //3km/h
			maxSpeed = 4; //14km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//20:00h to 22:00h
		for(int i=80; i<88; i++) {
			minSpeed = 6; //21km/h
			maxSpeed = 9; //30/km/h

			syntheticCosts[i] = distance/(random.nextInt(maxSpeed-minSpeed)+minSpeed);

		}

		//22:00h to 00:00h
		for(int i=88; i<96; i++) {
			minSpeed = 14; //50km/h
			maxSpeed = 17; //60km/h

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