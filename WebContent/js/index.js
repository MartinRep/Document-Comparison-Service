/**
 * 
 */

$(document).ready(function() {Init();});
		
jQuery.fn.existsWithValue = function() 
{ 
    return this.length && this.val().length; 
}

function Init()
{
	$("#title").keypress(function() {Validate();});
	$("#file").change(function() {Validate();});
	console.log("Listeners active..");
}
	
function Validate()
{
	if ($("#title").existsWithValue() && $("#file").existsWithValue()) 
	{
		$("#submitBt").removeAttr("disabled");
		console.log("Btn Enabled");
		return true;
	}
	else 
	{
		$("#submitBt").attr("disabled","True");
		console.log("Btn Disabled");
		return false;
	}
}
