package org.graphast.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.graphast.app.GraphInfo;
import org.graphast.exception.GraphastException;
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

	public static void setProperty(String key, String value) {
		config.setProperty(key, value);
	}	
	
	/** Carrega ou recarrega as configurações da aplicação a partir do 
	 * arquivo de propriedades config.properties.
	 */
	@SuppressWarnings({ "unchecked", "serial" })
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

			Enumeration<String> props = (Enumeration<String>) config.propertyNames();
            while(props.hasMoreElements()){
                    String s = props.nextElement();
                    config.setProperty(s, config.getProperty(s).replace("${user.home}", USER_HOME));
            }
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
	
	public static void save(GraphInfo graphInfo) {
		String appName = graphInfo.getAppName();
		config.setProperty("graphast." + appName + ".dir", graphInfo.getGraphDir());
		config.setProperty("graphast." + appName + ".edges", String.valueOf(graphInfo.getNumberOfEdges()));
		config.setProperty("graphast." + appName + ".nodes", String.valueOf(graphInfo.getNumberOfNodes()));
		config.setProperty("graphast." + appName + ".size", String.valueOf(graphInfo.getSize()));
		save();
	}
	
	public static void addApp(GraphInfo graphInfo) {
		getApps().add(graphInfo);
		config.setProperty("graphast.apps", getAppsNames(getApps()));
	}
	
	public static Properties getConfig() {
		return config;
	}
	
	public static List<GraphInfo> getApps() {
		String apps = config.getProperty("graphast.apps");
		if (apps != null) {
			List<GraphInfo> result = new ArrayList<GraphInfo>();
			List<String> appsList = Arrays.asList(apps.split(","));
			for (String app : appsList) {
				GraphInfo graphInfo = new GraphInfo();
				graphInfo.setAppName(app);
				graphInfo.setGraphDir(config.getProperty("graphast." + app + ".dir"));
				
				String propertyName = "graphast." + app + ".edges";
				if (config.getProperty(propertyName) != null) {
					graphInfo.setNumberOfEdges(Long.valueOf(config.getProperty(propertyName)));
				}
				
				propertyName = "graphast." + app + ".nodes";
				if (config.getProperty(propertyName) != null) {
					graphInfo.setNumberOfNodes(Long.valueOf(config.getProperty(propertyName)));
				}
				
				propertyName = "graphast." + app + ".size";
				if (config.getProperty(propertyName) != null) {
					graphInfo.setSize(Long.valueOf(config.getProperty(propertyName)));
				}
				result.add(graphInfo);
			}
			return result;
		}
		return null;
	}

	
	public static String getSelectedApp() {
		return config.getProperty("graphast.selected.app");
	}

	public static void setSelectedApp(String app) {
		config.setProperty("graphast.selected.app", app);
	}
	
	private static String getAppsNames(List<GraphInfo> list) {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i < list.size(); i++) {
			sb.append(list.get(i).getAppName());
			if (i < list.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
}