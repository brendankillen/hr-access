$(document).ready(function() 
{
	$('#a.nav-link').on('click', function () 
	{		
		//$('#a.nav-link').find( 'li.active' ).removeClass( 'active' );
		$(this).parent( 'li' ).addClass( 'active' );
	});
});