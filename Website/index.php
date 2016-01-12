<!DOCTYPE html>
<html>
	<head>
		<title>Managing Big Data: GameTweets</title>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
		<style>
			body {
				padding-top: 50px;
			}
			.games {
				margin: 30px 2% 200px 2%;
			}
			.gameContainer {
				display: flex;
				padding-bottom: 50px;
			}
			.game {
				flex-shrink: 1;
				flex-grow: 1;
				margin: 0 0 75px 0;
			}
			.left{
				flex-grow: 0;
				flex-shrink: 0;
				width: 300px;
				height: 400px;
				margin: 0 20px 50px 0;
			}
			.cover {
				width: 300px;
				height: 350px;
				background-size: contain;
				background-repeat: no-repeat;
			}
			.release {

			}
			@media all and (max-width: 800px) {
				.cover, .left {
					width: 200px;
				}
			}
			@media all and (max-width: 500px) {
				.cover, .left {
					width: 150px;
				}
			}
		</style>
	</head>
	<body>
		<div class="navbar navbar-inverse navbar-fixed-top">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand active" href="#">Managing Big Data: GameTweets</a>
				</div>
				<div class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li class="active"><a href="#">Game stats</a></li>
					</ul>
					<ul class="nav navbar-nav">
						<li><a href="https://github.com/NLthijs48/GameTweets">Code on Github</a></li>
					</ul>
				</div>
			</div>
		</div>

		<div class="games">

		</div>

		<script src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
		<script src="https://code.highcharts.com/stock/highstock.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
		<script>
			<?php include('data.php') ?>
			var releaseDates = {
					"Grand Theft Auto V":["17-9-2013 (PS3, X360)", "18-11-2014 (PS4, XOne)", "14-04-2015 (PC)"],
					"FIFA 15":["25-09-2014"],
					"Call of Duty: Modern Warfare 3":["08-11-2011"],
					"FIFA 14":["27-09-2013"],
					"Call of Duty: Black Ops II":["13-11-2012 (X360, PS3, PC)", "30-11-2012 (WiiU)"],
					"FIFA Soccer 13":["28-09-2012 (X360, PS3, Wii, 3DS, PSP, PC, PSV)", "30-11-2012 (WiiU)"],
					"Call of Duty: Ghosts":["05-11-2013 (X360, PS3, PC, WiiU)", "29-11-2013 (PS4)", "22-11-2013 (XOne)"],
					"FIFA 12":["29-09-2011"],
					"Call of Duty: Advanced Warfare":["04-11-2014"],
					"FIFA 16":["24-09-2015"],
					"The Elder Scrolls V: Skyrim":["11-11-2011"],
					"Minecraft":["18-11-2011 (PC)", "09-05-2012 (X360)", "17-12-2013 (PS3)", "03-09-2014 (PS4)", "05-09-2014 (XOne)", "15-10-2014 (PSV)"],
					"Battlefield 3":["27-10-2011"],
					"Call of Duty: Black Ops 3":["06-11-2015"],
					"Battlefield 4":["01-11-2013 (PC, PS3, X360)", "22-11-2013 (XOne)", "29-11-2013 (PS4)"],
					"Assassin's Creed IV: Black Flag":["22-11-2013 (XOne, PS4, PC, WiiU)", "29-11-2013 (PS3, X360)"],
					"Assassin's Creed III":["31-10-2012 (PS3, X360)", "23-11-2012 (PC)", "30-11-2012 (WiiU)"],
					"Assassin's Creed: Revelations":["15-11-2011 (PS3, X360)", "02-12-2011 (PC)"],
					"Diablo III":["15-05-2012 (PC)", "03-09-2013 (PS3, X360)", "19-08-2014 (PS4, XOne)"],
					"Far Cry 4":["18-11-2014"]
				};
			console.log(stats);
			$(function() {
				var count = 0;
				for(var gameKey in stats) {
					var releaseString = "<ul>";
					for(key in releaseDates[gameKey]) {
						releaseString+= "<li>"+releaseDates[gameKey][key]+"</li>";
					}
					releaseString += "</ul>";
					$(".games").append("<div class='gameContainer'><div class='left'><div class='cover' style='background-image: url(\"images/cover_"+gameKey.replace(/[^a-zA-Z1-9]/gi, "")+".jpg\");'></div><div class='release'>"+releaseString+"</div></div><div class='game game"+count+"'></div>"/*<div class='sum"+gameKey.replace(/[^a-zA-Z1-9]/gi, "")+"'></div>*/+"</div>");
					var localCount = count;
					$('.game'+count).highcharts('StockChart', {
						rangeSelector: {
							buttons: [{
									type: 'all',
									text: 'All'
								},{
									type: 'year',
									count: 1,
									text: '1Y'
								}, {
									type: 'month',
									count: 1,
									text: '1M'
								}, {
									type: 'week',
									count: 1,
									text: '1W'
								}],
							selected: 0
						},
						/* Count sum of selected part
						xAxis: {
							ordinal:false,
							events: {
								afterSetExtremes: function(e) {
									console.log(e);
									var sum = 0.0,
										chartOb = this;
									console.log("data: ", chartOb.series[0]);
									$.each(chartOb.series[0].data,function(i,point){
										//console.log(point);
										if(point !== undefined && point.isInside)
											sum += point.y;
									});
									console.log("set extremes count="+localCount+", sum="+sum);
									$('.sum'+e.target.chart.title.textStr.replace(/[^a-zA-Z1-9]/gi, "")).html('Sum: '+sum);
								}
							}
						},*/
						title: {
							text: gameKey
						},
						scrollbar : {
							enabled : false
						},
						series: [{
								name: 'Tweet Count',
								data: stats[gameKey],
								tooltip: {
									valueDecimals: 0
								}
							}]
					});
					count++;
				}
			});
		</script>
	</body>
</html>
