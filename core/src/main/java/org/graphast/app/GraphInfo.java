package org.graphast.app;

import java.util.List;

public class GraphInfo {

	private String appName;
	private String graphDir;
	private long numberOfNodes;
	private long numberOfEdges;
	private int numberOfPoIs;
	private int numberOfPoICategories;
	private long size;
	private String network;
	private String importer;
	private String pois;
	private String costs;
	private List<Integer> poiCategoryFilter;
	private String queryServices;
	
	public GraphInfo() {
		super();
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

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getImporter() {
		return importer;
	}

	public void setImporter(String importer) {
		this.importer = importer;
	}

	public String getPois() {
		return pois;
	}

	public void setPois(String pois) {
		this.pois = pois;
	}

	public String getCosts() {
		return costs;
	}

	public void setCosts(String costs) {
		this.costs = costs;
	}

	public List<Integer> getPoiCategoryFilter() {
		return poiCategoryFilter;
	}

	public void setPoiCategoryFilter(List<Integer> poiCategoryFilter) {
		this.poiCategoryFilter = poiCategoryFilter;
	}

	public int getNumberOfPoIs() {
		return numberOfPoIs;
	}

	public void setNumberOfPoIs(int numberOfPoIs) {
		this.numberOfPoIs = numberOfPoIs;
	}

	public int getNumberOfPoICategories() {
		return numberOfPoICategories;
	}

	public void setNumberOfPoICategories(int numberOfPoICategories) {
		this.numberOfPoICategories = numberOfPoICategories;
	}

	public String getQueryServices() {
		return queryServices;
	}

	public void setQueryServices(String queryServices) {
		this.queryServices = queryServices;
	}
	
}
