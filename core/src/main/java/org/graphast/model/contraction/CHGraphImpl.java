package org.graphast.model.contraction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import org.graphast.config.Configuration;
import org.graphast.model.Edge;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
import org.graphast.query.route.shortestpath.dijkstrach.DijkstraCH;
import org.graphast.query.route.shortestpath.model.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;

public class CHGraphImpl extends GraphImpl implements CHGraph {

	private IntBigArrayBigList nodesComplement;

	private IntBigArrayBigList edgesComplement;

	Map<Long, List<CHEdge>> possibleShortcuts = new HashMap<>();

	MixAndMatchComparator orComparator;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Queue<CHNodeImpl> sortedNodesQueue;

	private Map<Long, Integer> oldPriorities = new HashMap<>();

	private int neighborUpdatePercentage = 0;
	private int periodicUpdatesPercentage = 0;
	private int lastNodesLazyUpdatePercentage = 0;
	private double nodesContractedPercentage = 100;
	private int numberShortcutsCreated;
	private final Random rand = new Random(123);
	private int regularNodeHighestPriority = Integer.MIN_VALUE;

	private CHGraph reverseGraph;

	private DijkstraCH shortestPath;
	private int maximumEdgeCount;
	private double meanDegree;

	public CHGraphImpl() {
		super();
	}

	public CHGraphImpl(String directory) {
		super(directory);

		nodesComplement = new IntBigArrayBigList();
		edgesComplement = new IntBigArrayBigList();

		List<Comparator<CHNodeImpl>> comparators = new ArrayList<Comparator<CHNodeImpl>>();
		// first sort on priority
		comparators.add(new HighPriorityComparator());
		// then on time
		comparators.add(new IdComparator());
		MixAndMatchComparator orComparator = new MixAndMatchComparator(comparators);

		sortedNodesQueue = new PriorityQueue<CHNodeImpl>(orComparator);

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

	//Method is working properly
	@Override
	public boolean prepareNodes() {

		// Calculating the priority for all nodes
		for (int nodeId = 0; nodeId < this.getNumberOfNodes(); nodeId++) {

			// logger.info("[PRIORITIZING] nodeID: {}", nodeId);

			int priority = calculatePriority(this.getNode(nodeId), false);

			oldPriorities.put((long) nodeId, priority);

			this.updateNodeInfo(this.getNode(nodeId), 0, priority);
			sortedNodesQueue.add((CHNodeImpl) this.getNode(nodeId));

		}

		if (sortedNodesQueue.isEmpty())
			return false;

		return true;

	}

	@Override
	public int calculatePriority(CHNode n, boolean contract) {

		// set of shortcuts that would be added if node n would be contracted
		// next
		findShortcut(n, contract);

		int originalEdgeCount = 0;

		for (CHEdge e : possibleShortcuts.get(n.getId())) {
			originalEdgeCount += e.getOriginalEdgeCounter();
		}

		int degree = 0;
		int numberOfContractedNeighbors = 0;

		for (Long edgeId : this.getInEdges(n.getId())) {

			degree += 1;

			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}

		}

		for (Long edgeId : this.getOutEdges(n.getId())) {

			degree += 1;

			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}

		}

		int edgeDifference = possibleShortcuts.get(n.getId()).size() - degree;
		
//		int hyperPoICoefficient = 0;

//		if (n.getCategory() != -1) {
//			hyperPoICoefficient += 20 * this.getNumberOfEdges() + this.getNumberOfEdges() + this.getNumberOfNodes() + this.getInEdges(n.getId()).size() + this.getOutEdges(n.getId()).size();
//		}

		int prioridade = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors;
//		int prioridade = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors + hyperPoICoefficient;
		logger.debug("Priority of node {}: {}", n.getId(), prioridade);

