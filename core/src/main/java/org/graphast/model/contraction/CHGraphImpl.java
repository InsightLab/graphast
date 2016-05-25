package org.graphast.model.contraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.graphast.config.Configuration;
import org.graphast.model.Edge;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.ShortestPathService;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;

public class CHGraphImpl extends GraphImpl implements CHGraph {

	private IntBigArrayBigList nodesComplement;

	private IntBigArrayBigList edgesComplement;

	Map<Long, List<CHEdge>> possibleShortcuts = new HashMap<>();

	private Queue<CHNodeImpl> nodePriorityQueue = new PriorityQueue<>();

	private Map<Long, Integer> oldPriorities = new HashMap<>();

	private Set<CHNode> hyperPoIsSet = new HashSet<>();

	private int neighborUpdatePercentage = 20;
	private int periodicUpdatesPercentage = 20;
	private int lastNodesLazyUpdatePercentage = 10;
	private double nodesContractedPercentage = 100;
	private long counter;
	private double meanDegree;
	private final Random rand = new Random(123);
	private int regularNodeHighestPriority = Integer.MIN_VALUE;

	private CHGraph reverseGraph;

	private ShortestPathService shortestPath;
	private int maximumEdgeCount, maxLevel, minLevelPoI;

	public CHGraphImpl() {
		super();
	}

	public CHGraphImpl(String directory) {
		super(directory);

		nodesComplement = new IntBigArrayBigList();
		edgesComplement = new IntBigArrayBigList();

	}

	public void addNode(CHNode n) {
		super.addNode(n);

		synchronized (nodesComplement) {

			nodesComplement.add(n.getPriority());
			nodesComplement.add(n.getLevel());

		}

	}

	public void updateNodeInfo(CHNode n, int maxLevel, int priority) {

		super.updateNodeInfo(n);

		long position = n.getId() * CHNode.NODE_BLOCKSIZE;

		nodesComplement.set(position, priority);
		nodesComplement.set(position + 1, maxLevel);

	}

	// public void updateNodeInfo(Node n) {
	//
	// NodeImpl node = (NodeImpl) n;
	//
	// long labelIndex = storeLabel(node.getLabel(), nodesLabels);
	// node.setLabelIndex(labelIndex);
	// long costsIndex = storeCosts(node.getCosts(), nodesCosts);
	// node.setCostsIndex(costsIndex);
	//
	// long position = node.getId() * Node.NODE_BLOCKSIZE;
	// position = position + 2;
	//
	// synchronized (nodes) {
	// nodes.set(position++, node.getCategory());
	// nodes.set(position++, node.getLatitudeConvertedToInt());
	// nodes.set(position++, node.getLongitudeConvertedToInt());
	// nodes.set(position++, node.getFirstEdgeSegment());
	// nodes.set(position++, node.getFirstEdgeOffset());
	// nodes.set(position++, node.getLabelIndexSegment());
	// nodes.set(position++, node.getLabelIndexOffset());
	// nodes.set(position++, node.getCostsIndexSegment());
	// nodes.set(position++, node.getCostsIndexOffset());
	//
	// }
	// }

	@Override
	public CHNode getNode(long id) {

		Node node = super.getNode(id);

		CHNode chNode = new CHNodeImpl(node);

		long position = chNode.getId() * CHNode.NODE_BLOCKSIZE;

		chNode.setPriority(nodesComplement.get(position));
		chNode.setLevel(nodesComplement.get(position + 1));
		
		return chNode;

	}

	public void addEdge(CHEdge e) {
		super.addEdge(e);

		synchronized (edgesComplement) {

			edgesComplement.add(e.getOriginalEdgeCounter());
			edgesComplement.add(e.getContractedNodeIdSegment());
			edgesComplement.add(e.getContractedNodeIdOffset());
			edgesComplement.add(e.getIngoingSkippedEdgeSegment());
			edgesComplement.add(e.getIngoingSkippedEdgeOffset());
			edgesComplement.add(e.getOutgoingSkippedEdgeSegment());
			edgesComplement.add(e.getOutgoingSkippedEdgeOffset());

			if (e.isShortcut()) {
				edgesComplement.add(1);
			} else {
				edgesComplement.add(0);
			}

		}

	}

