package org.graphast.app;

import java.util.List;

public class GraphInfo {

	private String appName;
	private String graphDir;
	private Long numberOfNodes;
	private Long numberOfEdges;
	private Integer numberOfPoIs;
	private Integer numberOfPoICategories;
	private Long size;
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
	public Long getNumberOfNodes() {
		return numberOfNodes;
	}
	public void setNumberOfNodes(Long numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	public Long getNumberOfEdges() {
		return numberOfEdges;
	}
	public void setNumberOfEdges(Long numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
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

	public Integer getNumberOfPoIs() {
		return numberOfPoIs;
	}

	public void setNumberOfPoIs(Integer numberOfPoIs) {
		this.numberOfPoIs = numberOfPoIs;
	}

	public Integer getNumberOfPoICategories() {
		return numberOfPoICategories;
	}

	public void setNumberOfPoICategories(Integer numberOfPoICategories) {
		this.numberOfPoICategories = numberOfPoICategories;
	}

	public String getQueryServices() {
		return queryServices;
	}

	public void setQueryServices(String queryServices) {
		this.queryServices = queryServices;
	}
	
}
