package org.graphast.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	//cat  /proc/10925/io
	
	public static Long ioProcess(int pid, PROCESS process) throws IOException {
		
		final String COMMAND_PROCESS = "cat /proc/"+pid+"/io \n";
		final String COMMAND_EXIT = "exit\n";
		
		Process telnetProcess = createProcessBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(telnetProcess.getInputStream()));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(telnetProcess.getOutputStream()));
		runCommand(COMMAND_PROCESS, output);
		runCommand(COMMAND_EXIT, output);
		
		Map<String, Long> runPatterIO = runPatterIO(input);
		Long read_byte = runPatterIO.get("rchar");
		Long write_byte = runPatterIO.get("wchar");
		
		if(read_byte != null && write_byte != null) {
			if(process.equals(PROCESS.READ)) {
				return read_byte;
			} else {
				return write_byte;
			}	
		}
		return 0l;
	}
	
	private static Map<String, Long> runPatterIO(BufferedReader input) throws NumberFormatException, IOException {
		
		Map<String, Long> listMemory = new HashMap<String, Long>();
		String line;
		
		while ((line = input.readLine()) != null) {
			
			String[] split = line.split(PATTERN_SEPARATOR);
			String keyIOProcess = split[0];
			String x2 = split[1];
			Matcher matcher = Pattern.compile(PATTERN_NUMBER).matcher(x2);
			Long value = 0l;
			
			while (matcher.find()) {
				value = Long.valueOf(matcher.group());
			}
			
			listMemory.put(keyIOProcess, value);
		}
		
		return listMemory;
	}

	public enum PROCESS {
		READ,
		WRITE
	}
	
	public static List<Integer> listIdProcess() throws IOException {
		
		final String COMMAND_PROCESS = "ps -ax | grep java\n";
		final String COMMAND_EXIT = "exit\n";
		
		Process telnetProcess = createProcessBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(telnetProcess.getInputStream()));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(telnetProcess.getOutputStream()));
		runCommand(COMMAND_PROCESS, output);
		runCommand(COMMAND_EXIT, output);
		
		return runPatterProcess(input);
	}
	
	private static List<Integer> runPatterProcess(BufferedReader input) throws IOException {
			
		List<Integer> listProcess = new ArrayList<Integer>();
		String line;
		
		while ((line = input.readLine()) != null) {
			String[] PID = line.split(" ");
			if(org.apache.commons.lang3.StringUtils.isNumeric(PID[0])) {
				listProcess.add(Integer.valueOf(PID[0]));
			}
		}
			
		return listProcess;	
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
	
	public static long getUsedMemory() {
		
		Runtime.getRuntime().gc();
		long numberUseMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		return numberUseMemory;
	}
	
	public static void main(String[] args) throws IOException {
		
		List<Integer> listIdProcess = BenchmarkMemory.listIdProcess();
		for (Integer integer : listIdProcess) {
			System.out.println(BenchmarkMemory.ioProcess(integer, PROCESS.READ));
			System.out.println(BenchmarkMemory.ioProcess(integer, PROCESS.WRITE));
		}
	}
}
