package org.graphast.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.graphast.app.GraphInfo;
import org.graphast.exception.GraphastException;
import org.graphast.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

	private static Properties config;

	public static final String USER_HOME = System.getProperty("user.home");

	public static final String GRAPHAST_DIR = USER_HOME + "/graphast";

	public static final String CONFIG_FILE_BASE_NAME = "config.properties";

	public static final String CONFIG_FILE = GRAPHAST_DIR + "/" + CONFIG_FILE_BASE_NAME;
	
	private static Logger log = LoggerFactory.getLogger(Configuration.class);
	
	static {
		reload();
	}

	private Configuration() {	
		super();
	} 
	
	/**
	 * Obtém uma propriedade a partir do arquivo de propriedades que contém
	 * as configurações usadas na aplicação.
	 * Exemplo: Configuration.getProperty("app.environment")
	 * @param key Property name.
	 * @return Property value.
	 */
	public static String getProperty(String key) {
		return config.getProperty(key);
	}

	public static String getProperty(String app, String key) {
		return config.getProperty("graphast.app." + app + "." + key);
	}

	public static void setProperty(String app, String key, String value) {
		if (value != null) {
			config.setProperty("graphast.app." + app + "." + key, value);
		}
	}

	public static void setProperty(String key, String value) {
		config.setProperty(key, value);
	}	
	
	/** Carrega ou recarrega as configurações da aplicação a partir do 
	 * arquivo de propriedades config.properties.
	 */
	@SuppressWarnings("serial")
	public static void reload() {
		try {
			log.info("user.dir: {}", System.getProperty("user.dir"));
			log.info("user.home: {}", USER_HOME);

			config = new Properties() {
			    @Override
				// In order to store ordered keys
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};

			File userConfigFile = new File(CONFIG_FILE);
			if (! userConfigFile.exists()) {
				InputStream is = Configuration.class.getResourceAsStream("/" + CONFIG_FILE_BASE_NAME);
				Files.copy(is, userConfigFile.toPath());
			}

			log.info("config file: {}", CONFIG_FILE);
			config.load(new FileInputStream(CONFIG_FILE));

			log.info("graphast.apps: {}", getApps());
			log.info("graphast.selected.app: {}", getSelectedApp());

		} catch (Exception e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void save() {
		try {
			config.store(new FileOutputStream(CONFIG_FILE), null);
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static GraphInfo load(String app) {
		GraphInfo result = new GraphInfo();
		if (app != null) 
			result.setAppName(app);
		if (getProperty(app, "costs") != null) 
			result.setCosts(getProperty(app, "costs"));
		if (getProperty(app, "dir") != null) 
			result.setGraphDir(getProperty(app, "dir"));
		if (getProperty(app, "importer") != null) 
			result.setImporter(getProperty(app, "importer"));
		if (getProperty(app, "network") != null) 
			result.setNetwork(getProperty(app, "network"));
		if (getProperty(app, "edges") != null) 
			result.setNumberOfEdges(Long.parseLong(getProperty(app, "edges")));
		if (getProperty(app, "nodes") != null) 
			result.setNumberOfNodes(Long.parseLong(getProperty(app, "nodes")));
		if (getProperty(app, "pois") != null) 
			result.setPois(getProperty(app, "pois"));
		if (getProperty(app, "size") != null) 
			result.setSize(Long.parseLong(getProperty(app, "size")));
		if (getProperty(app, "number-pois") != null) 
			result.setNumberOfPoIs(Integer.parseInt(getProperty(app, "number-pois")));
		if (getProperty(app, "number-poi-categories") != null) 
			result.setNumberOfPoICategories(Integer.parseInt(getProperty(app, "number-poi-categories")));
		if (getProperty(app, "query-services") != null) 
			result.setQueryServices(getProperty(app, "query-services"));
		
		String filter = getProperty(app, "poi.category.filter");
		if (filter != null) {
			result.setPoiCategoryFilter(StringUtils.splitIntToList(",", filter));
		}
		return result;
	}
	
	public static void save(GraphInfo graphInfo) {
		String app = graphInfo.getAppName();
		setProperty(app, "costs", graphInfo.getCosts());
		setProperty(app, "dir", graphInfo.getGraphDir());
		setProperty(app, "importer", graphInfo.getImporter());
		setProperty(app, "network", graphInfo.getNetwork());
		setProperty(app, "edges", String.valueOf(graphInfo.getNumberOfEdges()));
		setProperty(app, "nodes", String.valueOf(graphInfo.getNumberOfNodes()));
		setProperty(app, "pois", graphInfo.getPois());
		setProperty(app, "size", String.valueOf(graphInfo.getSize()));
		setProperty(app, "number-pois", String.valueOf(graphInfo.getNumberOfPoIs()));
		setProperty(app, "number-poi-categories", String.valueOf(graphInfo.getNumberOfPoICategories()));
		setProperty(app, "query-services", String.valueOf(graphInfo.getQueryServices()));

		if (graphInfo.getPoiCategoryFilter() != null) {
			String filter = StringUtils.append(graphInfo.getPoiCategoryFilter());
			setProperty(app, "poi.category.filter", filter);
		}
		save();
	}

	public static Properties getConfig() {
		return config;
	}
	
	public static List<String> getAppNames() {
		List<String> appsList = new ArrayList<String>();
		Set<Object> set = config.keySet();
		for (Object o : set) {
			String s = o.toString();
			if (s.startsWith("graphast.app.")) {
				String appName = s.substring(13,s.lastIndexOf("."));
				if (! appsList.contains(appName)) {
					appsList.add(appName);
				}
			}
		}
		Collections.sort(appsList);
		return appsList;
	}
	
	public static List<GraphInfo> getApps() {
		List<String> appsList = getAppNames();
		List<GraphInfo> result = new ArrayList<GraphInfo>();
		for (String app : appsList) {
			result.add(load(app));
		}
		return result;
	}

	public static String getSelectedApp() {
		return config.getProperty("graphast.selected.app");
	}

	public static void setSelectedApp(String app) {
		config.setProperty("graphast.selected.app", app);
	}
	
}