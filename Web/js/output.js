$(document).ready(function() {
	$('th').mouseenter(function(e) {
		if ($(this).text() == 'Participant') return;
		var adStr = "";
		if (e.altKey)
		{
			adStr = "<br /><br />SectionID: " + $(this).data('sectionId') + "<br />QuestionID: " + $(this).data('questionId');
		}
		$('table').after('<div class="tooltip" style="top:' + (e.pageY + 10) + 'px; left:' + (e.pageX + 10) + 'px;">' + $(this).data('sectionName') + adStr + '</div>');
	});
	$('th').mouseleave(function(e) {
		$('.tooltip').remove();
	});
});