<?php
// CHANGE TO PREPARED STATEMENTS

// set content type to xml
header('Content-type: text/xml');
header('Charset: utf-8');

// read database configuration
$config = parse_ini_file("dbconfig.ini");

// make a mysql connection
$mysqli = new mysqli($config['host'], $config['user'], $config['password'], $config['database']);
if ($mysqli->connect_errno)
{
	die("Failed to connect to MySQL: " . $mysqli->connect_error);
}

// list the sections
if ($result = $mysqli->query("SELECT * FROM `Section`"))
{
	print('<sections>'."\n");
	while ($row = $result->fetch_assoc())
	{
		printf('<section id="%d" name="%s" description="%s">'."\n", $row['sectionID'], $row['name'], htmlspecialchars($row['description']));
		printQuestions($mysqli, $row['sectionID']);
		printChoices($mysqli, $row['sectionID']);
		print('</section>'."\n");
	}
	$result->free();
	print('</sections>'."\n");
}	
$mysqli->close();

function printQuestions($mysqli, $sectionID)
{
	if ($result = $mysqli->query("SELECT * FROM `Question` WHERE `sectionID`=$sectionID ORDER BY `number`"))
	{
		print('<questions>'."\n");
		while ($row = $result->fetch_assoc())
		{
			printf('<question id="%d" number="%d" description="%s" type="%s"/>'."\n", $row['questionID'], $row['number'], $row['description'], $row['type']);
		}
		$result->free();
		print('</questions>'."\n");
	}	
}

function printChoices($mysqli, $sectionID)
{
	if ($result = $mysqli->query("SELECT * FROM `Choice` WHERE `sectionID`=$sectionID ORDER BY `value`"))
	{
		print('<choices>'."\n");
		while ($row = $result->fetch_assoc())
		{
			printf('<choice id="%d" text="%s" value="%s"/>'."\n", $row['choiceID'], $row['text'], $row['value']);
		}
		$result->free();
		print('</choices>'."\n");
	}	
}
?>