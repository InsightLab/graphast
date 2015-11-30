package org.graphast.graphgenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.graphast.config.Configuration;
import org.graphast.model.Edge;
import org.graphast.model.EdgeImpl;
import org.graphast.model.GraphBounds;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.model.Node;
import org.graphast.model.NodeImpl;

public class GraphGeneratorGrid {

	private int width;
	private int length;
	private GraphBounds graph;
	private double percentagemPoi;
	private String PATH_GRAPH = Configuration.USER_HOME + "/graphast/test/example";
	
	public GraphGeneratorGrid(int width, int length, double percentualPoi) {
		this.width = width;
		this.length = length;
		this.percentagemPoi = percentualPoi;
		this.graph = new GraphBoundsImpl(PATH_GRAPH);
	}
	
	public void generateGraph() {
		plotNodes();
		plotEdges();
		graph.createBounds();
	}

	private void plotNodes() {
		
		BigDecimal interador_largura = BigDecimal.valueOf(180).divide(BigDecimal.valueOf(width), 8, RoundingMode.HALF_UP);
		BigDecimal interador_altura =  BigDecimal.valueOf(180).divide(BigDecimal.valueOf(length), 8, RoundingMode.HALF_UP);
		
		Set<Integer> listaIdsPoi = getListIdsPois();
		
		Integer category = 0;
		for (int i = 0; i < width; i++) {
			BigDecimal latitude = interador_altura.multiply(BigDecimal.valueOf(i)).add(BigDecimal.valueOf(-90));
			for (int j = 0; j < length; j++) {
				BigDecimal longitude = interador_largura.multiply(BigDecimal.valueOf(j)).add(BigDecimal.valueOf(-90));;
				Node node = new NodeImpl(Long.valueOf(category), latitude.doubleValue(), longitude.doubleValue());
				if(listaIdsPoi.contains(category)) {
					int[] costs = new int[]{0};
					node.setCategory(category);
					node.setLabel("CATEGORY "+category);	
					node.setCosts(costs);
					listaIdsPoi.remove(category);
				}
				graph.addNode(node);
				category++;
			}
		}
	}

	private Set<Integer> getListIdsPois() {
		
		int quantidadeVerticesPoi = BigDecimal.valueOf(width).multiply(BigDecimal.valueOf(length)).multiply(BigDecimal.valueOf(percentagemPoi)).divide(BigDecimal.valueOf(100.0f), 8, RoundingMode.UP).intValue();

		Set<Integer> listIdPoi = new HashSet<>();
		do {
			int rangeMax = width*length - 1;
			Double idRandom = generatePdseurandom(0, rangeMax);
			listIdPoi.add(idRandom.intValue());
		} while(listIdPoi.size()<quantidadeVerticesPoi);
		
		return listIdPoi;
	}
	
	
	private void plotEdges() {
		for (int i = 0; i < width*length - 1; i++) {
			
			if(i < (width * length - width)) {
				
				Edge edgeOutTopDown = new EdgeImpl(Long.valueOf(i).longValue(), Long.valueOf(i+length).longValue(), 0);
				addCost(edgeOutTopDown);
				graph.addEdge(edgeOutTopDown);
				
				Edge edgeInTopDown = new EdgeImpl(Long.valueOf(i+length).longValue(), Long.valueOf(i).longValue(), 0);
				addCost(edgeInTopDown);
				graph.addEdge(edgeInTopDown);
			}

			if(i%width != width - 1) {
			
				Edge edgeOutLeftRight = new EdgeImpl(Long.valueOf(i).longValue(), Long.valueOf(i+1).longValue(), 0);
				addCost(edgeOutLeftRight);
				graph.addEdge(edgeOutLeftRight);
				
				Edge edgeInLeftRight = new EdgeImpl(Long.valueOf(i+1).longValue(), Long.valueOf(i).longValue(), 0);
				addCost(edgeInLeftRight);
				graph.addEdge(edgeInLeftRight);
			
			}
			
		}
	}
	
	private double generatePdseurandom(int rangeMin, int rangeMax) {
		return rangeMin + (rangeMax - rangeMin) * new Random().nextDouble();
	}
	
	private void addCost(Edge edge) {
		
		int[] costs = new int[96];
		
		for (int i : costs) {
			costs[i] = Double.valueOf(generatePdseurandom(1, 1200)).intValue();
		}
		edge.setCosts(costs);
	}

	public boolean isConnected() {
		return Boolean.TRUE;
	}
	
	public GraphBounds getGraph() {
		return graph;
	}
}
