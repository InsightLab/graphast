package org.graphast.importer;

import java.util.Random;

public class CostGenerator {

	public static int[] generateSyntheticEdgesCosts() {

		Random random = new Random();

		int min, max;
		int[] syntheticCosts = new int[96];
		
		for(int i=0; i<24; i++) {
			min = 0;
			max = 300000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=24; i<28; i++) {
			min = 300000;
			max = 420000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=28; i<36; i++) {
			min = 420000;
			max = 720000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=36; i<44; i++) {
			min = 300000;
			max = 420000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=44; i<56; i++) {
			min = 420000;
			max = 720000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=56; i<64; i++) {
			min = 0;
			max = 300000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=64; i<68; i++) {
			min = 300000;
			max = 420000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=68; i<80; i++) {
			min = 420000;
			max = 720000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=80; i<88; i++) {
			min = 300000;
			max = 420000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
		
		for(int i=88; i<96; i++) {
			min = 0;
			max = 300000;
			
			syntheticCosts[i] = random.nextInt(max-min)+min;
			
		}
	
		return syntheticCosts;
	}
	
}
