package org.graphast.app;

public class GraphInfo {

	private String appName;
	private String graphDir;
	private long numberOfNodes;
	private long numberOfEdges;
	private long size;
	
	public GraphInfo() {
		super();
	}

	public GraphInfo(String appName, String graphDir, int numberOfNodes, int numberOfEdges, int size) {
		super();
		this.appName = appName;
		this.graphDir = graphDir;
		this.numberOfNodes = numberOfNodes;
		this.numberOfEdges = numberOfEdges;
		this.size = size;
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getGraphDir() {
		return graphDir;
	}
	public void setGraphDir(String graphDir) {
		this.graphDir = graphDir;
	}
	public long getNumberOfNodes() {
		return numberOfNodes;
	}
	public void setNumberOfNodes(long numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	public long getNumberOfEdges() {
		return numberOfEdges;
	}
	public void setNumberOfEdges(long numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
}