		return 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors;
//		return 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors + hyperPoICoefficient;

	}

	public int calculatePriority(CHNode n, boolean contract, long contractedNodeId, long outsideEdgeId) {

		findShortcut(n, contract);

		int originalEdgeCount = 0;

		for (CHEdge e : possibleShortcuts.get(n.getId())) {
			originalEdgeCount += e.getOriginalEdgeCounter();
		}

		int degree = 0;
		int numberOfContractedNeighbors = 0;

		for (Long edgeId : this.getInEdges(n.getId())) {

			if (this.getNode(this.getEdge(edgeId).getFromNode()).getId() == contractedNodeId) {
				if (this.getEdge(outsideEdgeId).isShortcut() && edgeId == outsideEdgeId) {
					degree += 1;
				} else {
					continue;
				}

			} else {
				if (this.getNode(this.getEdge(edgeId).getFromNode()).getLevel() == 0) {
					degree += 1;
				}
			}

			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}
		}

		for (Long edgeId : this.getOutEdges(n.getId())) {
			// TODO NOT SURE ABOUT THIS VERIFICATION
			if (this.getNode(this.getEdge(edgeId).getToNode()).getId() == contractedNodeId) {
				if (this.getEdge(outsideEdgeId).isShortcut() && edgeId == outsideEdgeId) {
					degree += 1;
				} else {
					continue;
				}
			} else {
				if (this.getNode(this.getEdge(edgeId).getToNode()).getLevel() == 0) {
					// if(this.getNode(this.getEdge(edgeId).getToNode()).getLevel()==maxLevel
					// || this.getEdge(edgeId).isShortcut() ) {
					degree += 1;
				}
			}

			if (this.getEdge(edgeId).isShortcut()) {
				numberOfContractedNeighbors += 1;
			}
		}

		int edgeDifference = possibleShortcuts.get(n.getId()).size() - degree;

		int priority = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors;

		return priority;

	}

	// Consider the following "graph": u --> u --> w
	public void findShortcut(CHNode n, boolean contract) {
		logger.debug("PROCURANDO ATALHOS PARA O NÓ {}. LAT: {} LON: {}", n.getExternalId(), n.getLatitude(),
				n.getLongitude());

		int temporaryDegreeCounter = 0;
		List<CHEdge> possibleLocalShortcut = new ArrayList<>();

		// Arestas chegando no nó N (equivalente aos outgoings do grafo
		// reverso).
		// Nó TO das arestas outgoings do grafo reverso serão os FROM do grafo
		// original.
		for (Long ingoingEdgeId : this.getInEdges(n.getId())) {
			logger.debug("fromNodeExternalID: {}. LAT: {} LON: {}",
					this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId(),
					this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getLatitude(),
					this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getLongitude());

			Long fromNodeId = this.getEdge(ingoingEdgeId).getFromNode();
			double ingoingDistance = this.getIngoingLowestEdgeValue(n, fromNodeId);

			// Accept only uncontracted nodes.
			if (this.getNode(fromNodeId).getLevel() != 0) {
				logger.debug("\tIgnored because the node {} is already contracted.",
						this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId());
				continue;
			}

			// TODO Try to increment the temporaryDegreeCounter for
			// the outgoingEdges too.
			temporaryDegreeCounter++;

			// Arestas outgoing do nó N. O nó TO será o nó final.
			for (Long outgoingEdgeId : this.getOutEdges(n.getId())) {
				logger.debug("\ttoNodeExternalID: {}. LAT: {} LON: {}",
						this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId(),
						this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getLatitude(),
						this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getLongitude());

				Long toNodeId = this.getEdge(outgoingEdgeId).getToNode();
				double outgoingDistance = this.getOutgoingLowestEdgeValue(n, toNodeId);

				// Se o toNodeId == fromNodeId, significa que estamos
				// saindo e chegando na mesma aresta, com um nó intermediário
				// Se getNode(toNodeId).getLevel() != maxLevel quer dizer
				// que já analisamos esse nó!
				if (this.getNode(toNodeId).getLevel() != 0 || toNodeId.equals(fromNodeId)) {

					if (this.getNode(toNodeId).getLevel() != 0) {

						logger.debug("\t\tIgnored because the node {} is already contracted.",
								this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId());
						continue;
					} else {
						logger.debug("\t\tIgnored because the source node {} is equal to the destination {}.",
								this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId(),
								this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId());
						continue;
					}

				}

				double shortestPathBeforeContraction = ingoingDistance + outgoingDistance;

				shortestPath = new DijkstraCH(this);
				shortestPath.setLimitVisitedNodes((int) meanDegree * 100);

				Path path;
				logger.debug("\t\tSearching for a path between {} and {}, ignoring node {}.",
						this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId(),
						this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId(), n.getExternalId());

				try {
					path = shortestPath.shortestPath(this.getNode(fromNodeId), this.getNode(toNodeId), n);
				} catch (Exception e) {
					// logger.error("In findShortcut method: ", e);
					path = null;
				}

				// Shortcut must be created!
				if ((path == null) || path.getTotalDistance() > shortestPathBeforeContraction) {

					// Shortcut must be created
					int ingoingEdgeCounter = this.getEdge(ingoingEdgeId).getOriginalEdgeCounter();
					int outgoingEdgeCounter = this.getEdge(outgoingEdgeId).getOriginalEdgeCounter();

					String newLabel = "Shortcut " + fromNodeId + "-" + toNodeId;
					logger.debug("\t\t\t[SHORTCUT] FROM NODE {} TO NODE {}. Distance: {}",
							this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId(),
							this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId(), (int) shortestPathBeforeContraction);
					CHEdge shortcut = new CHEdgeImpl(fromNodeId, toNodeId, (int) shortestPathBeforeContraction,
							ingoingEdgeCounter + outgoingEdgeCounter, n.getId(), this.getEdge(ingoingEdgeId).getId(),
							this.getEdge(outgoingEdgeId).getId(), newLabel, true);

					possibleLocalShortcut.add(shortcut);

				} else {
					logger.debug("\tShortcut between source node {} and destination node {} not created.",
							this.getNode(this.getEdge(ingoingEdgeId).getFromNode()).getExternalId(),
							this.getNode(this.getEdge(outgoingEdgeId).getToNode()).getExternalId());
				}

			}

		}

		possibleShortcuts.put(n.getId(), possibleLocalShortcut);

		if (contract) {
			meanDegree = (meanDegree * 2 + temporaryDegreeCounter) / 3;
		}

	}

	public void contractNodes() {

		meanDegree = this.getNumberOfEdges() / this.getNumberOfNodes();
		int level = 1;
		// TODO Rename this variable counter!!
		int counter = 0;

		// Preparation takes longer but queries are slightly faster with
		// preparation
		// => enable it but call not so often
		boolean periodicUpdate = true;

		long periodicUpdatesCount = Math.round(Math.max(10, sortedNodesQueue.size() / 100d * periodicUpdatesPercentage));

		if (periodicUpdatesPercentage == 0) {
			periodicUpdate = false;
		}

		// Disable lazy updates for last x percentage of nodes as preparation is
		// then a lot slower and query time does not really benefit
		long lastNodesLazyUpdates = lastNodesLazyUpdatePercentage == 0
                ? 0l
                : Math.round(sortedNodesQueue.size() / 100d * lastNodesLazyUpdatePercentage);
		
		// According to paper "Polynomial-time Construction of Contraction
		// Hierarchies for Multi-criteria Objectives" by Funke and Storandt,
		// we don't need to wait for all nodes to be contracted
		long nodesToAvoidContract = Math.round((100 - nodesContractedPercentage) / 100 * sortedNodesQueue.size());

		// Recompute priority of uncontracted neighbors.
		// Without neighbor updates preparation is faster but we need them
		// to slightly improve query time. Also if not applied too often it
		// decreases the shortcut number.
		boolean neighborUpdate = true;

		if (neighborUpdatePercentage == 0)
			neighborUpdate = false;

		while (!sortedNodesQueue.isEmpty()) {

			// If periodicUpdate = true, ALL nodes will have their priority
			// updated
			if (periodicUpdate && counter > 0 && counter % periodicUpdatesCount == 0) {

				sortedNodesQueue.clear();

				for (int nodeId = 0; nodeId < this.getNumberOfNodes(); nodeId++) {

					if (this.getNode(nodeId).getLevel() != 0)
						continue;
					
					oldPriorities.put((long) nodeId, calculatePriority(this.getNode(nodeId), true));
					int priority = oldPriorities.get((long) nodeId);

					//TODO Double check this
					this.updateNodeInfo(this.getNode(nodeId), this.getNode(nodeId).getLevel(), priority);
					sortedNodesQueue.add((CHNodeImpl) this.getNode(nodeId));

				}
				if (sortedNodesQueue.isEmpty())
					throw new IllegalStateException(
							"Cannot prepare as no unprepared nodes where found. Called preparation twice?");

			}

			counter++;
			CHNode polledNode = sortedNodesQueue.poll();
			logger.debug("Node being contracted: {}. Priority: {}", polledNode.getId(), polledNode.getPriority());

			if (sortedNodesQueue.size() < lastNodesLazyUpdates) {
				
				oldPriorities.put(polledNode.getId(), calculatePriority(this.getNode(polledNode.getId()), true));
				int priority = oldPriorities.get(polledNode.getId());

				if (!sortedNodesQueue.isEmpty() && priority > sortedNodesQueue.peek().getPriority()) {
					// current node got more important => insert as new value
					// and contract it later
					CHNode node = this.getNode(polledNode.getId());
					node.setPriority(priority);
					// TODO Double check this maxLevel setter. Is it necessary?
//					node.setLevel(maxLevel);
//					this.updateNodeInfo(node, maxLevel, priority);

					sortedNodesQueue.add((CHNodeImpl) node);
					continue;
				}
			}

			// Contracting a node

//			this.addShortcuts(polledNode.getId());
			// logger.debug("\t\t\tNumber of shortcuts created: {}",
			// numberShortcutsCreated);
			System.out.println("-Para o nó " + polledNode.getId() + " foram criados " + this.addShortcuts(polledNode.getId()) + " atalhos. Seu nível é " + level);

			this.updateNodeInfo(polledNode, level, polledNode.getPriority());
			// TODO Double check if this setLevel is necessary, since we already
			// have a updateNodeInfo before
//			polledNode.setLevel(level);
			

			level++;

			if (sortedNodesQueue.size() < nodesToAvoidContract) {
			
				while(!sortedNodesQueue.isEmpty()) {
					polledNode = sortedNodesQueue.poll();
					polledNode.setLevel(level);
				}
				// skipped nodes are already set to maxLevel
				break;
				
			}

			for (Long edgeID : this.getInEdges(polledNode.getId())) {
				// logger.debug("\t\t\tEdgeID: {}. FromNodeID: {}, ToNodeID:
				// {}", this.getEdge(edgeID).getId(),
				// this.getEdge(edgeID).getFromNode(),
				// this.getEdge(edgeID).getToNode());
				// Lemma 1
				long nearestNeighborID = this.getEdge(edgeID).getFromNode();

				if (this.getNode(nearestNeighborID).getLevel() != 0) {
					continue;
				}

				if (neighborUpdate && rand.nextInt(100) < neighborUpdatePercentage) {
					int oldPrio = oldPriorities.get(nearestNeighborID);
					//TODO Double check this priority calculation
					int newPrio = calculatePriority(this.getNode(nearestNeighborID), true, polledNode.getId(), edgeID);
					oldPriorities.replace(nearestNeighborID, newPrio);
					// logger.debug("\t[LAZY UPDATE] NodeID: {}, Priority: {}",
					// nearestNeighborID, newPrio);

					if (newPrio != oldPrio) {

						// TODO High chances to be a wrong way to remove a node!
						sortedNodesQueue.remove(this.getNode(nearestNeighborID));
						this.getNode(nearestNeighborID).setPriority(newPrio);
						this.updateNodeInfo(this.getNode(nearestNeighborID), this.getNode(nearestNeighborID).getLevel(),
								newPrio);
						sortedNodesQueue.add((CHNodeImpl) this.getNode(nearestNeighborID));

					}
				}
			}

			for (Long edgeID : this.getOutEdges(polledNode.getId())) {
				// Lemma 1
				long nearestNeighborID = this.getEdge(edgeID).getToNode();
				// logger.debug("\t\t\tEdgeID: {}. FromNodeID: {}, ToNodeID:
				// {}", this.getEdge(edgeID).getId(),
				// this.getEdge(edgeID).getFromNode(),
				// this.getEdge(edgeID).getToNode());

				if (this.getNode(nearestNeighborID).getLevel() != 0) {
					continue;
				}

				if (neighborUpdate && rand.nextInt(100) < neighborUpdatePercentage) {

					int oldPrio = oldPriorities.get(nearestNeighborID);
					//TODO Double check this priority calculation
					int newPrio = calculatePriority(this.getNode(nearestNeighborID), true, polledNode.getId(), edgeID);
					oldPriorities.replace(nearestNeighborID, newPrio);
					// logger.debug("\t[LAZY UPDATE] NodeID: {}, Priority: {}",
					// nearestNeighborID, newPrio);

					if (newPrio != oldPrio) {

						// TODO High chances to be a wrong way to remove a node!
						sortedNodesQueue.remove(this.getNode(nearestNeighborID));
						this.getNode(nearestNeighborID).setPriority(newPrio);
						this.updateNodeInfo(this.getNode(nearestNeighborID), this.getNode(nearestNeighborID).getLevel(),
								newPrio);
						sortedNodesQueue.add((CHNodeImpl) this.getNode(nearestNeighborID));

					}
				}
			}

		}
		
		this.save();
	}

	/**
	 * Adds a new shortcut to the graph or update other ones already in the
	 * graph.
	 * 
	 * @param node
	 * @return
	 */
	private int addShortcuts(long node) {

		possibleShortcuts.clear();

		findShortcut(this.getNode(node), true);

		int temporaryShortcutCounter = 0;
		for (CHEdge shortcutEntry : possibleShortcuts.get(node)) {

			// Check if we need to update some existing shortcuts in the graph
			boolean updatedInGraph = this.updateShortcuts(shortcutEntry);

			if (!updatedInGraph) {

				String newLabel = "Shortcut " + shortcutEntry.getFromNode() + "-" + shortcutEntry.getToNode();

				CHEdge newShortcut = new CHEdgeImpl(shortcutEntry.getFromNode(), shortcutEntry.getToNode(),
						shortcutEntry.getDistance(), shortcutEntry.getOriginalEdgeCounter(),
						shortcutEntry.getContractedNodeId(), shortcutEntry.getOutgoingSkippedEdge(),
						shortcutEntry.getIngoingSkippedEdge(), newLabel, true);

				this.addEdge(newShortcut);
				temporaryShortcutCounter++;

			}

		}
		
		return temporaryShortcutCounter;

	}

	private boolean updateShortcuts(CHEdge shortcutEntry) {
		for (Long edgeID : this.getOutEdges(shortcutEntry.getFromNode())) {

			CHEdge edge = this.getEdge(edgeID);

			if (edge.isShortcut() && edge.getToNode() == shortcutEntry.getToNode()) {

				if (shortcutEntry.getDistance() >= edge.getDistance()) {
					return false;
				}

				if (edge.getId() == shortcutEntry.getOutgoingSkippedEdge()
						|| edge.getId() == shortcutEntry.getIngoingSkippedEdge()) {
					throw new IllegalStateException();
				}

				edge.setDistance(shortcutEntry.getDistance());
				edge.setIngoingSkippedEdge(shortcutEntry.getIngoingSkippedEdge());
				edge.setOutgoingSkippedEdge(shortcutEntry.getOutgoingSkippedEdge());
				edge.setOriginalEdgeCounter(shortcutEntry.getOriginalEdgeCounter());

				return true;

			}
		}
		return false;
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
		this.sortedNodesQueue = nodePriorityQueue;
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

	public Queue<CHNodeImpl> getNodePriorityQueue() {
		return sortedNodesQueue;
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

	/**
	 * This method returns the lowest value of distance for the ingoing edges in
	 * case that we have more than one edge connecting the same source and
	 * destination nodes.
	 * 
	 * @param n
	 *            baseNode that we will verify the ongoing edges.
	 * @param fromNodeId
	 * @return the lowest distance among all edges with the same fromNode and
	 *         toNode.
	 */
	private int getIngoingLowestEdgeValue(Node n, long fromNodeId) {
		int ingoingDistance = Integer.MAX_VALUE;
		for (Long comparisonEdgeId : this.getInEdges(n.getId())) {
			if (this.getEdge(comparisonEdgeId).getFromNode() == fromNodeId
					&& this.getEdge(comparisonEdgeId).getDistance() <= ingoingDistance) {
				ingoingDistance = this.getEdge(comparisonEdgeId).getDistance();
			}
		}
		return ingoingDistance;
	}

	/**
	 * This method returns the lowest value of distance for the outgoing edges
	 * in case that we have more than one edge connecting the same source and
	 * destination nodes.
	 * 
	 * @param n
	 *            baseNode that we will verify the ongoing edges.
	 * @param toNodeId
	 * @return the lowest distance among all edges with the same fromNode and
	 *         toNode.
	 */
	private int getOutgoingLowestEdgeValue(Node n, long toNodeId) {
		int outgoingDistance = Integer.MAX_VALUE;
		for (Long comparisonEdgeId : this.getOutEdges(n.getId())) {
			if (this.getEdge(comparisonEdgeId).getToNode() == toNodeId
					&& this.getEdge(comparisonEdgeId).getDistance() <= outgoingDistance) {
				outgoingDistance = this.getEdge(comparisonEdgeId).getDistance();
			}
		}
		return outgoingDistance;
	}

}