	@Override
	public CHEdge getEdge(long id) {

		Edge edge = super.getEdge(id);

		CHEdge chEdge = new CHEdgeImpl(edge);

		long position = id * CHEdge.EDGE_BLOCKSIZE;

		int originalEdgeCounter = edgesComplement.getInt(position);
		long contractedNodeId = BigArrays.index(edgesComplement.getInt(position + 1),
				edgesComplement.getInt(position + 2));
		long ingoingSkippedEdge = BigArrays.index(edgesComplement.getInt(position + 3),
				edgesComplement.getInt(position + 4));
		long outgoingSkippedEdge = BigArrays.index(edgesComplement.getInt(position + 5),
				edgesComplement.getInt(position + 6));

		boolean isShortcut = true;
		if (edgesComplement.get(position + 7) == 0) {
			isShortcut = false;
		}

		chEdge.setOriginalEdgeCounter(originalEdgeCounter);
		chEdge.setContractedNodeId(contractedNodeId);
		chEdge.setIngoingSkippedEdge(ingoingSkippedEdge);
		chEdge.setOutgoingSkippedEdge(outgoingSkippedEdge);
		chEdge.setShortcut(isShortcut);

		return chEdge;

	}

	public boolean prepareNodes() {

		for (int i = 0; i < this.getNumberOfNodes(); i++) {

			this.updateNodeInfo(this.getNode(i), maxLevel, 0);

		}

		for (int i = 0; i < this.getNumberOfNodes(); i++) {

			long nodeId = i;

			int priority = calculatePriority(this.getNode(nodeId));

			oldPriorities.put(nodeId, priority);
			
			this.updateNodeInfo(this.getNode(nodeId), maxLevel, priority);
			nodePriorityQueue.add((CHNodeImpl)this.getNode(nodeId));
			
		}

//		int j = nodePriorityQueue.size();
//
//		for (int i = 0; i < j; i++) {
//
//			System.out.println("NID: " + nodePriorityQueue.peek().getId() + ", Priority: "
//					+ nodePriorityQueue.peek().getPriority());
//			nodePriorityQueue.poll();
//		}

		if (nodePriorityQueue.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}

	public int calculatePriority(Node n) {

		findShortcut(n);

		int originalEdgeCount = 0;
		for (CHEdge e : possibleShortcuts.get(n.getId())) {
			originalEdgeCount += e.getOriginalEdgeCounter();
		}

		int degree = this.getReverseGraph().getOutEdges(n.getId()).size() + this.getOutEdges(n.getId()).size();
		possibleShortcuts.get(n.getId()).size();
		int numberOfContractedNeighbors = 0;

		for (Long edgeId : this.getReverseGraph().getOutEdges(n.getId())) {
			if (this.getReverseGraph().getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}
		}

		for (Long edgeId : this.getOutEdges(n.getId())) {
			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}
		}

		int edgeDifference = possibleShortcuts.get(n.getId()).size() - degree;

		int hyperPoICoefficient = 0;

		// SOMAR O NUMERO DE ARESTAS E NUMERO DE NOS COMO LOWERBOUNDS
		if (n.getCategory() == 2) {
			// hyperPoICoefficient += 20 * this.getNumberOfEdges() +
			// this.getNumberOfEdges() + this.getNumberOfNodes();
			hyperPoICoefficient += 1000;
		}

		int priority = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors + hyperPoICoefficient;

		// if( n.getCategory() != 2 ) {
		// if(priority > regularNodeHighestPriority) {
		// regularNodeHighestPriority = priority;
		// }
		// } else {
		// //TODO Double check this
		// priority = Math.abs(priority) + regularNodeHighestPriority;
		// }
//
//		System.out.println("NID: " + n.getId() + ", Edge Difference: " + edgeDifference + ", Original Edges Count: "
//				+ originalEdgeCount + ", Contracted Neighbors: " + numberOfContractedNeighbors);
//		System.out.println("\t Priority: " + priority);

		return priority;

	}

