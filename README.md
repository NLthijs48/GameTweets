### GameTweets

This repository contains the methods used in Twitter data research. The research investigates if the number of tweets about a certain video game is linked to the number of sold copies in Europe. The used data is provided by [Twiqs.nl](www.twiqs.nl), and contains most of the tweets from The Netherlands from late 2010 until late 2015. The tweets are mostly in Dutch, because that is the target language Twiqs is collecting.

### Results

View the graphs made during this research on [this page](http://wiefferink.me/GameTweets).


### How to run it yourself

This repository contains a project folder named MapReduce. This folder contains the Maven project that is required to perform the MapReduce job on the data set of tweets. In order to perform this job yourself, please follow these steps:

- Copy the project folder to the cluster (for example, zip the project, run scp to move it to the cluster and then unzip it).
- Navigate to the project folder's (the folder containing a file called pom.xml) and run mvn package.
- After this, you may run the MapReduce job by navigating into the "target"-folder and executing the following command: hadoop jar bigdata-0.2.jar gametweets.GameTweets (path name(s)) (name of output folder) (so, if you wish to perform the job on the months September and October in the year 2013, use: /data/twitterNL/201309/* /data/twitterNL/201310/*).
- The result of the job will end up in (name of output folder) on the HDFS.