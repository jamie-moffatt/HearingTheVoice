<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>Questions</title>
	<link rel="stylesheet" type="text/css" href="question.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
	<script src="js/question.js"></script>
</head>
</body>
<div id="form">
	<form action="">
		<div>
		<input type="button" id="previousQuestion" value="Previous">
		<input type="button" id="nextQuestion" value="Next">
		</div>
		<select name="type">
			<option value="boolean">Yes/No</option>
			<option value="radio">Multiple Choice</option>
			<option value="dropdown">List</option>
			<option value="checkbox">Checkboxes</option>
			<option value="textbox">Text</option>
		</select>
		<br>
		Description
		<br>
		<input type="text" id="inputDescription" name="description">
		<div id="input-choices">
		</div>
		<input type="button" id="cmdQuestion" value="Submit">
	</form>
</div>
<?php
// CHANGE TO PREPARED STATEMENTS

// make a mysql connection
//$mysqli = new mysqli("mysql.dur.ac.uk", "cwwp64", "H3ather!", "Pcwwp64_Voice");
$mysqli = new mysqli("127.0.0.1", "root", "", "Voice");
if ($mysqli->connect_errno)
{
	die("Failed to connect to MySQL: " . $mysqli->connect_error);
}

$sections = array();

// list the sections
if ($result = $mysqli->query("SELECT * FROM `Section`"))
{
	print('<div id="sections">'."\n");
	while ($row = $result->fetch_assoc())
	{
		print('<div class="section">'."\n");
		printf('<h2>%s</h2><p>%s</p>'."\n", $row['name'], $row['description']);
		printQuestions($mysqli, $row['sectionID']);
		printChoices($mysqli, $row['sectionID']);
		print('</div>'."\n");
	}
	$result->free();
	print('</div>'."\n");
}	
$mysqli->close();

function printQuestions($mysqli, $sectionID)
{
	if ($result = $mysqli->query("SELECT * FROM `Question` WHERE `sectionID`=$sectionID ORDER BY `number`"))
	{
		while ($row = $result->fetch_assoc())
		{
			print('<div class="question">'."\n");
			printf('<h3>Question %d</h3><p>%s</p>'."\n", $row['number'], $row['description']);
			print('</div>'."\n");
		}
		$result->free();
	}	
}

function printChoices($mysqli, $sectionID)
{
	if ($result = $mysqli->query("SELECT * FROM `Choice` WHERE `sectionID`=$sectionID ORDER BY `value`"))
	{
		print('<h2>Responses</h2>'."\n");
		print('<ul class="choices">'."\n");
		while ($row = $result->fetch_assoc())
		{
			printf('<li>%s (%s)</li>'."\n", $row['text'], $row['value']);
		}
		$result->free();
		print('</ul>'."\n");
	}	
}
?>
<div id="iphone">
	<div id="screen">
		<div id="status">
		</div>
		<div id="navigation">
			<div id="back-button">Back</div>
			<div id="screen-title">Section 1</div>
			<div id="done-button">Done</div>
		</div>
		<div id="tableview">
			<div class="section-header">Question 1</div>
			<div class="section" id="description">
				The question will appear here.
			</div>
			<ul id="choices">
				<li>Choices will appear here.</li>
			</ul>
		</div>
	</div>
</div>
</body>
</html>