	// Consider the following "graph": u --> u --> w
	public void findShortcut(Node n) {

		List<CHEdge> possibleLocalShortcut = new ArrayList<>();

		// Arestas chegando no nó N (equivalente aos outgoings do grafo
		// reverso).
		// Nó TO das arestas outgoings do grafo reverso serão os FROM do grafo
		// original.
		for (Long edgeIngoing : this.getReverseGraph().getOutEdges(n.getId())) {

			Long fromNode = this.getReverseGraph().getEdge(edgeIngoing).getToNode();

			if (this.getNode(fromNode).getLevel() != maxLevel) {
				continue;
			}

			// Arestas outgoing do nó N. O nó TO será o nó final.
			for (Long edgeOutgoing : this.getOutEdges(n.getId())) {

				Long toNode = this.getEdge(edgeOutgoing).getToNode();

				// TODO Explicar essa verificação
				if (this.getNode(toNode).getLevel() != maxLevel || toNode == fromNode) {
					continue;
				}

				long previsousReverseEdgeId = 0l;
				double ingoingDistance = this.getReverseGraph().getEdge(edgeIngoing).getDistance();

				// Arestas de entrada no nó "n"
				for (Long reverseEdge : this.getReverseGraph().getOutEdges(n.getId())) {
					if (this.getReverseGraph().getEdge(reverseEdge).getToNode() == fromNode
							&& this.getReverseGraph().getEdge(reverseEdge).getDistance() <= this.getReverseGraph()
									.getEdge(previsousReverseEdgeId).getDistance()) {
						ingoingDistance = this.getReverseGraph().getEdge(reverseEdge).getDistance();
					}
				}

				long previsousOriginalEdgeId = 0l;
				double outgoingDistance = this.getEdge(edgeOutgoing).getDistance();
				for (Long originalEdge : this.getOutEdges(n.getId())) {
					if (this.getEdge(originalEdge).getToNode() == toNode && this.getEdge(originalEdge)
							.getDistance() <= this.getEdge(previsousOriginalEdgeId).getDistance()) {
						outgoingDistance = this.getEdge(originalEdge).getDistance();
					}
				}

				double shortestPathBeforeContraction = ingoingDistance + outgoingDistance;

				shortestPath = new DijkstraConstantWeight(this);

				Path path;

				try {
					path = shortestPath.shortestPath(this.getNode(fromNode), this.getNode(toNode), n);
				} catch (Exception e) {
					path = null;
				}

				if (path != null && path.getTotalDistance() <= shortestPathBeforeContraction)
					// Witness path found! Continue the search for the next
					// neighbor
					continue;

				// Shortcut must be created
				int ingoingEdgeCounter = this.getReverseGraph().getEdge(edgeIngoing).getOriginalEdgeCounter();
				int outgoingEdgeCounter = this.getEdge(edgeOutgoing).getOriginalEdgeCounter();

				CHEdge shortcut = new CHEdgeImpl(fromNode, toNode, (int) shortestPathBeforeContraction,
						ingoingEdgeCounter + outgoingEdgeCounter, n.getId(),
						this.getReverseGraph().getEdge(edgeIngoing).getId(), this.getEdge(edgeOutgoing).getId(), true);

				// System.out.println("shortcutFrom: " + fromNode
				// + ", shortcutTo: " + toNode
				// + ", Distance: " + (int) shortestPathBeforeContraction + ",
				// originalEdgeCount: "
				// + (ingoingEdgeCounter + outgoingEdgeCounter));

				possibleLocalShortcut.add(shortcut);

			}

		}

		// se o shortcut for criado -> meanDegree = (meanDegree * 2 +
		// tmpDegreeCounter) / 3;

		possibleShortcuts.put(n.getId(), possibleLocalShortcut);

	}

