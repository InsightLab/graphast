package org.graphast.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BenchmarkMemory {

	private static final String PATTERN_SEPARATOR = ":";
	private static final String PATTERN_NUMBER = "-?\\d+";

	public static Map<String, Long> quantityMemory() throws IOException {

		final String COMMAND_MEMORY = "egrep --color 'Mem|Cache|Swap' /proc/meminfo\n";
		final String COMMAND_EXIT = "exit\n";

		Process telnetProcess = createProcessBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(telnetProcess.getInputStream()));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(telnetProcess.getOutputStream()));
		runCommand(COMMAND_MEMORY, output);
		runCommand(COMMAND_EXIT, output);

		return runPatter(input);
	}

	private static Map<String, Long> runPatter(BufferedReader input) throws IOException {
		
		Map<String, Long> listMemory = new HashMap<String, Long>();
		String line;
		
		while ((line = input.readLine()) != null) {

			String[] split = line.split(PATTERN_SEPARATOR);
			String keyMemory = split[0];
			String x2 = split[1];
			Matcher matcher = Pattern.compile(PATTERN_NUMBER).matcher(x2);
			Long value = 0l;
			
			while (matcher.find()) {
				value = Long.valueOf(matcher.group());
			}
			
			listMemory.put(keyMemory, value);
		}
		
		return listMemory;
	}

	private static Process createProcessBuilder() throws IOException {

		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
		processBuilder.redirectErrorStream(true);
		Process telnetProcess = processBuilder.start();
		return telnetProcess;
	}

	private static void runCommand(final String command, BufferedWriter output) throws IOException {
		output.write(command);
		output.flush();
	}
}
