package me.wiefferink.gametweets;

import com.jaunt.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VGChartzScraper {

	private Map<String, Map<Long, Double>> soldStats;
	private UserAgent userAgent;
	private Set<String> doLater = new HashSet<>();

	public VGChartzScraper() {
		soldStats = new HashMap<>();
		userAgent = new UserAgent();
		Set<String> baseLinks = new HashSet<>(Arrays.asList(
				"http://www.vgchartz.com/game/85359/call-of-duty-black-ops-3/",
				"http://www.vgchartz.com/game/65884/grand-theft-auto-v/",
				"http://www.vgchartz.com/game/70617/assassins-creed-iii/",
				"http://www.vgchartz.com/game/71597/assassins-creed-iv-black-flag/",
				"http://www.vgchartz.com/game/51022/assassins-creed-revelations/",
				"http://www.vgchartz.com/game/40231/battlefield-3/",
				"http://www.vgchartz.com/game/71510/battlefield-4/",
				"http://www.vgchartz.com/game/81797/call-of-duty-advanced-warfare/",
				"http://www.vgchartz.com/game/85358/call-of-duty-black-ops-3/",
				"http://www.vgchartz.com/game/70717/call-of-duty-black-ops-ii/",
				"http://www.vgchartz.com/game/72087/call-of-duty-ghosts/",
				"http://www.vgchartz.com/game/44604/call-of-duty-modern-warfare-3/",
				"http://www.vgchartz.com/game/24178/diablo-iii/",
				"http://www.vgchartz.com/game/50884/fifa-12/",
				"http://www.vgchartz.com/game/70738/fifa-soccer-13/",
				"http://www.vgchartz.com/game/72084/fifa-14/",
				"http://www.vgchartz.com/game/82916/fifa-15/",
				"http://www.vgchartz.com/game/85615/fifa-16/",
				"http://www.vgchartz.com/game/80324/far-cry-4/",
				"http://www.vgchartz.com/game/51765/minecraft/",
				"http://www.vgchartz.com/game/49112/the-elder-scrolls-v-skyrim/"
		));
		download(baseLinks);
		download(doLater);
		GraphGenerator.printDataFile(soldStats, "sold.php", "sold");
	}

	public void download(Set<String> links) {
		progress("Downloading data");
		for(String link : links) {
			if(!link.endsWith("Europe/")) {
				link = link+"Europe/";
			}
			progress("  doing: "+link);
			try {
				Map<Long, Double> gameSold = new TreeMap<>();
				userAgent.visit(link);
				Document document = userAgent.doc;
				// Find game name
				String gameName = document.findFirst("<h1>").findFirst("<a>").getText();
                gameName = gameName.replace(" Soccer ", " ");

				// Find current platform
				String platform = document.findFirst("<table id='game_infobox'>").findEvery("<tr>").getElement(1).findFirst("<td>").findFirst("<a>").getText();
				progress("  " + gameName + " on " + platform);
				if(!links.equals(doLater)) {
                    // Find game on other platforms
                    Elements alsoOn = document.findFirst("<table id='game_infobox'>").findEvery("<tr>").getElement(1).getElement(1).findEvery("<a>");
                    for (Element crossLink : alsoOn) {
                        doLater.add(crossLink.getAt("href"));
                        progress("    added " + crossLink.getAt("href"));
                    }
                }

                // Check if there are stats
                if(!document.findFirst("<h2>").getText().contains("Europe First Ten Weeks")) {
                    error("  No table with stats for "+gameName+" on "+platform);
                    continue;
                }

				// Get stats
				Elements rows = document.findFirst("<div id='game_table_box'>").findFirst("<table>").findEach("<tr>");
				for (Element row : rows) {
					Elements cells = row.findEach("<td>");
					if (cells.size() == 0) { // Skip header row (has <th>)
						continue;
					}
					// Process date
					String date = cells.getElement(0).findFirst("<a>").getText();
					SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMMMMMMMM yyyy");
					// Remove day indicators, hard to parse
					date = date.substring(0,2) + date.substring(4);
					Date parsedDate = new Date();
					try {
						parsedDate = format.parse(date);
					} catch (ParseException e) {
						error("    Could not parse date: " + date);
					}
					Calendar calendar = null;
					if (parsedDate != null) {
						calendar = Calendar.getInstance();
						calendar.setTime(parsedDate);
					}

					String count = cells.getElement(2).getText();
					String cleanedCount = count.replace(",", "");
					double parsedCount = -1;
					try {
						parsedCount = Double.parseDouble(cleanedCount);
					} catch (NumberFormatException e) {
						error("    Could not parse count: " + count);
					}

					if (calendar != null && parsedCount != -1) {
						//progress("    at " + parsedDate + " count is " + parsedCount);
						gameSold.put(calendar.getTimeInMillis()-302400000L, parsedCount); // Offset by 0.5 week
						soldStats.put(gameName+"@"+platform, gameSold);
					}

				}
			} catch (JauntException e) {
				error("Something went wrong with downloading the data:");
				e.printStackTrace(System.err);
			}
		}
	}

	public static void main(String[] args) {
		new VGChartzScraper();
	}

	/**
	 * Print message to the standard output
	 *
	 * @param message The message to print
	 */
	public void progress(String message) {
		System.out.println(message);
	}

	/**
	 * Print message to the error output
	 *
	 * @param message The message to print
	 */
	public void error(String message) {
		System.err.println(message);
	}
}
