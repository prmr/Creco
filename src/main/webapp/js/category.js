if(!$('.eq-result-entry').length)
{
	showEmptyMessage();
}

function showEmptyMessage() {
	$('#has-result-message').css('display', 'none');
	$('#no-result-message').css('display', 'block');
}