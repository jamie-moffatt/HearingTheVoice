<?php
/*
EXAMPLE XML POST

<user dob="1992-05-09" gender="MALE" />
*/

$post_data = file_get_contents('php://input');

if ($_SERVER['REQUEST_METHOD'] === "GET")
{
	print("No User ID Given.");
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
		
		$dob = mysqli_real_escape_string($mysqli, $xml['dob']);
		$gender = mysqli_real_escape_string($mysqli, $xml['gender']);
		
		if ($mysqli->query("INSERT INTO `User` (`DOB`, `gender`) VALUES ('$dob', '$gender')"))
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

?>