<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/struts-conversation-tags" prefix="sc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="/simple-example/static/css/registration.css" />
<link rel="shortcut icon" href="/simple-example/static/img/strutsConvo.ICO" type="image/x-icon" /> 
<title>Contact Information</title>
</head>
<body>

	<s:property value="%{conversationError}"/>

	<h2>Please provide your contact information:</h2>

	<table>
		<tr>
			<td>
				<div id="contactFormDiv">
					<sc:form action="contact">
						<s:textfield label="First Name" key="contact.firstName" />
						<s:textfield label="Last Name" key="contact.lastName" />
						<s:textfield label="Address Line 1" key="contact.addressLine1" />
						<s:textfield label="Address Line 2" key="contact.addressLine2" />
						<s:textfield label="City" key="contact.city" />
						<s:textfield label="State" key="contact.state" />
						<s:textfield label="Postal Code" key="contact.postalCode" />
						<s:submit/>
					</sc:form>
				</div>
			</td>
		</tr>
	</table>
	
</body>
</html>