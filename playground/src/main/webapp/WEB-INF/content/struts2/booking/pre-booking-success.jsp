<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/struts-conversation-tags" prefix="sc"%>

<html>
<head>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script
	src="https://raw.github.com/cowboy/jquery-bbq/v1.2.1/jquery.ba-bbq.js"></script>

</head>
<body>

	<table>
		<s:iterator status="rowNumber" value="(3).{#this}">
			<tr>
				<s:iterator status="colNumber" value="(7).{#this}">
					<td>
						<div id="bookingDiv${rowNumber.count}${colNumber.count}">
							<sc:form action="begin-booking">
								<s:submit value="Begin Booking" />
							</sc:form>
						</div></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>

	<script>
	
	
		function bindDivSubmitButton(theDiv) {
		
			var theForm = theDiv.find('form');
			
			var theAnchor = theDiv.find('a');
			
			theAnchor.click(function(event) {
			
			    /* stop link from executing normally */
			    event.preventDefault(); 
			    
			    var url = theAnchor.attr( 'href' );
			    
			    /* Send the data using post and put the results in a div */
			    $.post( url,
			      function( data ) {
			          theDiv.html( data );
			          bindDivSubmitButton(theDiv);
			      }
			    );
		    
		  	});
			
			theForm.unbind();
			
			 /* attach a submit handler to the form */
		  	theForm.submit(function(event) {
		
			    /* stop form from submitting normally */
			    event.preventDefault(); 
			    
			    var url = theForm.attr( 'action' ),
			        formData = theForm.serialize();
			    
			    /* Send the data using post and put the results in a div */
			    $.post( url, formData,
			      function( data ) {
			          theDiv.html( data );
			          bindDivSubmitButton(theDiv);
			      }
			    );
		    
		  	});
		  
		}
		
		function bindShit() {
		
			var divList = $("div[id^='bookingDiv']");
			
			jQuery.each(divList, function() {
		      bindDivSubmitButton($(this));
		   	});
		}
		
		bindShit();
		
	</script>

</body>
</html>