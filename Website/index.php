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
			}
			.game {
				flex-shrink: 1;
				flex-grow: 1;
				margin: 0 0 50px 0;
			}
			.cover {
				flex-grow: 0;
				flex-shrink: 0;
				width: 300px;
				height: 400px;
				background-size: contain;
				background-repeat: no-repeat;
				margin: 0 20px 50px 0;
			}
			@media all and (max-width: 800px) {
				.cover {
					width: 200px;
				}
			}
			@media all and (max-width: 500px) {
				.cover {
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
			console.log(stats);
			$(function() {
				var count = 0;
				for(var gameKey in stats) {
					$(".games").append("<div class='gameContainer'><div class='cover' style='background-image: url(\"images/cover_"+gameKey.replace(/[^a-zA-Z1-9]/gi, "")+".jpg\");'></div><div class='game game"+count+"'></div></div>");
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
						title: {
							text: gameKey
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
