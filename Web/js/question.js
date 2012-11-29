currentQuestion = 1;

$(document).ready(function()
{
	$('#form select').change(function() 
	{
		var selection = $('#form select').val();
		var choices = $('#input-choices');
		if (selection === 'radio' || selection === 'dropdown' || selection === 'checkbox')
		{
			choices.html("<ul>");
			for (i = 0; i < 5; i++)
			{
				choices.append('<li><input class="choice-entry" type="text" name="question"></li>');
			}
			choices.append('</ul>');
		}
		else
		{
			choices.html('');	
		}
	});
	
	$('#cmdQuestion').click(function()
	{
		$('#iphone #description').text($('#inputDescription').val());
		var type = $('#form select').val();
		var choices = $('#tableview #choices');
		choices.html("");
		switch (type)
		{
			case 'boolean':
				choices.html('<li>Yes <input type="radio" name="response" value="yes"></li><li>No <input type="radio" name="response" value="no"></li>');
			break;
			case 'radio':
				$('.choice-entry').each(function()
				{
					if ($(this).val().length > 0)
					{
						choices.append('<li>' + $(this).val() + ' <input class="iphone-checkbox" type="radio" name="response" value="yes"></li>');
					}
				});
			break;
			case 'dropdown':
			
			break;
			case 'checkbox':
				$('.choice-entry').each(function()
				{
					if ($(this).val().length > 0)
					{
						choices.append('<li>' + $(this).val() + ' <input class="iphone-checkbox" type="checkbox" name="response" value="yes"></style></li>');
					}
				});
			break;
			case 'textbox' :
			
			break;
		}
	});
	
	$('#previousQuestion').click(function()
	{
		if (currentQuestion > 1)
		{
			currentQuestion--;
		}	
	});
	
	$('#nextQuestion').click(function()
	{
		currentQuestion++;
		$.ajax({
				type: "GET",
				url: "questions.php",
				dataType: "xml",
				success: function(xml)
				{
					$(xml).find('question').each(function()
					{
						alert($(this).attr('description'));
					}
					);
				}
		});
	});
});