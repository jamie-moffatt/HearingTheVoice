<?php

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

		print("\n" . $post_data . "\n");

		// create xml object
		$xml = simplexml_load_string($post_data);

		$user_ID = mysqli_real_escape_string($mysqli, $xml['id']);
		$use_data_permission = mysqli_real_escape_string($mysqli, $xml['useDataPermission']);

		if ($use_data_permission == "true")
		{
			$use_data_permission = 1;
		}
		else
		{
			$use_data_permission = 0;
		}
		
		print("UPDATE `User` SET `dataUsePermission`=$use_data_permission WHERE `userID` = $user_ID");

		if ($mysqli->query("UPDATE `User` SET `dataUsePermission`=$use_data_permission WHERE `userID` = $user_ID"))
		{
			print("Success.");
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
?>