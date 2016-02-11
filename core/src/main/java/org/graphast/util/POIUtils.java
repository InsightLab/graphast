package org.graphast.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.graphast.exception.GraphastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.morbz.osmpoispbf.Filter;
import de.morbz.osmpoispbf.FilterFileParser;
import de.morbz.osmpoispbf.Poi;
import de.morbz.osmpoispbf.Scanner;
import de.morbz.osmpoispbf.utils.StopWatch;
import net.morbz.osmonaut.EntityFilter;
import net.morbz.osmonaut.IOsmonautReceiver;
import net.morbz.osmonaut.Osmonaut;
import net.morbz.osmonaut.osm.Entity;
import net.morbz.osmonaut.osm.EntityType;
import net.morbz.osmonaut.osm.Tags;
import net.morbz.osmonaut.osm.Way;

public class POIUtils extends Scanner {
	// Const
	private static final String VERSION = "v1.1";
	
	// Vars
	private static Writer writer;
	private static List<Filter> filters;
	private static Options options;
	private static boolean onlyClosedWays = true;
	private static boolean printPois = false;
	private static int poisFound = 0;
	private static String[] requiredTags = { "name" };
	private static String[] outputTags = { "name" };
	private static int lastPrintLen = 0;
	private static long lastMillis = 0;
		
	private static Logger log = LoggerFactory.getLogger(POIUtils.class);
	
	public static void execute(String[] args) {
		log.info("OsmPoisPbf " + VERSION + " started");
		
		// Get input file
		if(args.length < 1) {
			throw new GraphastException("Error: Please provide an input file");
		}
		String inputFile = args[args.length - 1];
		
		// Get output file
		String outputFile;
		int index = inputFile.indexOf('.');
		if(index != -1) {
			outputFile = inputFile.substring(0, index);
		} else {
			outputFile = inputFile;
		}
		outputFile += ".csv";
		
		// Setup CLI parameters
		options = new Options();
		options.addOption("ff", "filterFile", true, "The file that is used to filter categories");
		options.addOption("of", "outputFile", true, "The output CSV file to be written");
		options.addOption("rt", "requiredTags", true, "Comma separated list of tags that are required [name]");
		options.addOption("ot", "outputTags", true, "Comma separated list of tags that are exported [name]");
		options.addOption("r", "relations", false, "Parse relations");
		options.addOption("nw", "noWays", false, "Don't parse ways");
		options.addOption("nn", "noNodes", false, "Don't parse nodes");
		options.addOption("u", "allowUnclosedWays", false, "Allow ways that aren't closed");
		options.addOption("d", "decimals", true, "Number of decimal places of coordinates [7]");
		options.addOption("s", "separator", true, "Separator character for CSV [|]");
		options.addOption("v", "verbose", false, "Print all found POIs");
		options.addOption("h", "help", false, "Print this help");
		
		// Parse parameters
		CommandLine line = null;
		try {
			line = (new DefaultParser()).parse(options, args);
		} catch(ParseException exp) {
	        System.err.println(exp.getMessage());
	        printHelp();
			throw new GraphastException();
	    }
		
		// Help
		if(line.hasOption("help")) {
			printHelp();
			return;
		}
		
		// Get filter file
		String filterFile = null;
		if(line.hasOption("filterFile")) {
			filterFile = line.getOptionValue("filterFile");
		}
		
		// Get output file
		if(line.hasOption("outputFile")) {
			outputFile = line.getOptionValue("outputFile");
		}
		
		// Check files
		if(inputFile.equals(outputFile)) {
			throw new GraphastException("Error: Input and output files are the same");
		}
		File file = new File(inputFile);
		if(!file.exists()) {
			throw new GraphastException("Error: Input file doesn't exist");
		}
		
		// Check OSM entity types
		boolean parseNodes = true;
		boolean parseWays = true;
		boolean parseRelations = false;
		if(line.hasOption("noNodes")) {
			parseNodes = false;
		}
		if(line.hasOption("noWays")) {
			parseWays = false;
		}
		if(line.hasOption("relations")) {
			parseRelations = true;
		}
		
		// Unclosed ways allowed?
		if(line.hasOption("allowUnclosedWays")) {
			onlyClosedWays = false;
		}
		
		// Get CSV separator
		char separator = '|';
		if(line.hasOption("separator")) {
			String arg = line.getOptionValue("separator");
			if(arg.length() != 1) {
				throw new GraphastException("Error: The CSV separator has to be exactly 1 character");
			}
			separator = arg.charAt(0);
		}
		Poi.setSeparator(separator);
		
		// Set decimals
		int decimals = 7; // OSM default
		if(line.hasOption("decimals")) {
			String arg = line.getOptionValue("decimals");
			try {
				int dec = Integer.valueOf(arg);
				if(dec < 0) {
					throw new GraphastException("Error: Decimals must not be less than 0");
				} else {
					decimals = dec;
				}
			} catch(NumberFormatException ex) {
				throw new GraphastException("Error: Decimals have to be a number");
			}
		}
		Poi.setDecimals(decimals);
		
		// Verbose mode?
		if(line.hasOption("verbose")) {
			printPois = true;
		}
		
		// Required tags
		if(line.hasOption("requiredTags")) {
			String arg = line.getOptionValue("requiredTags");
			requiredTags = arg.split(",");
		}
		
		// Output tags
		if(line.hasOption("outputTags")) {
			String arg = line.getOptionValue("outputTags");
			outputTags = arg.split(",");
		}
		
		// Get filter rules
		FilterFileParser parser = new FilterFileParser(filterFile);
		filters = parser.parse();
		if(filters == null) {
			throw new GraphastException();
		}

		// Setup CSV output
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF8"));
		} catch(IOException e) {
			throw new GraphastException("Error: Output file error");
		}
		
