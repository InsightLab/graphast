package org.graphast.model.contraction;

import static org.graphast.util.GeoUtils.latLongToDouble;

import java.util.HashMap;
import java.util.HashSet;
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

	Map<Long, Set<CHEdge>> possibleShortcuts = new HashMap<>();

	private Queue<CHNodeImpl> nodePriorityQueue = new PriorityQueue<>();

	private Map<Long, Integer> oldPriorities = new HashMap<>();

	private int neighborUpdatePercentage = 20;
	private final Random rand = new Random(123);

	private CHGraph reverseGraph;

	private ShortestPathService shortestPath;
	private int maximumEdgeCount, maxLevel;

	public CHGraphImpl() {
		super();
	}

	// public CHGraphImpl(Graph graph) {
	// super(graph.getDirectory());
	// graph.load();
	//
	// this.
	// }

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

	public void updateNodeInfo(CHNode n, int maxLevel) {

		super.updateNodeInfo(n);

		long position = n.getId() * CHNode.NODE_BLOCKSIZE;

		nodesComplement.set(position, n.getPriority());
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

		long position = id * CHNode.NODE_BLOCKSIZE;

		chNode.setPriority(nodesComplement.getInt(position));
		chNode.setLevel(nodesComplement.getInt(position + 1));

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

			long position = i * Node.NODE_BLOCKSIZE;

			double latitude = latLongToDouble(this.getNodes().getInt(position + 3));
			double longitude = latLongToDouble(this.getNodes().getInt(position + 4));

			long nodeId = this.getNodeId(latitude, longitude);

			this.updateNodeInfo(this.getNode(nodeId), maxLevel);

		}

		for (int i = 0; i < this.getNumberOfNodes(); i++) {

			long position = i * Node.NODE_BLOCKSIZE;

			double latitude = latLongToDouble(this.getNodes().getInt(position + 3));
			double longitude = latLongToDouble(this.getNodes().getInt(position + 4));

			long nodeId = this.getNodeId(latitude, longitude);

			int priority = calculatePriority(this.getNode(nodeId));

			oldPriorities.put(nodeId, priority);
			
			CHNode node = this.getNode(nodeId);
			node.setPriority(priority);
			node.setLevel(maxLevel);
			
			nodePriorityQueue.add((CHNodeImpl) node);

		}

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

		int priority = 10 * edgeDifference + originalEdgeCount + numberOfContractedNeighbors;

		System.out.println("NID: " + n.getId() + ", Edge Difference: " + edgeDifference + ", Original Edges Count: "
				+ originalEdgeCount + ", Contracted Neighbors: " + numberOfContractedNeighbors);
		System.out.println("\t Priority: " + priority);

		return priority;

	}

	// Consider the following "graph": u --> u --> w
	public void findShortcut(Node n) {

		Set<CHEdge> possibleLocalShortcut = new HashSet<>();

		// Arestas chegando no nó N (equivalente aos outgoings do grafo reverso).
		// Nó TO das arestas outgoings do grafo reverso serão os FROM do grafo original.
		for (Long edgeIngoing : this.getReverseGraph().getOutEdges(n.getId())) {

			Long fromNode = this.getReverseGraph().getEdge(edgeIngoing).getToNode();
			
			if(this.getNode(fromNode).getLevel()!=maxLevel) {
				continue;
			}

			// Arestas outgoing do nó N. O nó TO será o nó final.
			for (Long edgeOutgoing : this.getOutEdges(n.getId())) {

				Long toNode = this.getEdge(edgeOutgoing).getToNode();
				
				//TODO Explicar essa verificação
				if (this.getNode(toNode).getLevel() != maxLevel || toNode == fromNode) {
					continue;
				}

				long previsousReverseEdgeId = 0l;
				double ingoingDistance = this.getReverseGraph().getEdge(edgeIngoing).getDistance();
				
				// Arestas de entrada no nó "n"
				for (Long reverseEdge : this.getReverseGraph().getOutEdges(n.getId())) {
					if (this.getReverseGraph().getEdge(reverseEdge).getToNode() == fromNode
							&& this.getReverseGraph().getEdge(reverseEdge).getDistance() <= this.getReverseGraph().getEdge(previsousReverseEdgeId).getDistance()) {
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

				Path path = shortestPath.shortestPath(fromNode, toNode);
				path.getTotalDistance();

				if (path.getTotalDistance() < shortestPathBeforeContraction) {
					// System.out.println("WITNESS PATH FOUND");
				} else {
					if (path.getTotalDistance() == shortestPathBeforeContraction) {

						if (this.getEdge(path.getEdges().get(0))
								.getFromNode() == this
										.getEdge(this.getReverseGraph().getEdge(edgeIngoing).getToNode(),
												this.getReverseGraph().getEdge(edgeIngoing).getFromNode())
										.getFromNode()
								&& this.getEdge(path.getEdges().get(0))
										.getToNode() == this
												.getEdge(this.getReverseGraph().getEdge(edgeIngoing).getToNode(),
														this.getReverseGraph().getEdge(edgeIngoing).getFromNode())
												.getToNode()
								&& this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getFromNode() == this
										.getEdge(edgeOutgoing).getFromNode()
								&& this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getToNode() == this
										.getEdge(edgeOutgoing).getToNode()) {

							// System.out.println("CREATE SHORTCUT");
							int ingoingEdgeCounter = this.getEdge(path.getEdges().get(0)).getOriginalEdgeCounter();
							int outgoingEdgeCounter = this.getEdge(path.getEdges().get(path.getEdges().size() - 1))
									.getOriginalEdgeCounter();
							CHEdge shortcut = new CHEdgeImpl(
									this.getEdge(path.getEdges().get(0)).getFromNode(),
									this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getToNode(),
									this.getEdge(path.getEdges().get(0)).getDistance() + this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getDistance(),
									ingoingEdgeCounter + outgoingEdgeCounter, 
									n.getId(),
									this.getEdge(path.getEdges().get(0)).getId(),
									this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getId(), 
									true);
							
							
							// Verificando se já existe um shortcut igual
							boolean existShortcut = false;
							for(CHEdge edge : possibleLocalShortcut) {
								if(edge.getIngoingSkippedEdge() == shortcut.getIngoingSkippedEdge() &&
										edge.getOutgoingSkippedEdge() == shortcut.getOutgoingSkippedEdge()) {
									existShortcut = true;
									break;
								}
							}
							
							if(existShortcut == false) {
								possibleLocalShortcut.add(shortcut);
							}
							
							

						} else {
							// System.out.println("WITNESS PATH FOUND");
						}
					} else {
						// System.out.println("CREATE SHORTCUT");
						int ingoingEdgeCounter = this.getEdge(path.getEdges().get(0)).getOriginalEdgeCounter();
						int outgoingEdgeCounter = this.getEdge(path.getEdges().get(path.getEdges().size() - 1))
								.getOriginalEdgeCounter();
						CHEdge shortcut = new CHEdgeImpl(this.getEdge(path.getEdges().get(0)).getFromNode(),
								this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getToNode(),
								(int)shortestPathBeforeContraction,
								ingoingEdgeCounter + outgoingEdgeCounter, 
								n.getId(),
								this.getEdge(path.getEdges().get(0)).getId(),
								this.getEdge(path.getEdges().get(path.getEdges().size() - 1)).getId(), 
								true);

						// Verificando se já existe um shortcut igual
						boolean existShortcut = false;
						for(CHEdge edge : possibleLocalShortcut) {
							if(edge.getIngoingSkippedEdge() == shortcut.getIngoingSkippedEdge() &&
									edge.getOutgoingSkippedEdge() == shortcut.getOutgoingSkippedEdge()) {
								existShortcut = true;
								break;
							}
						}
						
						if(existShortcut == false) {
							possibleLocalShortcut.add(shortcut);
						}
					}
				}
			}

		}

		// se o shortcut for criado -> meanDegree = (meanDegree * 2 +
		// tmpDegreeCounter) / 3;

		possibleShortcuts.put(n.getId(), possibleLocalShortcut);

	}

	public void contractNodes() {

		int level = 1;

		// Recompute priority of uncontracted neighbors.
		// Without neighbor updates preparation is faster but we need them
		// to slightly improve query time. Also if not applied too often it
		// decreases the shortcut number.
		boolean neighborUpdate = true;
		// if (neighborUpdatePercentage == 0)
		// neighborUpdate = false;

		while (!nodePriorityQueue.isEmpty()) {

			CHNode polledNode = nodePriorityQueue.poll();
			int numberShortcutsCreated = this.addShortcuts(polledNode.getId());
			System.out.println("nodeID: " + polledNode.getId() + ", #shortcuts: " + numberShortcutsCreated);
			
//			this.getNode(polledNode.getId()).      setLevel(level);
			this.updateNodeInfo(polledNode, level);
			polledNode.setLevel(level);
			level++;

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
		NEXT_SC:
		for (CHEdge shortcutEntry : possibleShortcuts.get(node)) {

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

	public void setPossibleShortcuts(Map<Long, Set<CHEdge>> possibleShortcuts) {
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
					originalCHGraph.getNode(i).getLatitude(), originalCHGraph.getNode(i).getLatitude());

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

}
