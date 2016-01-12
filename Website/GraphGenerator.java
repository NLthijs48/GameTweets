import java.io.*;
import java.util.*;

public class GraphGenerator {
	/* Target data structure per game:
	   [
			[<timestamp>, <count>],
			[...]
	   ]
	 */

	// <game, <timestamp, count>>
	private Map<String, Map<Long, Double>> gameStats;

	public GraphGenerator() {
		collectResults();
		printDataFile();
	}

	public void collectResults() {
		progress("Collecting results");
		gameStats = new TreeMap<>();
		File inputFolder = new File("C:\\Coding\\IntelliJ IDEA\\Managing Big Data\\data");
		try {
			File[] resultFiles = inputFolder.listFiles();
			if(resultFiles == null || resultFiles.length == 0) {
				System.err.println("No result files to read from at "+inputFolder.getAbsolutePath());
				return;
			}
			for (File resultFile : resultFiles) {
				if(!resultFile.isFile() || resultFile.getName().startsWith("_") || resultFile.getName().endsWith(".zip")) {
					continue;
				}
				progress("  Reading file: "+resultFile.getName());
				try(BufferedReader reader = new BufferedReader(new FileReader(resultFile))) {
					String line = reader.readLine();
					while(line != null) {
						// Skip empty lines (the last one normally)
						if(line.length() == 0) {
							continue;
						}
						// Detect errors
						if(line.startsWith("Error")) {
							System.err.println("  Found error line: "+line);
							line = reader.readLine();
							continue;
						}
						// Split game name, date and score
						String[] parts = line.split("-|\t");
						if(parts.length < 5) {
							System.err.println("  Only "+parts.length+" parts in line: "+line);
							line = reader.readLine();
							continue;
						}
						// Parse date
						Calendar date = Calendar.getInstance();
						date.setTimeInMillis(0);
						date.set(Calendar.YEAR, parseInt(parts[1]));
						date.set(Calendar.MONTH, parseInt(parts[2])-1);
						date.set(Calendar.DAY_OF_MONTH, parseInt(parts[3]));
						// Parse score
						double score = 0;
						try {
							score = Double.parseDouble(parts[4]);
						} catch (IllegalFormatException e) {
							System.err.println("  Could not parse score: "+score);
						}
						// Add to map
						String gameName = parts[0];
						if (gameName.endsWith("Ghost")) {
							gameName += "s";
						} else if (gameName.contains("Revaltions")) {
							gameName = gameName.replace("Revaltions", "Revelations");
						}
						Map<Long, Double> gameMap = gameStats.get(gameName);
						if(gameMap == null) {
							gameMap = new TreeMap<>();
						}
						gameMap.put(date.getTimeInMillis(), score);
						gameStats.put(gameName, gameMap);
						// Read next line
						line = reader.readLine();
					}
				} catch (IOException e) {
					System.err.println("  Something went wrong reading result file:");
					e.printStackTrace(System.err);
				}
			}
		}
		catch(SecurityException e) {
			System.err.println("No permission to read file");
			e.printStackTrace();
		}
		progress("Filling gaps in the data");
		long firstTime = 1292457600000L;
		long lastTime = 1446681600000L;
		long period = 86400000L;
		for (Map.Entry<String, Map<Long, Double>> gameMap : gameStats.entrySet()) {
			long currentTime = firstTime;
			int fillCount = 0;
			int totalCount = 0;
			Map<Long, Double> valueMap = gameMap.getValue();
			while (currentTime <= lastTime) {
				totalCount++;
				if (!valueMap.containsKey(currentTime) && !valueMap.containsKey(currentTime-3600000L)) { // Account for 1 hour offset because of winter/summer time
					valueMap.put(currentTime, 0.0);
					fillCount++;
				}
				currentTime += period;
			}
			gameMap.setValue(valueMap);
			progress("  Filled " + fillCount + " values for " + gameMap.getKey()+", totalcount="+totalCount);
		}
		progress("Done with collecting results, found games: " + gameStats.keySet().toString());
	}

	public void printDataFile() {
		progress("Printing data file");
		File output = new File("C:\\Coding\\IntelliJ IDEA\\Managing Big Data\\result\\data.php");
		try {
			output.getParentFile().mkdirs();
			output.createNewFile();
		} catch (IOException e) {
			System.err.println("Something went wrong while creating the result file:");
			e.printStackTrace(System.err);
		}

		try(BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
			writer.write("var stats = {};\n");
			for(String game : gameStats.keySet()) {
				progress("  Printing data: "+game);
				Map<Long, Double> gameMap = gameStats.get(game);
				writer.write("stats[\""+game+"\"] = [");
				boolean first = true;
				for(Map.Entry<Long, Double> entry : gameMap.entrySet()) {
					if(first) {
						writer.write("["+entry.getKey()+", "+entry.getValue()+"]");
						first = false;
					} else {
						writer.write(", [" + entry.getKey() + ", " + entry.getValue() + "]");
					}
				}
				writer.write("];\n");
			}
		} catch (IOException e) {
			System.err.println("Something went wrong while writing the result file:");
			e.printStackTrace(System.err);
		}
		progress("Done printing data file");
	}

	/**
	 * Print message to the standard output
	 * @param message The message to print
	 */
	public void progress(String message) {
		System.out.println(message);
	}

	/**
	 * parse number
	 * @param number The number string to parse
	 * @return The resulting number
	 */
	public int parseInt(String number) {
		int result = 0;
		try {
			result = Integer.parseInt(number);
		} catch (IllegalFormatException e) {
			System.err.println("Could not parse number: "+number);
		}
		return result;
	}


	public static void main(String[] args) {
		new GraphGenerator();
	}
}