		// Setup OSMonaut
		EntityFilter filter = new EntityFilter(parseNodes, parseWays, parseRelations);
		Osmonaut naut = new Osmonaut(inputFile, filter, false);
		
		// Start watch
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		// Start OSMonaut
		naut.scan(new IOsmonautReceiver() {
		    @Override
		    public boolean needsEntity(EntityType type, Tags tags) {
		    	// Are there any tags?
				if(tags.size() == 0) {
					return false;
				}
				
				// Check required tags
		    	for(String tag : requiredTags) {
		    		if(!tags.hasKey(tag)) {
		    			return false;
		    		}
		    	}
		    	
		    	// Check category
		        return getCategory(tags, filters) != null;
		    }

		    @Override
		    public void foundEntity(Entity entity) {
		    	// Check if way is closed
		    	if(onlyClosedWays && entity.getEntityType() == EntityType.WAY) {
		    		if(!((Way)entity).isClosed()) {
		    			return;
		    		}
		    	}
				
				// Get category
				Tags tags = entity.getTags();
				String cat = getCategory(tags, filters);
				if(cat == null) {
					return;
				}
				
				// Make OSM-ID
				String id = "";
				switch(entity.getEntityType()) {
					case NODE:
						id = "N";
						break;
					case WAY:
						id = "W";
						break;
					case RELATION:
						id = "R";
						break;
				}
				id += entity.getId();
				
				// Make output tags
				String[] values = new String[outputTags.length];
				for(int i = 0; i < outputTags.length; i++) {
					String key = outputTags[i];
					if(tags.hasKey(key)) {
						values[i] = tags.get(key);
					}
				}
		    	
		        // Make POI
				poisFound++;
				Poi poi = new Poi(values, cat, entity.getCenter(), id);
				
				// Output
				if(printPois) {
					System.out.println(poi);
				} else if(System.currentTimeMillis() > lastMillis + 40) {
					// Update counter every 40 millis
					printPoisFound();
					lastMillis = System.currentTimeMillis();
				}
				
				// Write to file
				try { 
					writer.write(poi.toCsv() + "\n");
				} catch(IOException e) {
					throw new GraphastException("Error: Output file write error");
				}
		    }
		});
		
		// Close writer
		try {
			writer.close();
		} catch(IOException e) {
			throw new GraphastException("Error: Output file close error");
		}
		
		// Output results
		stopWatch.stop();
		
		printPoisFound();
		log.info("Elapsed time in milliseconds: " + stopWatch.getElapsedTime());
		
		// Quit
		return;
	}
	
	private static void printPoisFound() {
		// Clear output
		while(lastPrintLen > 0) {
			System.out.print('\b');
			lastPrintLen--;
		}
		
		// Output count
		String newStr = poisFound + " POIs found";
		System.out.println(newStr);
		lastPrintLen = newStr.length();
	}
	
	// Print help
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp("[-options] file", options);
	}
	
	/* Categories */
	private static String getCategory(Tags tags, List<Filter> filters) {
		// Iterate filters
		String cat = null;
		for(Filter filter : filters) {
			cat = getCategoryRecursive(filter, tags, null);
			if(cat != null) {
				return cat;
			}
		}
		return null;
	}
	
	private static String getCategoryRecursive(Filter filter, Tags tags, String key) {
		// Use key of parent rule or current
		if(filter.hasKey()) {
			key = filter.getKey();
		}
		
		// Check for key/value
		if(tags.hasKey(key)) {
			if(filter.hasValue() && !filter.getValue().equals(tags.get(key))) {
				return null;
			}
		} else {
			return null;
		}
		
		// If childs have categories, those will be used
		for(Filter child : filter.childs) {
			String cat = getCategoryRecursive(child, tags, key);
			if(cat != null) {
				return cat;
			}
		}
		return filter.getCategory();
	}	
	
	public static Map<Integer, String> readPoICategories(InputStream inputStream) {
		BufferedReader br = null;
		Map<Integer, String> result = new HashMap<Integer, String>(); 
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while((line = br.readLine()) != null){
				try {
					int pos = line.indexOf(',');
					int id = Integer.parseInt(line.substring(pos + 1));
					String label = line.substring(0, pos);
					result.put(id, label);
				} catch (Exception e) {
					log.debug("Could not convert line to a PoI Category: {}", line);
				}
			}
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new GraphastException(e.getMessage(), e);
				}
			}
		}
		return result;
	}
	
}
