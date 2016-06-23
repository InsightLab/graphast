package org.graphast.model.contraction;

import java.util.ArrayList;
import java.util.Comparator;
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
import org.graphast.query.route.shortestpath.dijkstraCH.DijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;

public class CHGraphImpl extends GraphImpl implements CHGraph {

	private IntBigArrayBigList nodesComplement;

	private IntBigArrayBigList edgesComplement;

	Map<Long, List<CHEdge>> possibleShortcuts = new HashMap<>();

	
	MixAndMatchComparator orComparator;
	
	
	
	
	private Queue<CHNodeImpl> nodePriorityQueue;

	private Map<Long, Integer> oldPriorities = new HashMap<>();

	private Set<CHNode> hyperPoIsSet = new HashSet<>();

	private int neighborUpdatePercentage = 100;
	private int periodicUpdatesPercentage = 100;
	private int lastNodesLazyUpdatePercentage = 100;
	private double nodesContractedPercentage = 100;
	private int numberShortcutsCreated;
	private long counter;
	private double meanDegree;
	private int maxHopLimit;
	private final Random rand = new Random(123);
	private int regularNodeHighestPriority = Integer.MIN_VALUE;
	private int maxVisitedNodes;

	private CHGraph reverseGraph;

	private DijkstraCH shortestPath;
	private int maximumEdgeCount, maxLevel, minLevelPoI;

	public CHGraphImpl() {
		super();
	}

	public CHGraphImpl(String directory) {
		super(directory);

		nodesComplement = new IntBigArrayBigList();
		edgesComplement = new IntBigArrayBigList();
		
		List<Comparator<CHNodeImpl>> comparators = new ArrayList<Comparator<CHNodeImpl>>();
		//first sort on priority            
		comparators.add(new HighPriorityComparator());
		//then on time
		comparators.add(new IdComparator());
		MixAndMatchComparator orComparator = new MixAndMatchComparator(comparators);
		
		nodePriorityQueue = new PriorityQueue<CHNodeImpl>(orComparator);

	}

	public void addNode(CHNode n) {
		super.addNode(n);

		synchronized (nodesComplement) {

			nodesComplement.add(n.getPriority());
			nodesComplement.add(n.getLevel());

		}

	}

	public void updateNodeInfo(CHNode n, int level, int priority) {

		super.updateNodeInfo(n);

		long position = n.getId() * CHNode.NODE_BLOCKSIZE;

		nodesComplement.set(position, priority);
		nodesComplement.set(position + 1, level);
		
		n.setPriority(priority);
		n.setLevel(level);

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

			System.out.println("[CONTRAINDO] NID: " + nodeId );
			
			int priority = calculatePriority(this.getNode(nodeId), false);

			oldPriorities.put(nodeId, priority);

			this.updateNodeInfo(this.getNode(nodeId), maxLevel, priority);
			nodePriorityQueue.add((CHNodeImpl) this.getNode(nodeId));

		}

