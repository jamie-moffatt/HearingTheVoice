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

header('Content-Disposition: attachment; filename="responses.csv"');
header('Content-type: text/csv');

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
		$questions[$section] = getQuestions($mysqli, $section);
	}
}

foreach ($sections as $scheduleID => $sectionArray)
{
	foreach ($sectionArray as $section)
	{
		foreach ($questions[$section] as $questionID)
		{
			printf(",S%d%s%d", $scheduleID, $sectionCodes[$section], getQuestionNumber($mysqli, $questionID));
		}
		
	}
}

foreach (getUsers($mysqli) as $user)
{
	printf("\n%d", $user);
	foreach ($sections as $scheduleID => $sectionArray)
	{
		foreach ($sectionArray as $section)
		{
			foreach ($questions[$section] as $questionID)
			{
				printf(",%s", getResponse($mysqli, $user, $scheduleID, $questionID));
			}
		}
	}
}
	
$mysqli->close();

function getSections($mysqli, $scheduleID)
{
	$sections = array();
	if ($result = $mysqli->query("SELECT `Section`.`sectionID` FROM `Section`, `SectionSchedule` WHERE `Section`.`sectionID` = `SectionSchedule`.`sectionID` AND `SectionSchedule`.`scheduleID` = $scheduleID ORDER BY `Section`.`code`"))
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
			$questions[] = $row['questionID'];
		}
		$result->free();
	}	
	return $questions;
}

function getUsers($mysqli)
{
	$users = array();
	if ($result = $mysqli->query("SELECT userID FROM `User`"))
	{
		while ($row = $result->fetch_assoc())
		{
			$users[] = $row['userID'];
		}
		$result->free();
	}	
	return $users;
}

function getResponse($mysqli, $user, $scheduleID, $questionID)
{
	if ($result = $mysqli->query("SELECT response FROM `Answer` WHERE userID=$user AND scheduleID=$scheduleID AND questionID=$questionID"))
	{
		while ($row = $result->fetch_assoc())
		{
			$response = $row['response'];
		}
		$result->free();
	}	
	return $response;
}

function getQuestionNumber($mysqli, $questionID)
{
	if ($result = $mysqli->query("SELECT `number` FROM `Question` WHERE questionID=$questionID"))
	{
		while ($row = $result->fetch_assoc())
		{
			$response = $row['number'];
		}
		$result->free();
	}	
	return $response;
}

?>