	public void contractNodes() {

		int level = 1;
		//TODO Rename this variable counter!!
		counter = 0;
		int updateCounter = 0;
		meanDegree = this.getNumberOfEdges() / this.getNumberOfNodes();
		
		// preparation takes longer but queries are slightly faster with preparation
        // => enable it but call not so often
        boolean periodicUpdate = true;
        
        long periodicUpdatesCount = Math.round(Math.max(10, nodePriorityQueue.size() / 100d * periodicUpdatesPercentage));
        if (periodicUpdatesPercentage == 0)
            periodicUpdate = false;
        
     // disable lazy updates for last x percentage of nodes as preparation is then a lot slower
        // and query time does not really benefit
        long lastNodesLazyUpdates = Math.round(nodePriorityQueue.size() / 100d * lastNodesLazyUpdatePercentage);

        // according to paper "Polynomial-time Construction of Contraction Hierarchies for Multi-criteria Objectives" by Funke and Storandt
        // we don't need to wait for all nodes to be contracted
        long nodesToAvoidContract = Math.round((100 - nodesContractedPercentage) / 100 * nodePriorityQueue.size());

		// Recompute priority of uncontracted neighbors.
		// Without neighbor updates preparation is faster but we need them
		// to slightly improve query time. Also if not applied too often it
		// decreases the shortcut number.
		boolean neighborUpdate = true;
		if (neighborUpdatePercentage == 0)
			neighborUpdate = false;

		while (!nodePriorityQueue.isEmpty()) {
			
			
			if(periodicUpdate && counter > 0 && counter % periodicUpdatesCount == 0) {
				
				nodePriorityQueue.clear();
				
				for(int nodeId = 0; nodeId < this.getNumberOfNodes(); nodeId++) {
					
					if(this.getNode(nodeId).getLevel() != maxLevel) 
						continue;
					
					int priority = calculatePriority(this.getNode(nodeId));

					oldPriorities.put((long)nodeId, priority);
					//TODO Change "maxLevel"to this.getNode(nodeId).getLevel() or the opposite?
					this.updateNodeInfo(this.getNode(nodeId), this.getNode(nodeId).getLevel(), priority);
					nodePriorityQueue.add((CHNodeImpl)this.getNode(nodeId));
					
//					oldPriorities.put((long)nodeId, priority);

//					CHNode node = this.getNode(nodeId);
//					node.setPriority(priority);
//					//TODO Double check this maxLevel setter. Is it necessary?
//					node.setLevel(maxLevel);

//					nodePriorityQueue.add((CHNodeImpl) node);
					
				}
				updateCounter++;
				if (nodePriorityQueue.isEmpty())
                    throw new IllegalStateException("Cannot prepare as no unprepared nodes where found. Called preparation twice?");
				
			}
			
			
			counter++;
			
			System.out.println("NID: " + nodePriorityQueue.peek().getId() + ", Priority: "
			+ nodePriorityQueue.peek().getPriority());
			

			CHNode polledNode = nodePriorityQueue.poll();
			
			
			if (!nodePriorityQueue.isEmpty() && nodePriorityQueue.size() < lastNodesLazyUpdates)
            {
				int priority = calculatePriority(this.getNode(polledNode.getId()));

				oldPriorities.put((long)polledNode.getId(), priority);
                if (priority > nodePriorityQueue.peek().getPriority())
                {
                    // current node got more important => insert as new value and contract it later
                	CHNode node = this.getNode(polledNode.getId());
					node.setPriority(priority);
					//TODO Double check this maxLevel setter. Is it necessary?
					node.setLevel(maxLevel);

					nodePriorityQueue.add((CHNodeImpl) node);
                    continue;
                }
            }
			
			
			
			
			
			
			int numberShortcutsCreated = this.addShortcuts(polledNode.getId());
			// System.out.println("nodeID: " + polledNode.getId() + ", Priority:
			// " + polledNode.getPriority());
			// System.out.println("nodeID: " + polledNode.getId() + ",
			// #shortcuts: " + numberShortcutsCreated);

			// this.getNode(polledNode.getId()). setLevel(level);
			this.updateNodeInfo(polledNode, level, polledNode.getPriority());
			polledNode.setLevel(level);
			level++;
			
			if (nodePriorityQueue.size() < nodesToAvoidContract)
                // skipped nodes are already set to maxLevel
                break;

			for (Long edgeID : this.getOutEdges(polledNode.getId())) {

				// Lemma 1
				long nearestNeighborID = this.getEdge(edgeID).getToNode();

				if (this.getNode(nearestNeighborID).getLevel() != maxLevel) {
					continue;
				}

				if (neighborUpdate && rand.nextInt(100) < neighborUpdatePercentage) {
					int oldPrio = oldPriorities.get(nearestNeighborID);
					int newPrio = calculatePriority(this.getNode(nearestNeighborID));
					oldPriorities.put(nearestNeighborID, newPrio);
					if (newPrio != oldPrio) {

						// TODO High chances to be a wrong way to remove a node!
						nodePriorityQueue.remove(this.getNode(nearestNeighborID));
						this.getNode(nearestNeighborID).setPriority(newPrio);
						nodePriorityQueue.add((CHNodeImpl) this.getNode(nearestNeighborID));
					}
				}

			}

			// newShortcuts += addShortcuts(polledNode);
			// prepareGraph.setLevel(polledNode, level);
			// level++;

			// int polledNode = sortedNodes.pollKey();

		}

	}

