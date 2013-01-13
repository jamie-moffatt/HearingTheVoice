<?php
// read database configuration
$config = parse_ini_file("dbconfig.ini");

// make a mysql connection
$mysqli = new mysqli($config['host'], $config['user'], $config['password'], $config['database']);
if ($mysqli->connect_errno)
{
	die("Failed to connect to MySQL: " . $mysqli->connect_error);
}

// get schedule IDs
// get sections in that schedule and the section code
// get get questions per section

$samples = array();
$sections = array();
$questions = array();
$sectionCodes = getSectionCodes($mysqli);

if ($result = $mysqli->query("SELECT * FROM `Schedule` ORDER BY `order`"))
{
	while ($row = $result->fetch_assoc())
	{
		$samples[] = $row['scheduleID'];
	}
	$result->free();
}

foreach ($samples as $sample)
{
	$sections[$sample] = getSections($mysqli, $sample);
}
	
foreach ($sections as $scheduleID => $sectionArray)
{
	foreach ($sectionArray as $section)
	{
		print("$section\n");
		$questions = getQuestions($mysqli, $section);
		foreach ($questions as $questionID)
		{
			printf("S%d%s%d  ", $scheduleID, $sectionCodes[$section], $questionID);
		}
	}
}
	
$mysqli->close();

function getSections($mysqli, $scheduleID)
{
	$sections = array();
	if ($result = $mysqli->query("SELECT `Section`.`sectionID` FROM `Section`, `SectionSchedule` WHERE `Section`.`sectionID` = `SectionSchedule`.`sectionID` AND `SectionSchedule`.`scheduleID` = $scheduleID"))
	{
		while ($row = $result->fetch_assoc())
		{
			$sections[] = $row['sectionID'];
		}
		$result->free();
	}
	return $sections;
}

function getSectionCodes($mysqli)
{
	$sectionCodes = array();
	if ($result = $mysqli->query("SELECT * FROM `Section`"))
	{
		while ($row = $result->fetch_assoc())
		{
			$sectionCodes[$row['sectionID']] = $row['code'];
		}
		$result->free();
	}
	return $sectionCodes;
}

function getQuestions($mysqli, $sectionID)
{
	$questions = array();
	if ($result = $mysqli->query("SELECT * FROM `Question` WHERE `sectionID`=$sectionID ORDER BY `number`"))
	{
		while ($row = $result->fetch_assoc())
		{
			$questions[] = $row['number'];
		}
		$result->free();
	}	
	return $questions;
}

?>