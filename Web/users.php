<?php
/*
EXAMPLE XML POST

<user dob="1992-05-09" gender="MALE" />
*/

$post_data = file_get_contents('php://input');

if ($_SERVER['REQUEST_METHOD'] === "GET")
{
	if ($_GET['user'])
	{
		$user_id = $_GET['user'];

		// read database configuration
		$config = parse_ini_file("dbconfig.ini");

		// make a mysql connection
		$mysqli = new mysqli($config['host'], $config['user'], $config['password'], $config['database']);
		if ($mysqli->connect_errno)
		{
			die("Failed to connect to MySQL: " . $mysqli->connect_error);
		}

		// list the sections
		if ($result = $mysqli->query("SELECT * FROM `User` WHERE `userID` = '$user_id'"))
		{
			if ($row = $result->fetch_assoc())
			{
				// set content type to xml
				header('Content-type: text/xml');
				header('Charset: utf-8');
				printf('<user id="%d" dob="%s" gender="%s" averageResponseTime="%s" />'."\n", $row['userID'], $row['DOB'], $row['gender'], getAverageResponseTime($mysqli, $user_id));
			}
			else
			{
				printf("User with id (%d) does not exist.\n", $user_id);
			}
			$result->free();
		}	
		else
		{
			print("Error retrieving data from MySQL: " . $mysqli->error . "\n");
		}
		$mysqli->close();
	}
	else
	{
		print("No User ID Given.");
	}
}
elseif ($_SERVER['REQUEST_METHOD'] === "POST")
{
	if ($post_data)
	{
		// read database configuration
		$config = parse_ini_file("dbconfig.ini");

		// make a mysql connection
		$mysqli = new mysqli($config['host'], $config['user'], $config['password'], $config['database']);
		if ($mysqli->connect_errno)
		{
			die("Failed to connect to MySQL: " . $mysqli->connect_error);
		}
		
		// create xml object
		$xml = simplexml_load_string($post_data);
		
		$user_code = mysqli_real_escape_string($mysqli, $xml['code']);
		$age = mysqli_real_escape_string($mysqli, $xml['age']);
		$gender = mysqli_real_escape_string($mysqli, $xml['gender']);
		
		if ($mysqli->query("INSERT INTO `User` (`code`, `age`, `gender`) VALUES ('$user_code', '$age', '$gender')"))
		{
			print($mysqli->insert_id);
		}
		else
		{
			print("Error inserting data into MySQL: " . $mysqli->error . "\n");
		}
		
		$mysqli->close();
	}
	else
	{
		header('HTTP/1.1 400 Bad Request', true, 400);
		print("No Data Received.");
	}
}
else
{
	header('HTTP/1.1 405 Method Not Allowed', true, 405);
	print("Method Not Allowed.");
}

function getNumberOfSubmissions($mysqli, $userID)
{
	// might be better to do this client side
}

function getAverageResponseTime($mysqli, $userID)
{
	if ($result = $mysqli->query("SELECT `notificationTime`, `submissionTime` FROM `Answer` WHERE `userID` = $userID GROUP BY `scheduleID`"))
	{
		$timeArray = array();
		// calculate average time interval between notifications and submissions
		while($row = $result->fetch_assoc())
		{
			$notificationDateTime = new DateTime($row['notificationTime']);
			$submissionDateTime = new DateTime($row['submissionTime']);
			$responseTimeInSeconds = $submissionDateTime->format('U') - $notificationDateTime->format('U');
			$timeArray[] = $responseTimeInSeconds;
		}
		$result->free();
		
		return array_sum($timeArray) / count($timeArray);
	}
	else
	{
		return "UNKNOWN";
	}
}

?>