	/*
	 * Adds a new shortcut to the graph or update other ones already in the
	 * graph.
	 */

	int addShortcuts(long node) {

		possibleShortcuts.clear();
		findShortcut(this.getNode(node));
		int temporaryShortcutCounter = 0;
		NEXT_SC: for (CHEdge shortcutEntry : possibleShortcuts.get(node)) {

			boolean updatedInGraph = false;

			this.getOutEdges(shortcutEntry.getFromNode());

			for (Long edgeID : this.getOutEdges(shortcutEntry.getFromNode())) {

				CHEdge edge = this.getEdge(edgeID);

				if (edge.isShortcut() && edge.getToNode() == shortcutEntry.getToNode()) {

					if (shortcutEntry.getDistance() >= edge.getDistance()) {
						continue NEXT_SC;
					}

					if (edge.getId() == shortcutEntry.getOutgoingSkippedEdge()
							|| edge.getId() == shortcutEntry.getIngoingSkippedEdge()) {
						// TODO Throw illegal
					}

					edge.setDistance(shortcutEntry.getDistance());
					edge.setIngoingSkippedEdge(shortcutEntry.getIngoingSkippedEdge());
					edge.setOutgoingSkippedEdge(shortcutEntry.getOutgoingSkippedEdge());

					edge.setOriginalEdgeCounter(shortcutEntry.getOriginalEdgeCounter());
					updatedInGraph = true;
					break;

				}

			}

			if (!updatedInGraph) {

				CHEdge newShortcut = new CHEdgeImpl(shortcutEntry.getFromNode(), shortcutEntry.getToNode(),
						shortcutEntry.getDistance(), shortcutEntry.getOriginalEdgeCounter(),
						shortcutEntry.getContractedNodeId(), shortcutEntry.getOutgoingSkippedEdge(),
						shortcutEntry.getIngoingSkippedEdge(), true);

				this.addEdge(newShortcut);
				temporaryShortcutCounter++;

			}

		}

		return temporaryShortcutCounter;

	}

