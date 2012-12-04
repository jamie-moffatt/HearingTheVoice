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

if ($result = $mysqli->query("SELECT * FROM `Schedule` ORDER BY `order`"))
{
	print('<schedule>'."\n");
	while ($row = $result->fetch_assoc())
	{
		printf('<session id="%d">'."\n", $row['scheduleID']);
		printSections($mysqli, $row['scheduleID']);
		print('</session>'."\n");
	}
	$result->free();
	print('</schedule>'."\n");
}	
$mysqli->close();

function printSections($mysqli, $scheduleID)
{
	if ($result = $mysqli->query("SELECT `Section`.`sectionID`, `Section`.`name`, `Section`.`description` FROM `Section`, `SectionSchedule` WHERE `Section`.`sectionID` = `SectionSchedule`.`sectionID` AND `SectionSchedule`.`scheduleID` = $scheduleID;"))
	{
		while ($row = $result->fetch_assoc())
		{
			printf('<section id="%d" name="%s" description="%s" />'."\n", $row['sectionID'], $row['name'], $row['description']);
		}
		$result->free();
	}	
}
?>