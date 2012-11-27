<?php
	header('Content-type: text/xml');

	require_once '../Twig/lib/Twig/Autoloader.php';
	Twig_Autoloader::register();

	$twig_loader = new Twig_Loader_Filesystem('./templates');
	$twig_options = array('cache' => './tmp/cache');
	$twig = new Twig_Environment($twig_loader, $twig_options);
	
	$template = $twig->loadTemplate('questions.xml');
	
	$params = array(
		'sections' => array(
			array(
				'sectionID' => 1,
				'questions' => array(
					array(
						'questionID' => 1,
						'type' => 'RADIO',
						'description' => 'This is a question?',
						'choices' => array(
							array('value' => 'Agree','order' => 1),
							array('value' => 'Unsure','order' => 2),
							array('value' => 'Disagree','order' => 3)
						)
					)
				)
			)
		)
	);
	
	
	$template->display($params);
?>