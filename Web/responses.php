<?php
/*
EXAMPLE XML POST

<submission userID="1" scheduleID="1" notificationTime="yyyy-MM-dd HH:mm:ss" submissionTime="yyyy-MM-dd HH:mm:ss">
	<response questionID="1" response="2" />
	<response questionID="2" response="5" />
</submission>
*/

$post_data = file_get_contents('php://input');

if ($_SERVER['REQUEST_METHOD'] === "POST")
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
		
		$stmt = $mysqli->prepare("INSERT INTO `Answer` (`userID`, `scheduleID`, `notificationTime`, `submissionTime`, `questionID`, `response`) VALUES (?, ?, ?, ?, ?, ?)");
		$stmt->bind_param('iissii', $userID, $sessionID, $notificationTime, $submissionTime, $questionID, $response);
		
		$userID = mysqli_real_escape_string($mysqli, $xml['userID']);
		$sessionID = mysqli_real_escape_string($mysqli, $xml['sessionID']);
		$notificationTime = mysqli_real_escape_string($mysqli, $xml['notificationTime']);
		$submissionTime = mysqli_real_escape_string($mysqli, $xml['submissionTime']);
		
		foreach ($xml->response as $responseXML)
		{
			$questionID = mysqli_real_escape_string($mysqli, $responseXML['questionID']);
			$response = mysqli_real_escape_string($mysqli, $responseXML['response']);
			
			if ($stmt->execute())
			{
				printf("%d Row inserted.\n", $stmt->affected_rows);
			}
			else
			{
				print("Error inserting data into MySQL: " . $mysqli->error . "\n");
			}
		}
		
		$stmt->close();
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

?>