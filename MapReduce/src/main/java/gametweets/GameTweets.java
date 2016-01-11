/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gametweets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.json.simple.parser.JSONParser;

public class GameTweets {

	private static Map<String, Set<String>> games = null;
	private static DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss Z yyyy");

	public static class ExampleMapper extends Mapper<Object, Text, Text, DoubleWritable> {

		private JSONParser parser = new JSONParser();
		private Map<String, Object> tweet;

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			if(games == null)
			{
				setupGames();
			}
			
			try {
				tweet = (Map<String, Object>) parser.parse(value.toString().replaceAll("^unknown[^\\{]*", ""));
			} catch (ClassCastException e) {
				context.write(new Text("Error 1"), new DoubleWritable(1.0));
				return;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				context.write(value, new DoubleWritable(1.0));
				return;
			}

			String text = clean((String) tweet.get("text"));
			String dateStr = (String) tweet.get("created_at");

			Date date;
			try {
				date = df.parse(dateStr);
			} catch (ParseException e) {
				context.write(new Text("Error 3"), new DoubleWritable(1.0));
				return;
			}
			Calendar c = Calendar.getInstance();
			c.setTime(date);

			for (Map.Entry<String, Set<String>> entry : games.entrySet()) {
				for (String name : entry.getValue()) {
					if (text.contains(name)) {
						context.write(new Text(
								entry.getKey() + "-"
								+ c.get(Calendar.YEAR) + "-" 
								+ c.get(Calendar.MONTH) + "-" 
								+ c.get(Calendar.DAY_OF_MONTH)
								),
								new DoubleWritable(1.0));
					}
				}
			}
		}
	}

	public static class ExampleReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

		private DoubleWritable sum = new DoubleWritable(0);

		public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
				throws IOException, InterruptedException {
			sum.set(0);
			for (DoubleWritable count : values) {
				sum.set(sum.get() + count.get());
			}
			context.write(key, sum);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 2) {
			System.err.println("Usage: exampleTwitter <in> [<in>...] <out>");
			System.exit(2);
		}
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "GameTweets");
		job.setJarByClass(GameTweets.class);
		job.setMapperClass(ExampleMapper.class);
		job.setReducerClass(ExampleReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		for (int i = 0; i < otherArgs.length - 1; ++i) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static void setupGames() {
		
		games = new HashMap<>();
		games.put("Grand Theft Auto V",
				new HashSet<>(Arrays.asList("GTA V", "GTA 5", "Grand Theft Auto V", "Grand Theft Auto 5")));
		games.put("FIFA 15", new HashSet<>(Arrays.asList("FIFA 15")));
		games.put("Call of Duty: Modern Warfare 3", new HashSet<>(Arrays.asList("MW3", "Modern Warfare 3, CoD: MW3")));
		games.put("FIFA 14", new HashSet<>(Arrays.asList("FIFA 14")));
		games.put("Call of Duty: Black Ops II", new HashSet<>(Arrays.asList("Black Ops 2", "Black Ops II", "BO2")));
		games.put("FIFA 13", new HashSet<>(Arrays.asList("FIFA 13")));
		games.put("Call of Duty: Ghosts", new HashSet<>(Arrays.asList("Call of Duty: Ghosts", "CoD: Ghosts")));
		games.put("FIFA 12", new HashSet<>(Arrays.asList("FIFA 12")));
		games.put("Call of Duty: Advanced Warfare", new HashSet<>(Arrays.asList("Call of Duty: Advanced Warfare",
				"Cod: AW", "CoD: Advanced Warfare", "Call of Duty: AW")));
		games.put("FIFA 16", new HashSet<>(Arrays.asList("FIFA 16")));
		games.put("The Elder Scrolls V: Skyrim", new HashSet<>(Arrays.asList("Skyrim", "TESV")));
		games.put("Minecraft", new HashSet<>(Arrays.asList("Minecraft")));
		games.put("Battlefield 3", new HashSet<>(Arrays.asList("Battlefield 3", "BF3")));
		games.put("Call of Duty: Black Ops 3", new HashSet<>(Arrays.asList("Black Ops 3", "BO3")));
		games.put("Battlefield 4", new HashSet<>(Arrays.asList("Battlefield 4", "BF4")));
		games.put("Assassin's Creed IV: Black Flag",
				new HashSet<>(Arrays.asList("Assassin's Creed IV", "Assassin's Creed 5", "AC IV", "AC 4")));
		games.put("Assassin's Creed III",
				new HashSet<>(Arrays.asList("Assassin's Creed III", "Assassin's Creed 3", "AC III", "AC 3")));
		games.put("Assassin's Creed: Revelations",
				new HashSet<>(Arrays.asList("Assassin's Creed: Revelations", "AC: Revelations")));
		games.put("Diablo III", new HashSet<>(Arrays.asList("Diablo III", "Diablo 3")));
		games.put("Far Cry 4", new HashSet<>(Arrays.asList("Far Cry 4", "Far Cry IV", "FC 4", "FC IV")));
		// Clean search tags by our protocol
		for (Map.Entry<String, Set<String>> entry : games.entrySet()) {
			Set<String> cleaned = new HashSet<>();
			for (String tag : entry.getValue()) {
				cleaned.add(clean(tag));
			}
			entry.setValue(cleaned);
		}
	}

	public static String clean(String input) {
		return input.toLowerCase().replaceAll("[^a-z1-9]", "");
	}
}
