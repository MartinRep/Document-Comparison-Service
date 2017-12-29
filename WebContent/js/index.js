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
			console.log("Listeners active..");
			$("#title").keypress(function() {Validate();});
			$("#file").change(function() {Validate();});
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
		