<?php
if ($_GET['format'] === "csv")
{
	header('Content-Disposition: attachment; filename="responses.csv"');
	header('Content-type: text/csv');
}
else
{
	print("<!DOCTYPE html>");
	print("<head>");
	print("<title>Inner Life</title>");
	print('<meta charset="UTF-8">');
	print('<link rel="stylesheet" type="text/css" href="output.css">');
	print('<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>');
	print('<script src="js/output.js"></script>');
	print('</head>');
	print('<body>');
}

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

if ($result = $mysqli->query("SELECT * FROM `Schedule` WHERE scheduleID < 29 ORDER BY `order`"))
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

if ($_GET['format'] !== "csv")
{
	print('<table>');
	
	print('<tr><td style="font-weight: bold;">Session</td>');
	foreach ($sections as $scheduleID => $sectionArray)
	{
		printf('<td colspan="%d">%d</td>', getNumberOfQuestionsInSession($mysqli, $scheduleID), $scheduleID);
	}
	print('</tr>');
	
	print('<tr><td style="font-weight: bold;">Section</td>');
	foreach ($sections as $scheduleID => $sectionArray)
	{
		for ($i = 0; $i < count($sectionArray); ++$i)
		{
			printf('<td colspan="%d">%d</td>', count($questions[$sectionArray[$i]]), $sectionArray[$i]);
		}
	}
	print('</tr>');
}

if ($_GET['format'] === "csv")
	print("Participant");
else
	print("<tr><th>Participant</th>");

foreach ($sections as $scheduleID => $sectionArray)
{
	foreach ($sectionArray as $section)
	{
		foreach ($questions[$section] as $questionID)
		{
			if ($_GET['format'] === "csv")
				printf(',S%d%s%d', $scheduleID, $sectionCodes[$section], getQuestionNumber($mysqli, $questionID));
			else
				printf('<th class="%s" data-section-id="%d" data-section-name="%s" data-question-id="%d">S%d%s%d</th>', $sectionCodes[$section], $section, getSectionName($mysqli, $section), $questionID, $scheduleID, $sectionCodes[$section], getQuestionNumber($mysqli, $questionID));
		}
		
	}
}

if ($_GET['format'] === "csv")
	print("\n");
else
	print("</tr>");

foreach (getUsers($mysqli) as $user)
{
	if ($_GET['format'] === "csv")
		printf("%d : %s", $user, getUserCode($mysqli, $user));
	else
		printf("<tr><td>%d : %s</td>", $user, getUserCode($mysqli, $user));
	foreach ($sections as $scheduleID => $sectionArray)
	{
		foreach ($sectionArray as $section)
		{
			foreach ($questions[$section] as $questionID)
			{
				if ($_GET['format'] === "csv")
					printf(',%s', getResponse($mysqli, $user, $scheduleID, $questionID));
				else
					printf('<td class="%s">%s</td>', $sectionCodes[$section], getResponse($mysqli, $user, $scheduleID, $questionID));
			}
		}
	}
	if ($_GET['format'] === "csv")
		print("\n");
	else
		print("</tr>");
}

if ($_GET['format'] !== "csv")
	print("</table>");
	
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

function getUserCode($mysqli, $userID)
{
	if ($result = $mysqli->query("SELECT `code` FROM `User` WHERE `userID` = $userID"))
	{
		while ($row = $result->fetch_assoc())
		{
			$userCode = $row['code'];
		}
		$result->free();
	}
	return $userCode;
}

function getSectionName($mysqli, $sectionID)
{
	if ($result = $mysqli->query("SELECT `name` FROM `Section` WHERE `sectionID` = $sectionID"))
	{
		while ($row = $result->fetch_assoc())
		{
			$sectionName = $row['name'];
		}
		$result->free();
	}
	return $sectionName;
}

function getNumberOfQuestionsInSession($mysqli, $scheduleID)
{
	if ($result = $mysqli->query("SELECT COUNT(`questionID`) AS `count` FROM `SectionSchedule`, `Question`, `Section` WHERE `scheduleID` = $scheduleID AND `Question`.`sectionID` = `Section`.`sectionID` AND `Section`.`sectionID` = `SectionSchedule`.`sectionID`"))
	{
		while ($row = $result->fetch_assoc())
		{
			$count = $row['count'];
		}
		$result->free();
	}
	return $count;
}

if ($_GET['format'] !== "csv")
	print('</body>');
?>