		if (nodePriorityQueue.isEmpty()) {
			
			return false;
		
		} else {
		
			return true;
		
		}

	}

	public int calculatePriority(Node n, boolean contract) {

		findShortcut(n, contract);

		int originalEdgeCount = 0;
		
		for (CHEdge e : possibleShortcuts.get(n.getId())) {
			originalEdgeCount += e.getOriginalEdgeCounter();
		}
		
		int degree = 0;

		int numberOfContractedNeighbors = 0;

		for (Long edgeId : this.getInEdges(n.getId())) {
			
			if(this.getNode(this.getEdge(edgeId).getFromNode()).getLevel()==maxLevel ) {
				degree += 1;
			}
			
			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}
		
		}

		for (Long edgeId : this.getOutEdges(n.getId())) {
			
			if(this.getNode(this.getEdge(edgeId).getToNode()).getLevel()==maxLevel ) {
				degree += 1;
			}
			
			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			} 
		
		}

		int edgeDifference = possibleShortcuts.get(n.getId()).size() - degree;

		int hyperPoICoefficient = 0;

		if (n.getCategory() == 2) {
//			hyperPoICoefficient += 20 * this.getNumberOfEdges() + this.getNumberOfEdges() + this.getNumberOfNodes();
			hyperPoICoefficient += 20 * this.getNumberOfEdges() + this.getNumberOfEdges() + this.getNumberOfNodes() + this.getInEdges(n.getId()).size() + this.getOutEdges(n.getId()).size();
			
		}

		int priority = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors + hyperPoICoefficient;

		return priority;

	}
	
	public int calculatePriority(Node n, boolean contract, long contractedNodeId, long outsideEdgeId) {

		findShortcut(n, contract);

		int originalEdgeCount = 0;
		
		for (CHEdge e : possibleShortcuts.get(n.getId())) {
			originalEdgeCount += e.getOriginalEdgeCounter();
		}
		
		int degree = 0;
		int numberOfContractedNeighbors = 0;

		for (Long edgeId : this.getInEdges(n.getId())) {
//			System.out.println("[IN]");
//			System.out.println(this.getEdge(edgeId));
			
			if(this.getNode(this.getEdge(edgeId).getFromNode()).getId() == contractedNodeId) {
				if(this.getEdge(outsideEdgeId).isShortcut() && edgeId == outsideEdgeId) {
					degree += 1;
					System.out.println("\t\tedge: " + this.getEdge(edgeId).getId() + " " + this.getEdge(edgeId).getFromNode() + "-" + this.getEdge(edgeId).getToNode());
				} else {
					continue;
				}
				
			} else {
				if(this.getNode(this.getEdge(edgeId).getFromNode()).getLevel()==maxLevel) {
//				if(this.getNode(this.getEdge(edgeId).getFromNode()).getLevel()==maxLevel || this.getEdge(edgeId).isShortcut() ) {
					degree += 1;
					System.out.println("\t\tedge: " + this.getEdge(edgeId).getId() + " " + this.getEdge(edgeId).getFromNode() + "-" + this.getEdge(edgeId).getToNode());
				}
			}
			
			if (this.getEdge(edgeId).isShortcut()) {
//				System.out.println("numberOfContractedNeighbors += 1");
//				System.out.println("\t" + this.getEdge(edgeId));
				numberOfContractedNeighbors += 1;
			}
		}

		for (Long edgeId : this.getOutEdges(n.getId())) {
//			System.out.println("[OUT]");
			//TODO NOT SURE ABOUT THIS VERIFICATION
			if(this.getNode(this.getEdge(edgeId).getToNode()).getId() == contractedNodeId) {
				if(this.getEdge(outsideEdgeId).isShortcut() && edgeId == outsideEdgeId) {
					degree += 1;
					System.out.println("\t\tedge: " + this.getEdge(edgeId).getId() + " " + this.getEdge(edgeId).getFromNode() + "-" + this.getEdge(edgeId).getToNode());
				} else {
					continue;
				}
			} else {
				if(this.getNode(this.getEdge(edgeId).getToNode()).getLevel()==maxLevel ) {
//				if(this.getNode(this.getEdge(edgeId).getToNode()).getLevel()==maxLevel || this.getEdge(edgeId).isShortcut() ) {
					degree += 1;
					System.out.println("\t\tedge: " + this.getEdge(edgeId).getId() + " " + this.getEdge(edgeId).getFromNode() + "-" + this.getEdge(edgeId).getToNode());
				}
			}
			
			if (this.getEdge(edgeId).isShortcut()) {
//				System.out.println("numberOfContractedNeighbors += 1");
//				System.out.println("\t" + this.getEdge(edgeId));
				numberOfContractedNeighbors += 1;
			} 
		}

		int edgeDifference = possibleShortcuts.get(n.getId()).size() - degree;

		int hyperPoICoefficient = 0;

		if (n.getCategory() == 2) {
			hyperPoICoefficient += 20 * this.getNumberOfEdges() + this.getNumberOfEdges() + this.getNumberOfNodes();
		}

		int priority = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors + hyperPoICoefficient;

		return priority;

	}

	// Consider the following "graph": u --> u --> w
	public void findShortcut(Node n, boolean contract) {
		//TODO double check if this verification is necessary
		if(contract == false) {
			meanDegree = 0;
			maxHopLimit = 1;
		} else {
			maxHopLimit = Integer.MAX_VALUE;
		}
		
		
		boolean shortcutAdded = false;
		
		List<CHEdge> possibleLocalShortcut = new ArrayList<>();
		
		long tmpDegreeCounter = 0;

		// Arestas chegando no nó N (equivalente aos outgoings do grafo
		// reverso).
		// Nó TO das arestas outgoings do grafo reverso serão os FROM do grafo
		// original.
		for (Long ingoingEdgeId : this.getInEdges(n.getId())) {

			Long fromNodeId = this.getEdge(ingoingEdgeId).getFromNode();
			System.out.println("FROM: " + fromNodeId);
			if (this.getNode(fromNodeId).getLevel() != maxLevel) {
				System.out.println("\tIgnored because the node " + fromNodeId + " is already contracted.");
				continue;
			}

//			System.out.println("Level do nó " + fromNodeId + ": " + this.getNode(fromNodeId).getLevel());
			
			tmpDegreeCounter++;
			// Arestas outgoing do nó N. O nó TO será o nó final.
			for (Long outgoingEdgeId : this.getOutEdges(n.getId())) {

				Long toNodeId = this.getEdge(outgoingEdgeId).getToNode();
				System.out.println("\tTO: " + toNodeId);
				// Se o toNodeId == fromNodeId, significa que estamos saindo e
				// chegando na mesma aresta, com um nó
				// intermediário
				// Se getNode(toNodeId).getLevel() != maxLevel quer dizer que já analisamos esse nó
				if (this.getNode(toNodeId).getLevel() != maxLevel || toNodeId == fromNodeId) {
					System.out.println("\t\tIgnored because the node " + toNodeId + " is already contracted.");
					continue;
				}
				
//				System.out.println("\tLevel do nó " + toNodeId + ": " + this.getNode(fromNodeId).getLevel());

				double ingoingDistance = this.getEdge(ingoingEdgeId).getDistance();

				// Essa comparação serve para sempre pegarmos a aresta com o
				// menor valor quando temos mais de uma
				// ligando o mesmo nó de origem ao mesmo nó de destino
				for (Long comparisonEdgeId : this.getInEdges(n.getId())) {
					if (this.getEdge(comparisonEdgeId).getFromNode() == fromNodeId
							&& this.getEdge(comparisonEdgeId).getDistance() <= ingoingDistance) {
						ingoingDistance = this.getEdge(comparisonEdgeId).getDistance();
					}
				}

				double outgoingDistance = this.getEdge(outgoingEdgeId).getDistance();

				// Essa comparação serve para sempre pegarmos a aresta com o
				// menor valor quando temos mais de uma
				// ligando o mesmo nó de origem ao mesmo nó de destino
				for (Long comparisonEdgeId : this.getOutEdges(n.getId())) {
					if (this.getEdge(comparisonEdgeId).getToNode() == toNodeId
							&& this.getEdge(comparisonEdgeId).getDistance() <= outgoingDistance) {
						outgoingDistance = this.getEdge(comparisonEdgeId).getDistance();
					}
				}

				double shortestPathBeforeContraction = ingoingDistance + outgoingDistance;

				shortestPath = new DijkstraCH(this);
				
				shortestPath.setMaxHopLimit((int) maxHopLimit);

				Path path;
				System.out.println("\t\tSearching for a path between " + fromNodeId + " and " + toNodeId + ", ignoring node " + n.getId());
				try {
					//TODO esse shortest path esta considerando nos apenas com prioridades maiores?
					path = shortestPath.shortestPath(this.getNode(fromNodeId), this.getNode(toNodeId), null, n);
				} catch (Exception e) {
					path = null;
				}
				
				if (path != null && path.getTotalDistance() <= shortestPathBeforeContraction)
					// Witness path found! Continue the search for the next
					// neighbor
					continue;

				// Shortcut must be created
				int ingoingEdgeCounter = this.getEdge(ingoingEdgeId).getOriginalEdgeCounter();
				int outgoingEdgeCounter = this.getEdge(outgoingEdgeId).getOriginalEdgeCounter();

				String newLabel = "Shortcut " + String.valueOf(fromNodeId) + "-" + String.valueOf(toNodeId);
				System.out.println("\t\t[SHORTCUT] FROM: " + fromNodeId + ", TO: " + toNodeId);
				CHEdge shortcut = new CHEdgeImpl(fromNodeId, toNodeId, (int) shortestPathBeforeContraction,
						ingoingEdgeCounter + outgoingEdgeCounter, n.getId(), this.getEdge(ingoingEdgeId).getId(),
						this.getEdge(outgoingEdgeId).getId(), newLabel, true);

				// System.out.println("shortcutFrom: " + fromNodeId
				// + ", shortcutTo: " + toNodeId
				// + ", Distance: " + (int) shortestPathBeforeContraction + ",
				// originalEdgeCount: "
				// + (ingoingEdgeCounter + outgoingEdgeCounter));

				possibleLocalShortcut.add(shortcut);
				shortcutAdded = true;

			}

		}

		possibleShortcuts.put(n.getId(), possibleLocalShortcut);

		if((contract == true) && (shortcutAdded == true)) {
			meanDegree = (meanDegree * 2 + tmpDegreeCounter) / 3;
			maxHopLimit = (int) meanDegree;
		}

	}

	public void contractNodes() {

		int level = 1;
		// TODO Rename this variable counter!!
		counter = 0;
		int updateCounter = 0;
		meanDegree = this.getNumberOfEdges() / this.getNumberOfNodes();

		// preparation takes longer but queries are slightly faster with
		// preparation
		// => enable it but call not so often
		boolean periodicUpdate = true;

		long periodicUpdatesCount = Math.round(Math.max(10, nodePriorityQueue.size() / 100d * periodicUpdatesPercentage));

		if (periodicUpdatesPercentage == 0) {
			periodicUpdate = false;
		}

		// disable lazy updates for last x percentage of nodes as preparation is
		// then a lot slower
		// and query time does not really benefit
		long lastNodesLazyUpdates = Math.round(nodePriorityQueue.size() / 100d * lastNodesLazyUpdatePercentage);

		// according to paper "Polynomial-time Construction of Contraction
		// Hierarchies for Multi-criteria Objectives" by Funke and Storandt
		// we don't need to wait for all nodes to be contracted
		long nodesToAvoidContract = Math.round((100 - nodesContractedPercentage) / 100 * nodePriorityQueue.size());

		// Recompute priority of uncontracted neighbors.
		// Without neighbor updates preparation is faster but we need them
		// to slightly improve query time. Also if not applied too often it
		// decreases the shortcut number.
		boolean neighborUpdate = true;

		if (neighborUpdatePercentage == 0) {
			neighborUpdate = false;
		}

		while (!nodePriorityQueue.isEmpty()) {

			if (periodicUpdate && counter > 0 && counter % periodicUpdatesCount == 0) {

				nodePriorityQueue.clear();

				for (int nodeId = 0; nodeId < this.getNumberOfNodes(); nodeId++) {

					if (this.getNode(nodeId).getLevel() != maxLevel)
						continue;
//					System.out.println("Linha 436");
					int priority = calculatePriority(this.getNode(nodeId), true);

					oldPriorities.put((long) nodeId, priority);
					// TODO Change "maxLevel"to this.getNode(nodeId).getLevel()
					// or the opposite?
					this.updateNodeInfo(this.getNode(nodeId), this.getNode(nodeId).getLevel(), priority);
					nodePriorityQueue.add((CHNodeImpl) this.getNode(nodeId));

					// oldPriorities.put((long)nodeId, priority);

					// CHNode node = this.getNode(nodeId);
					// node.setPriority(priority);
					// //TODO Double check this maxLevel setter. Is it
					// necessary?
					// node.setLevel(maxLevel);

					// nodePriorityQueue.add((CHNodeImpl) node);

				}
				updateCounter++;
				if (nodePriorityQueue.isEmpty())
					throw new IllegalStateException(
							"Cannot prepare as no unprepared nodes where found. Called preparation twice?");

			}

			counter++;

//			System.out.println("nodeId: " + nodePriorityQueue.peek().getId() + ", priority: "
//					+ nodePriorityQueue.peek().getPriority());

			CHNode polledNode = nodePriorityQueue.poll();
			System.out.println("[CONTRAINDO] NID: " + polledNode.getId() + " Prioridade: " + polledNode.getPriority());

			if (!nodePriorityQueue.isEmpty() && nodePriorityQueue.size() < lastNodesLazyUpdates) {
				int priority = calculatePriority(this.getNode(polledNode.getId()), true);

				oldPriorities.put((long) polledNode.getId(), priority);
//				System.out.println("\t[ATUALIZANDO VIZINHO - LAZY] NID: " + polledNode.getId() + " Prioridade: " + priority);
				if (priority > nodePriorityQueue.peek().getPriority()) {
					// current node got more important => insert as new value
					// and contract it later
					CHNode node = this.getNode(polledNode.getId());
					node.setPriority(priority);
					// TODO Double check this maxLevel setter. Is it necessary?
					 node.setLevel(maxLevel);
					 this.updateNodeInfo(node, maxLevel, priority);

					nodePriorityQueue.add((CHNodeImpl) node);
					// System.out.println("Saiu");
					continue;
				}
			}

			// contract!
			numberShortcutsCreated += this.addShortcuts(polledNode.getId());
			System.out.println("\t\t\t\tNUMEBER OF SHORTCUTS CREATED: " + numberShortcutsCreated);
//			System.out.println("#Shortcuts created: " + numberShortcutsCreated);
			this.updateNodeInfo(polledNode, level, polledNode.getPriority());
			//TODO Double check if this setLevel is necessary, since we already have a updateNodeInfo before
			polledNode.setLevel(level);
			// System.out.println(level);
			level++;

			if (nodePriorityQueue.size() < nodesToAvoidContract)
				// skipped nodes are already set to maxLevel
				break;

			for (Long edgeID : this.getInEdges(polledNode.getId())) {
				System.out.println("\t\t\t[ARESTA] - " + this.getEdge(edgeID).getId() + " " + this.getEdge(edgeID).getFromNode() + "-" + this.getEdge(edgeID).getToNode());
				// Lemma 1
				long nearestNeighborID = this.getEdge(edgeID).getFromNode();

				if (this.getNode(nearestNeighborID).getLevel() != maxLevel) {
					continue;
				}

				if (neighborUpdate && rand.nextInt(100) < neighborUpdatePercentage) {
					int oldPrio = oldPriorities.get(nearestNeighborID);
					int newPrio = calculatePriority(this.getNode(nearestNeighborID), true, polledNode.getId(), edgeID);
					oldPriorities.replace(nearestNeighborID, newPrio);
					System.out.println("\t[ATUALIZANDO VIZINHO - LAZY] NID: " + nearestNeighborID + " Prioridade: " + newPrio);

					if (newPrio != oldPrio) {

						// TODO High chances to be a wrong way to remove a node!
						nodePriorityQueue.remove(this.getNode(nearestNeighborID));
						this.getNode(nearestNeighborID).setPriority(newPrio);
						this.updateNodeInfo(this.getNode(nearestNeighborID), this.getNode(nearestNeighborID).getLevel(), newPrio);
						nodePriorityQueue.add((CHNodeImpl) this.getNode(nearestNeighborID));

					}
				}
			}
			
			for (Long edgeID : this.getOutEdges(polledNode.getId())) {
				// Lemma 1
				long nearestNeighborID = this.getEdge(edgeID).getToNode();
				System.out.println("\t\t\t[ARESTA] - " + this.getEdge(edgeID).getId() + " " + this.getEdge(edgeID).getFromNode() + "-" + this.getEdge(edgeID).getToNode());

				if (this.getNode(nearestNeighborID).getLevel() != maxLevel) {
					continue;
				}

				if (neighborUpdate && rand.nextInt(100) < neighborUpdatePercentage) {
					
					int oldPrio = oldPriorities.get(nearestNeighborID);
					int newPrio = calculatePriority(this.getNode(nearestNeighborID), true, polledNode.getId(), edgeID);
					oldPriorities.replace(nearestNeighborID, newPrio);
					System.out.println("\t[ATUALIZANDO VIZINHO - LAZY2] NID: " + nearestNeighborID + " Prioridade: " + newPrio);

					if (newPrio != oldPrio) {

						// TODO High chances to be a wrong way to remove a node!
						nodePriorityQueue.remove(this.getNode(nearestNeighborID));
						this.getNode(nearestNeighborID).setPriority(newPrio);
						this.updateNodeInfo(this.getNode(nearestNeighborID), this.getNode(nearestNeighborID).getLevel(), newPrio);
						nodePriorityQueue.add((CHNodeImpl) this.getNode(nearestNeighborID));

					}
				}
			}
			
		}
	}

	/*
	 * Adds a new shortcut to the graph or update other ones already in the
	 * graph.
	 */

	int addShortcuts(long node) {

		possibleShortcuts.clear();
		findShortcut(this.getNode(node), true);
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
						throw new IllegalStateException();
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

				String newLabel = "Shortcut " + String.valueOf(shortcutEntry.getFromNode()) + "-"
						+ String.valueOf(shortcutEntry.getToNode());

				// CHEdge shortcut = new CHEdgeImpl(fromNodeId, toNodeId, (int)
				// shortestPathBeforeContraction,
				// ingoingEdgeCounter + outgoingEdgeCounter, n.getId(),
				// this.getEdge(ingoingEdgeId).getId(),
				// this.getEdge(outgoingEdgeId).getId(), newLabel, true);

				CHEdge newShortcut = new CHEdgeImpl(shortcutEntry.getFromNode(), shortcutEntry.getToNode(),
						shortcutEntry.getDistance(), shortcutEntry.getOriginalEdgeCounter(),
						shortcutEntry.getContractedNodeId(), shortcutEntry.getOutgoingSkippedEdge(),
						shortcutEntry.getIngoingSkippedEdge(), newLabel, true);

				this.addEdge(newShortcut);
//				System.out.println("\t" + newShortcut);
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

	public void setShortestPath(DijkstraCH shortestPath) {
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
		
		ShortestPathService shortestPathHyper = new DijkstraConstantWeight(this);

		Path p;
		
		for (int i = 0; i < this.getNumberOfNodes(); i++) {

			CHNode auxiliarCHNode = this.getNode(i);

			if (auxiliarCHNode.getCategory() == 1) {

				// HyperPoI's must have category==2
				CHNode hyperPoICH = new CHNodeImpl(auxiliarCHNode.getExternalId(), auxiliarCHNode.getLatitude(),
						auxiliarCHNode.getLongitude(), 2);

				this.addNode(hyperPoICH);

//				System.out.println(
//						"HyperPoI " + hyperPoICH.getId() + " created from PoI " + auxiliarCHNode.getExternalId());

				hyperEdges = new HashSet<>();

				String newLabel = "HyperEdge " + auxiliarCHNode.getId() + "-" + hyperPoICH.getId();
				
				CHEdge hyperPoICHEdge = new CHEdgeImpl(auxiliarCHNode.getId(), hyperPoICH.getId(), 0, 1, newLabel);
				
				hyperEdges.add(hyperPoICHEdge);

//				newLabel = "HyperEdge " + hyperPoICH.getId() + "-" + auxiliarCHNode.getId();
//				
//				hyperPoICHEdge = new CHEdgeImpl(hyperPoICH.getId(), auxiliarCHNode.getId(), 0, 1, newLabel);
//
//				hyperEdges.add(hyperPoICHEdge);
				
				// Copiando arestas de saída
				for (Long auxiliarCHEdgeID : this.getOutEdges(auxiliarCHNode.getId())) {

					CHEdge auxiliarOutgoingEdge = this.getEdge(auxiliarCHEdgeID);

					newLabel = "HyperEdge " + hyperPoICH.getId() + "-" + auxiliarOutgoingEdge.getToNode();

					int newDistance = (int) shortestPathHyper.shortestPath(auxiliarCHNode.getId(), auxiliarOutgoingEdge.getToNode()).getTotalDistance();
					
					hyperPoICHEdge = new CHEdgeImpl(hyperPoICH.getId(), auxiliarOutgoingEdge.getToNode(),
							newDistance, 1, newLabel);

					hyperEdges.add(hyperPoICHEdge);

				}

				// Copiando arestas de entrada
				for (Long auxiliarCHEdgeID : this.getInEdges(auxiliarCHNode.getId())) {

					CHEdge auxiliarIngoingEdge = this.getEdge(auxiliarCHEdgeID);

					newLabel = "HyperEdge " + auxiliarIngoingEdge.getFromNode() + "-" + hyperPoICH.getId();

					int newDistance = (int) shortestPathHyper.shortestPath(auxiliarIngoingEdge.getFromNode(), auxiliarCHNode.getId()).getTotalDistance();
					
					hyperPoICHEdge = new CHEdgeImpl(auxiliarIngoingEdge.getFromNode(), hyperPoICH.getId(),
							newDistance, 1, newLabel);
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

	@Override
	public String toString() {

		String returnString = "";

		returnString = returnString
				.concat("-- Graph n:" + this.getNumberOfNodes() + " e:" + this.getNumberOfEdges() + " ---\n");

		returnString = returnString.concat("\n\tList of Nodes\n\n");

		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			CHNode n = this.getNode(i);

			returnString = returnString
					.concat("NID: " + n.getId() + ", Level: " + n.getLevel() + ", Priority: " + n.getPriority() + "\n");

		}

		returnString = returnString.concat("\n\tList of Edges\n\n");

		for (int i = 0; i < this.getNumberOfEdges(); i++) {
			CHEdge e = this.getEdge(i);

			returnString = returnString.concat("EID: " + e.getId() + ", FROM: " + e.getFromNode() + ", TO: "
					+ e.getToNode() + ", Distance: " + e.getDistance() + ", isShortcut: " + e.isShortcut() + "\n");

		}

		return returnString;
	}

}