	@Override
	public void setLevel(int nodeId, int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLevel(int nodeId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isShortcut(int edgeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shortcut(int a, int b) {
		// TODO Auto-generated method stub
		return false;
	}

	// public void setReverseGraph(CHGraph reverseGraph) {
	// this.reverseGraph = reverseGraph;
	// }

	public int getMaximumEdgeCount() {
		return maximumEdgeCount;
	}

	public void setMaximumEdgeCount(int maximumEdgeCount) {
		this.maximumEdgeCount = maximumEdgeCount;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setReverseGraph(CHGraph reverseGraph) {
		this.reverseGraph = reverseGraph;
	}

	public void setNodesComplement(IntBigArrayBigList nodesComplement) {
		this.nodesComplement = nodesComplement;
	}

	public void setEdgesComplement(IntBigArrayBigList edgesComplement) {
		this.edgesComplement = edgesComplement;
	}

	public void setPossibleShortcuts(Map<Long, List<CHEdge>> possibleShortcuts) {
		this.possibleShortcuts = possibleShortcuts;
	}

	public void setNodePriorityQueue(Queue<CHNodeImpl> nodePriorityQueue) {
		this.nodePriorityQueue = nodePriorityQueue;
	}

	public void setShortestPath(ShortestPathService shortestPath) {
		this.shortestPath = shortestPath;
	}

	public CHGraph getReverseGraph() {
		return this.reverseGraph;
	}

	public CHGraph generateReverseCHGraph(CHGraph originalCHGraph) {

		CHGraph reverseCHGraph = new CHGraphImpl(Configuration.USER_HOME + "/graphast/test/exampleReverseCH");

		for (int i = 0; i < originalCHGraph.getNumberOfNodes(); i++) {

			CHNode node = new CHNodeImpl(originalCHGraph.getNode(i).getExternalId(),
					originalCHGraph.getNode(i).getLatitude(), originalCHGraph.getNode(i).getLongitude());
			// System.out.println(node);
			reverseCHGraph.addNode(node);

		}

		for (int i = 0; i < originalCHGraph.getNumberOfEdges(); i++) {

			CHEdge reverseEdge = new CHEdgeImpl(originalCHGraph.getEdge(i).getToNode(),
					originalCHGraph.getEdge(i).getFromNode(), originalCHGraph.getEdge(i).getDistance(),
					originalCHGraph.getEdge(i).getOriginalEdgeCounter());

			reverseCHGraph.addEdge(reverseEdge);

		}

		return reverseCHGraph;
	}

	public void createHyperPOIS() {

		Set<CHEdge> hyperEdges;

		for (int i = 0; i < this.getNumberOfNodes(); i++) {

			CHNode auxiliarCHNode = this.getNode(i);

			if (auxiliarCHNode.getCategory() == 1) {

				// HyperPoI's must have category==2
				CHNode hyperPoICH = new CHNodeImpl(auxiliarCHNode.getExternalId(), auxiliarCHNode.getLatitude(),
						auxiliarCHNode.getLongitude(), 2);

				this.addNode(hyperPoICH);

				hyperEdges = new HashSet<>();

				CHEdge hyperPoICHEdge = new CHEdgeImpl(auxiliarCHNode.getId(), hyperPoICH.getId(), 0, 1);
				hyperEdges.add(hyperPoICHEdge);
				// this.addEdge(hyperPoICHEdge);

				// Copiando arestas de saída
				for (Long auxiliarCHEdgeID : this.getOutEdges(auxiliarCHNode.getId())) {

					CHEdge auxiliarOutgoingEdge = this.getEdge(auxiliarCHEdgeID);
					hyperPoICHEdge = new CHEdgeImpl(hyperPoICH.getId(), auxiliarOutgoingEdge.getToNode(),
							auxiliarOutgoingEdge.getDistance(), 1);
					// this.addEdge(hyperPoICHEdge);
					hyperEdges.add(hyperPoICHEdge);

				}

				// Copiando arestas de entrada
				for (Long auxiliarCHEdgeID : this.reverseGraph.getOutEdges(auxiliarCHNode.getId())) {

					CHEdge auxiliarIngoingEdge = this.getEdge(auxiliarCHEdgeID);
					hyperPoICHEdge = new CHEdgeImpl(auxiliarIngoingEdge.getFromNode(), hyperPoICH.getId(),
							auxiliarIngoingEdge.getDistance(), 1);
					// this.addEdge(hyperPoICHEdge);
					hyperEdges.add(hyperPoICHEdge);

				}

				for (CHEdge e : hyperEdges) {
					this.addEdge(e);
				}

			}

		}

	}

	public Queue<CHNodeImpl> getNodePriorityQueue() {
		return nodePriorityQueue;
	}

	
	
}
